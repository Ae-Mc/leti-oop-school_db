package pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import entities.*
import exceptions.ValidationException
import io.github.aakira.napier.Napier
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import util.validators.fullNameValidator
import widgets.DropDown
import widgets.InputRow
import widgets.TableCell
import java.time.format.DateTimeFormatter

@Composable
fun EditStudentPage(
    database: Database,
    student: Student,
    callback: () -> Unit = {},
) {
    val columnState = LazyListState()
    var studentFullName by remember { mutableStateOf("") }
    var studentClass by remember { mutableStateOf(student.studentClass) }
    var errorText by remember { mutableStateOf<String?>(null) }
    val weights = floatArrayOf(0.2f, 1.3f, 1f, 0.2f, 1f, 0.4f, 0.4f)
    val classes = transaction(database) {
        Class.all().toList()
    }
    var marks by remember { mutableStateOf(refreshMarks(database, student)) }

    Napier.d("Edit student page opened")
    Column(
        modifier = Modifier.padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            content = { Text("Назад") },
            onClick = callback
        )
        Text(
            "Редактирование ученика",
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
                addNoElement = false
            )
        }
        // Table
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            state = columnState,
            userScrollEnabled = true,
        ) {
            // Here is the header
            item {
                Row(Modifier.background(Color(0xff9efd38))) {
                    TableCell(
                        text = "№",
                        weight = weights[0],
                    )
                    TableCell(text = "id", weight = weights[1])
                    TableCell(text = "Предмет", weight = weights[2])
                    TableCell(
                        text = "Оценка",
                        weight = weights[3]
                    )
                    TableCell(
                        text = "Преподаватель",
                        weight = weights[4]
                    )
                    TableCell(
                        text = "Дата",
                        weight = weights[5]
                    )
                    TableCell(text = "", weight = weights[6])
                }
            }
            // Here are all the lines of your table.
            this.itemsIndexed(
                items = marks,
                contentType = { _, _ -> },
                key = { _, mark -> mark.id },
            ) { index, mark ->
                Row {
                    TableCell(
                        text = index.toString(),
                        weight = weights[0]
                    )
                    TableCell(
                        text = mark.id.toString(),
                        weight = weights[1],
                        textStyle = TextStyle(textDecoration = TextDecoration.Underline)
                    )
                    TableCell(
                        text = mark.subject.name,
                        weight = weights[4]
                    )
                    TableCell(
                        text = mark.mark.toString(),
                        weight = weights[2]
                    )
                    TableCell(
                        text = mark.teacher.fullName,
                        weight = weights[3]
                    )
                    TableCell(
                        text = mark.date.format(DateTimeFormatter.ISO_DATE),
                        weight = weights[5]
                    )
                    TableCell(
                        text = "Удалить",
                        weight = weights[6],
                        onClick = {
                            transaction(database) {
                                mark.delete()
                            }
                            marks = refreshMarks(database, student)
                        },
                    )
                }

            }
        }
        if (errorText != null) {
            Text(
                "Ошибка: $errorText",
                style = MaterialTheme.typography.subtitle2.copy(color = MaterialTheme.colors.error)
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    try {
                        transaction(database) {
                            student.fullName =
                                fullNameValidator(studentFullName)
                            student.studentClass = studentClass
                        }
                        callback()
                    } catch (e: ValidationException) {
                        errorText = "Не удалось изменить ученика. " +
                                "Возможно, вы не выбрали класс " +
                                "или ФИО короче 3-х букв?"
                    }
                }
            )
            {
                Text("Применить")
            }
            Button(
                onClick = {
                    // TODO
                    // showAddTeacherPage = true
                },
            ) {
                Text(
                    "Добавить оценку",
                )
            }
        }
    }
}

fun refreshMarks(database: Database, student: Student): List<Mark> {
    return transaction(database) {
        Mark.wrapRows(
            Marks.innerJoin(Students).select { Students.id eq student.id }
        ).toList()
    }
}
