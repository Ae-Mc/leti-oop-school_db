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
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import positiveIntValidator
import util.validators.fullNameValidator
import widgets.CheckboxesList
import widgets.InputRow

@Composable
fun EditTeacherPage(
    database: Database,
    teacher: Teacher,
    callback: () -> Unit = {},
) {
    var isOpen by remember { mutableStateOf(true) }

    var teacherFIO by remember { mutableStateOf(teacher.fullName) }
    var teacherSalary by remember { mutableStateOf(teacher.salary.toString()) }
    var errorText by remember { mutableStateOf<String?>(null) }
    val subjects = transaction(database) { Subject.all().toList() }
    val selectedSubjects = remember {
        mutableStateListOf(*teacher.subjects.map { it.id.value }
            .toTypedArray())
    }

    if (isOpen) {
        Napier.d("Edit teacher page opened")
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
                "Редактирование учителя",
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
                selectedItems = subjects.filter { it.id.value in selectedSubjects },
                onSelect = {
                    if (selectedSubjects.contains(it.id.value)) {
                        selectedSubjects -= it.id.value
                    } else {
                        selectedSubjects += it.id.value
                    }
                },
            )
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    try {
                        transaction(database) {
                            val fullName = fullNameValidator(teacherFIO)
                            val salary =
                                positiveIntValidator(teacherSalary.toIntOrNull())
                            teacher.fullName = fullName
                            teacher.salary = salary
                            teacher.subjects =
                                SizedCollection(subjects.filter { it.id.value in selectedSubjects })
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
                Text("Применить")
            }
        }
    }
}