import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
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
fun App(database: Database, initialTeachers: List<Teacher>) {
    var text by remember { mutableStateOf(if (initialTeachers.isEmpty()) "Create base db records" else "Base db records already created") }
    var teachers by remember { mutableStateOf(initialTeachers) }
    var isButtonEnabled by remember { mutableStateOf(teachers.isEmpty()) }
    val weights = floatArrayOf(1.3f, 1f, 1f, 0.4f)

    teachers = transaction(database) { Teacher.all().with(Teacher::subjects, Teacher::classroomClasses).toList() }

    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), content = {
            LazyColumn(Modifier.fillMaxWidth()) {
                // Here is the header
                item {
                    Row(Modifier.background(Color.Gray)) {
                        TableCell(text = "id", weight = weights[0])
                        TableCell(text = "ФИО", weight = weights[1])
                        TableCell(text = "Предметы", weight = weights[2])
                        TableCell(text = "Классрук", weight = weights[3])
                    }
                }
                // Here are all the lines of your table.
                items(teachers.size * 10) {
                    val teacher = teachers[it % teachers.size]
                    Row(Modifier.fillMaxWidth()) {
                        TableCell(text = teacher.id.toString(), weight = weights[0])
                        TableCell(text = teacher.fullName, weight = weights[1])
                        TableCell(
                            text = teacher.subjects.joinToString(", ") { subject -> subject.name },
                            weight = weights[2]
                        )
                        TableCell(
                            text = teacher.classroomClasses.joinToString(", "),
                            weight = weights[3]
                        )
                    }
                }
            }
            Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                isButtonEnabled = false
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
                text = "Base db records created"
            }, enabled = isButtonEnabled) {
                Text(text)
            }
        })
    }
}

fun main() = application {
    val database = DatabaseFactory().getDatabase()
    val teachers = transaction(database) {
        Teacher.all().with(Teacher::classroomClasses, Teacher::subjects).toList()
    }
    val state = WindowState(width = 1920.dp, height = 1080.dp, position = WindowPosition.Aligned(Alignment.Center))
    Window(onCloseRequest = ::exitApplication, state = state) {
        App(database = database, initialTeachers = teachers)
    }
}
