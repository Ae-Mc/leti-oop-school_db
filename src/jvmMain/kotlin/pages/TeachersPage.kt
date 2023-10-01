package pages

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.onClick
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import entities.Teacher
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import widgets.TableCell

// List of teachers
@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun TeachersPage(database: Database, callback: () -> Unit) {
    // Used to close window
    var isOpen by remember { mutableStateOf(true) }

    var teachers by remember { mutableStateOf(emptyList<Teacher>()) }
    val weights = floatArrayOf(0.2f, 1.3f, 1f, 0.2f, 1f, 0.4f)
    val columnState = LazyListState()
    var showAddTeacherPage by remember { mutableStateOf(false) }
    var editingTeacher by remember { mutableStateOf<Teacher?>(null) }
    val teacher: Teacher? = editingTeacher

    teachers = transaction(database) {
        Teacher.all().with(Teacher::subjects, Teacher::classroomClasses)
            .toList()
    }

    if (isOpen) {
        Window(
            onCloseRequest = {
                isOpen = false
                callback()
            },
            state = WindowState(
                width = 1920.dp,
                height = 1080.dp,
                position = WindowPosition(Alignment.Center),
            ),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
            ) {
                // Table
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    state = columnState,
                    userScrollEnabled = true,
                ) {
                    // Here is the header
                    item {
                        Row(Modifier.background(Color.Gray)) {
                            TableCell(
                                text = "№",
                                weight = weights[0],
                            )
                            TableCell(text = "id", weight = weights[1])
                            TableCell(text = "ФИО", weight = weights[2])
                            TableCell(text = "Зарплата", weight = weights[3])
                            TableCell(text = "Предметы", weight = weights[4])
                            TableCell(text = "Классрук", weight = weights[5])
                        }
                    }
                    // Here are all the lines of your table.
                    this.itemsIndexed(
                        items = teachers,
                        contentType = { _, _ -> },
                        key = { _, teacher -> teacher.id },
                    ) { index, teacher ->
                        Row(
                            modifier = Modifier.onClick {
                                editingTeacher = teacher
                            },
                        ) {
                            TableCell(
                                text = index.toString(),
                                weight = weights[0]
                            )
                            TableCell(
                                text = teacher.id.toString(),
                                weight = weights[1],
                                textStyle = TextStyle(textDecoration = TextDecoration.Underline)
                            )
                            TableCell(
                                text = teacher.fullName,
                                weight = weights[2]
                            )
                            TableCell(
                                text = teacher.salary.toString(),
                                weight = weights[3]
                            )
                            TableCell(
                                text = teacher.subjects.joinToString(", ") { subject -> subject.name },
                                weight = weights[4]
                            )
                            TableCell(
                                text = teacher.classroomClasses.joinToString(", "),
                                weight = weights[5]
                            )
                        }

                    }
                }
                Box(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Button(
                        onClick = {
                            showAddTeacherPage = true
                        },
                    ) {
                        Text(
                            "Добавить учителя",
                        )
                    }
                }
            }
        }
    }

    if (showAddTeacherPage) {
        // Shows add teacher page
        AddTeacherPage(
            database,
            callback = {
                showAddTeacherPage = false
                teachers = transaction(database) {
                    Teacher.all()
                        .with(Teacher::subjects, Teacher::classroomClasses)
                        .toList()
                }
            },
        )
    } else if (teacher is Teacher) {
        // Shows edit teacher page
        EditTeacherPage(database = database, teacher = teacher, callback = {
            editingTeacher = null
            teachers = transaction(database) {
                Teacher.all().with(Teacher::subjects, Teacher::classroomClasses)
                    .toList()
            }
        })
    }
}