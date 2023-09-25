import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import entities.Class
import entities.Subject
import entities.Teacher
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction

@Composable
@Preview
fun App(database: Database, teachers: List<Teacher>) {
    var text by remember { mutableStateOf(if (teachers.isEmpty()) "Create base db records" else "Base db records already created") }
    var teachersList by remember { mutableStateOf(teachers) }
    var isButtonEnabled by remember { mutableStateOf(teachers.isEmpty()) }

    MaterialTheme {
        Column(
            content = {
                Text(teachersList.joinToString(separator = "\n") { teacher: Teacher -> teacher.toString() })
                Button(onClick = {
                    isButtonEnabled = false
                    teachersList = transaction(database) {
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
            }
        )
    }
}

fun main() = application {
    val database = DatabaseFactory().getDatabase()
    val teachers = transaction(database) {
        Teacher.all().with(Teacher::classroomClasses, Teacher::subjects).toList()
    }
    Window(onCloseRequest = ::exitApplication) {
        App(database = database, teachers = teachers)
    }
}
