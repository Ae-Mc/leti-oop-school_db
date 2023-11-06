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
import entities.Teacher
import exceptions.ValidationException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import widgets.InputRow

@Composable
fun AddTeacherPage(database: Database, callback: () -> Unit = {}) {
    var isOpen by remember { mutableStateOf(true) }

    var teacherFIO by remember { mutableStateOf("") }
    var teacherSalary by remember { mutableStateOf("0") }
    var errorText by remember { mutableStateOf<String?>(null) }

    if (isOpen) {
        Column(
            modifier = Modifier.padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                content = { Text("Назад") },
                onClick = {
                    isOpen = false
                    callback()
                }
            )
            Text(
                "Добавление учителя",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            InputRow(
                "ФИО", teacherFIO
            ) {
                teacherFIO = it
            }
            InputRow(
                "Заработная плата",
                teacherSalary
            ) {
                teacherSalary = it
            }
            if (errorText != null) {
                Text(
                    "Ошибка: $errorText",
                    style = MaterialTheme.typography.subtitle2.copy(color = MaterialTheme.colors.error)
                )
            }
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    try {
                        transaction(database) {
                            Teacher.validateNew(
                                fullName = teacherFIO,
                                salary = teacherSalary
                            )
                        }
                        isOpen = false
                        callback()
                    } catch (e: ValidationException) {
                        errorText = "Не удалось создать преподавателя. " +
                                "Возможно, введена отрицательная зарплата " +
                                "или ФИО короче 3-х букв?"
                    }
                }
            )
            {
                Text("Добавить")
            }
        }
    }
}