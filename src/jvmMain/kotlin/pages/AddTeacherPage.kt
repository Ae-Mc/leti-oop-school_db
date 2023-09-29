import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import org.jetbrains.exposed.sql.Database
import pages.CloseDialog

@Composable
fun AddTeacherPage(database: Database, callback: () -> Unit = {}) {
    var isOpen by remember { mutableStateOf(true) }
    var isAskingToClose by remember { mutableStateOf(false) }

    if (isOpen) {
        Window(
            onCloseRequest = { isAskingToClose = true },
            state = WindowState(
                width = 1200.dp,
                height = 720.dp,
                position = WindowPosition(Alignment.Center),
            ),
        ) {
            if (isAskingToClose) {
                CloseDialog(
                    title = "Отмена?",
                    text = "Вы уверены, что хотите отменить добавление учителя?",
                    confirmCallback = { isOpen = false },
                    cancelCallback = { isAskingToClose = false })
            }

            Column {
                Text("Ok")
                /*
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
            */
            }
        }
    } else {
        callback()
    }
}