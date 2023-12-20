package pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import entities.Class
import entities.Teacher
import exceptions.ValidationException
import io.github.aakira.napier.Napier
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import widgets.DropDown
import widgets.InputRow
import kotlin.concurrent.thread

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddClassPage(database: Database, callback: () -> Unit) {
    var isOpen by remember { mutableStateOf(true) }

    var admissionYear by remember { mutableStateOf("") }
    var letter by remember { mutableStateOf("") }
    var teacher by remember { mutableStateOf<Teacher?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var teachersExpanded by remember { mutableStateOf(false) }
    val teachers = transaction(database) {
        Teacher.all().toList()
    }

    if (isOpen) {
        Napier.d("Add class page opened")
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
                "Добавление ученика",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            InputRow(
                "Год создания", admissionYear
            ) {
                admissionYear = it
            }
            InputRow(
                "Буква", letter
            ) {
                letter = it
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(all = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Классный руководитель")
                Spacer(Modifier.width(10.dp))
                ExposedDropdownMenuBox(
                    expanded = teachersExpanded,
                    onExpandedChange = { teachersExpanded = !teachersExpanded }
                ) {
                    TextField(
                        value = teacher?.fullName ?: "Нет",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = teachersExpanded
                            )
                        },
                    )
                    DropDown(
                        teachers,
                        selectedItem = teacher,
                        onSelect = { teacher = it },
                        toString = { it.fullName }
                    )
                }
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
                    thread {
                        try {
                            transaction(database) {
                                Class.validateNew(
                                    admissionYear,
                                    letter,
                                    teacher
                                )
                            }
                            isOpen = false
                            callback()
                        } catch (e: ValidationException) {
                            errorText = "Не удалось создать класс. " +
                                    "Возможно, вы не указали букву или " +
                                    "год создания?"
                        }

                    }
                }
            )
            {
                Text("Добавить")
            }
        }
    }
}