package pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.onClick
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import entities.Class
import io.github.aakira.napier.Napier
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import widgets.TableCell

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClassesPage(database: Database, callback: () -> Unit) {
    var isOpen by remember { mutableStateOf(true) }
    var classes by remember { mutableStateOf(refreshClasses(database)) }
    val weights = floatArrayOf(0.2f, 1.3f, 0.3f, 0.2f, 0.4f, 0.8f, 0.5f, 0.2f)
    val columnState = LazyListState()
    var showAddClassPage by remember { mutableStateOf(false) }


    if (isOpen) {
        Napier.d("Classes page opened")
        if (showAddClassPage) {
            AddClassPage(database) {
                showAddClassPage = false
                classes = refreshClasses(database)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.Top
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(onClick = {
                        isOpen = false
                        callback()
                    }) {
                        Text("Назад")
                    }
                    Spacer(Modifier.weight(1f))
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
                            TableCell(text = "Название", weight = weights[2])
                            TableCell(
                                text = "Буква",
                                weight = weights[3]
                            )
                            TableCell(
                                text = "Год создания",
                                weight = weights[4]
                            )
                            TableCell(
                                text = "Классрук",
                                weight = weights[5]
                            )
                            TableCell(
                                text = "Кол-во учеников",
                                weight = weights[6]
                            )
                            TableCell(
                                text = "",
                                weight = weights[7]
                            )
                        }
                    }
                    // Here are all the lines of your table.
                    this.itemsIndexed(
                        items = classes,
                        contentType = { _, _ -> },
                        key = { _, studentClass -> studentClass.id },
                    ) { index, studentClass ->
                        Row(
                            modifier = Modifier.onClick {
                                // editingTeacher = teacher
                            },
                        ) {
                            TableCell(
                                text = index.toString(),
                                weight = weights[0]
                            )
                            TableCell(
                                text = studentClass.id.toString(),
                                weight = weights[1],
                                textStyle = TextStyle(textDecoration = TextDecoration.Underline)
                            )
                            TableCell(
                                text = studentClass.name,
                                weight = weights[2]
                            )
                            TableCell(
                                text = studentClass.letter,
                                weight = weights[3]
                            )
                            TableCell(
                                text = studentClass.admissionYear.toString(),
                                weight = weights[4]
                            )
                            TableCell(
                                text = studentClass.classroomTeacher?.fullName
                                    ?: "",
                                weight = weights[5]
                            )
                            TableCell(
                                text = studentClass.students.count().toString(),
                                weight = weights[6]
                            )
                            TableCell(
                                text = "Удалить",
                                weight = weights[7],
                                onClick = {
                                    transaction(database) {
                                        studentClass.delete()
                                    }
                                    classes = refreshClasses(database)
                                },
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Button(
                        onClick = {
                            showAddClassPage = true
                        },
                    ) {
                        Text(
                            "Добавить класс",
                        )
                    }
                }
            }
        }
    }
}

fun refreshClasses(database: Database): List<Class> {
    return transaction(database) {
        Class.all().with(Class::classroomTeacher, Class::students).toList()
    }
}

