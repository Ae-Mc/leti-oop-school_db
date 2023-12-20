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
import entities.Subject
import entities.Teacher
import exceptions.ValidationException
import io.github.aakira.napier.Napier
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import widgets.CheckboxesList
import widgets.InputRow

@Composable
fun AddTeacherPage(database: Database, callback: () -> Unit = {}) {
    var teacherFIO by remember { mutableStateOf("") }
    var teacherSalary by remember { mutableStateOf("0") }
    var errorText by remember { mutableStateOf<String?>(null) }
    val selectedSubjects = remember { mutableStateListOf<Subject>() }
    val subjects = transaction(database) { Subject.all().toList() }

    Napier.d("Add teacher page opened")
        Column(
            modifier = Modifier.padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                content = { Text("Назад") },
                onClick = callback
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
            Text(
                "Предметы, которые преподаватель может преподавать",
                style = MaterialTheme.typography.h6
            )
            CheckboxesList(
                subjects,
                toString = { subject -> subject.name },
                selectedItems = selectedSubjects,
                onSelect = {
                    if (selectedSubjects.contains(it)) {
                        selectedSubjects -= it
                    } else {
                        selectedSubjects += it
                    }
                },
            )
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    try {
                        transaction(database) {
                            Teacher.validateNew(
                                fullName = teacherFIO,
                                salary = teacherSalary,
                                subjects = selectedSubjects,
                            )
                        }
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