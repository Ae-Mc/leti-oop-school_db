package pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import entities.Teacher
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import widgets.InputRow

@Composable
fun AddTeacherPage(database: Database, callback: () -> Unit = {}) {
    var isOpen by remember { mutableStateOf(true) }
    var isAskingToClose by remember { mutableStateOf(false) }

    var teacherFIO by remember { mutableStateOf("") }
    var teacherSalary by remember { mutableStateOf(0) }
    var isFioError by remember { mutableStateOf(true) }
    var isSalaryError by remember { mutableStateOf(true) }

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

            Column(
                modifier = Modifier.padding(all = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Добавление учителя",
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                InputRow(
                    "ФИО", teacherFIO, isError = isFioError,
                ) {
                    if (it.isBlank()) {
                        isFioError = true
                    } else {
                        teacherFIO = it
                        isFioError = false
                    }
                }
                InputRow(
                    "Заработная плата",
                    teacherSalary.toString(),
                    isError = isSalaryError,
                ) {
                    if (it.isBlank()) {
                        isSalaryError = true
                        teacherSalary = 0
                    } else if (it.toIntOrNull() == null) {
                        isSalaryError = true
                    } else {
                        teacherSalary = it.toInt()
                        isSalaryError = false
                    }
                }
                Button(
                    enabled = !(isSalaryError && isFioError),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        transaction(database) {
                            Teacher.new {
                                fullName = teacherFIO
                                salary = teacherSalary
                            }
                            isOpen = false
                        }
                    }
                )
                {
                    Text("Добавить")
                }
            }
        }
    } else {
        callback()
    }
}