package pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import entities.Class
import entities.Student
import exceptions.ValidationException
import io.github.aakira.napier.Napier
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import widgets.DropDown
import widgets.InputRow

@Composable
fun AddStudentPage(database: Database, callback: () -> Unit = {}) {
    var studentFullName by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }
    val classes = transaction(database) {
        Class.all().toList()
    }

    if (classes.isEmpty()) {
        DialogWindow(onCloseRequest = callback) {
            Column {
                Text("Ошибка! Нет ни одного класса. Сначала добавьте класс!")
                Button(onClick = callback) {
                    Text("Ок")
                }
            }
        }
    } else {

        var studentClass by remember { mutableStateOf(classes.first()) }
        Napier.d("Add student page opened")
        Column(
            modifier = Modifier.padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                content = { Text("Назад") }, onClick = callback
            )
            Text(
                "Добавление ученика",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            InputRow(
                "ФИО", studentFullName
            ) {
                studentFullName = it
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(all = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Класс")
                Spacer(Modifier.width(10.dp))
                DropDown(
                    items = classes,
                    selectedItem = studentClass,
                    onSelect = { studentClass = it!! },
                    toString = { it.toString() },
                    addNoElement = false,
                )
            }
            if (errorText != null) {
                Text(
                    "Ошибка: $errorText",
                    style = MaterialTheme.typography.subtitle2.copy(color = MaterialTheme.colors.error)
                )
            }
            Button(modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    try {
                        transaction(database) {
                            Student.validateNew(
                                fullName = studentFullName,
                                studentClass = studentClass
                            )
                        }
                        callback()
                    } catch (e: ValidationException) {
                        errorText =
                            "Не удалось создать ученика. " + "Возможно, вы не выбрали класс " + "или ФИО короче 3-х букв?"
                    }
                }) {
                Text("Добавить")
            }
        }
    }
}