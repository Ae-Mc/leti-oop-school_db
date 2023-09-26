package pages

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import entities.Class
import entities.Subject
import entities.Teacher
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import widgets.TableCell

@Composable
@Preview
fun TeachersPage(database: Database) {
    var teachers by remember { mutableStateOf(emptyList<Teacher>()) }
    val weights = floatArrayOf(0.2f, 1.3f, 1f, 1f, 0.4f)
    val columnState = LazyListState()

    teachers = transaction(database) { Teacher.all().with(Teacher::subjects, Teacher::classroomClasses).toList() }

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                state = columnState,
                userScrollEnabled = true,
            ) {
                // Here is the header
                item {
                    Row(Modifier.background(Color.Gray)) {
                        TableCell(text = "№", weight = weights[0])
                        TableCell(text = "id", weight = weights[1])
                        TableCell(text = "ФИО", weight = weights[2])
                        TableCell(text = "Предметы", weight = weights[3])
                        TableCell(text = "Классрук", weight = weights[4])
                    }
                }
                // Here are all the lines of your table.
                this.itemsIndexed(
                    items = teachers,
                    contentType = { _, _ -> Unit },
                    key = { _, teacher -> teacher.id },
                ) { index, teacher ->
                    Row {
                        TableCell(text = index.toString(), weight = weights[0])
                        TableCell(text = teacher.id.toString(), weight = weights[1])
                        TableCell(text = teacher.fullName, weight = weights[2])
                        TableCell(
                            text = teacher.subjects.joinToString(", ") { subject -> subject.name },
                            weight = weights[3]
                        )
                        TableCell(
                            text = teacher.classroomClasses.joinToString(", "), weight = weights[4]
                        )
                    }

                }
            }
            Box(
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Button(
                    onClick = {
                        teachers = transaction(database) {
                            val teacher = Teacher.new {
                                fullName = "Макурин Александр Евгеньевич"
                                salary = 100000
                            }
                            Subject.new {
                                name = "Математика"
                                this.teachers = SizedCollection(listOf(teacher))
                            }
                            teacher.addSubject(Subject.new { name = "Программирование" })
                            Class.new {
                                classroomTeacher = teacher
                                letter = "А"
                                admissionYear = 2005
                            }
                            Teacher.all().with(Teacher::classroomClasses, Teacher::subjects).toList()
                        }
                    },
                ) {
                    Text("Добавить учителя", modifier = Modifier.clickable {
                        // TODO
                        Window(state = WindowState(width = 1600.dp, height = 900.dp)) {
                            Text("OK")
                        }
                    })
                }
            }
        }
    }
}