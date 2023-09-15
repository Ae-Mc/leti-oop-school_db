import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import entities.Class
import entities.Subject
import entities.Teacher
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

fun main() = application {
    val database = DatabaseFactory().getDatabase()
    transaction(database) {
        val teacher = Teacher.new {
            fullName = "Макурин Александр Евгеньевич"
            salary = 100000
        }
        Subject.new {
            name = "Математика"
            teachers = SizedCollection(listOf(teacher))
        }
        teacher.addSubject(Subject.new { name = "Программирование" })
        Class.new {
            classroomTeacher = teacher
            letter = "А"
            admissionYear = 2005
        }
    }
    transaction(database) {
        val teachers = Teacher.all().asIterable()
        for (teacher in teachers) {
            print(teacher)
        }
    }
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
