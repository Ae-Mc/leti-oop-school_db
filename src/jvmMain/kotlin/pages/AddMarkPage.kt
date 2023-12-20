package pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import entities.*
import exceptions.ValidationException
import io.github.aakira.napier.Napier
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import widgets.DropDown
import widgets.InputRow
import kotlin.concurrent.thread

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddMarkPage(database: Database, student: Student, callback: () -> Unit) {
    var mark by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf<Subject?>(null) }
    var teacher by remember { mutableStateOf<Teacher?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var teachersExpanded by remember { mutableStateOf(false) }
    var subjectsExpanded by remember { mutableStateOf(false) }
    val teachers = transaction(database) { Teacher.all().toList() }
    val subjects = transaction(database) { Subject.all().toList() }

    Napier.d("Add mark page opened")
    Column(
        modifier = Modifier.padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            content = { Text("Назад") },
            onClick = callback
        )
        Text(
            "Добавление оценки",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        InputRow("Оценка", mark) { mark = it }
        InputRow("Дата (XXXX-XX-XX)", date) { date = it }
        Row(
            modifier = Modifier.fillMaxWidth().padding(all = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Учитель")
            Spacer(Modifier.width(10.dp))
            DropDown(
                teachers,
                selectedItem = teacher,
                onSelect = { teacher = it },
                toString = { it.fullName },
                addNoElement = false
            )
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
                            Mark.validateNew() // TODO
                        }
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