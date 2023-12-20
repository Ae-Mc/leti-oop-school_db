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
import entities.Subject
import io.github.aakira.napier.Napier
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import widgets.TableCell

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubjectsPage(database: Database, callback: () -> Unit) {
    var isOpen by remember { mutableStateOf(true) }
    lateinit var subjects: List<Subject>
    val weights = floatArrayOf(0.2f, 1.3f, 0.3f, 0.2f, 0.4f, 0.8f, 0.5f)
    val columnState = LazyListState()
    var showAddSubjectPage by remember { mutableStateOf(false) }

    fun refreshClasses(database: Database): Any {
        subjects = transaction(database) {
            Subject.all().with(Subject::teachers).toList()
        }
        return subjects
    }

    refreshClasses(database)

    if (isOpen) {
        Napier.d("Subjects page opened")
        if (showAddSubjectPage) {
            AddSubjectPage(database, { showAddSubjectPage = false })
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
                                text = "Преподаватели",
                                weight = weights[3]
                            )
                        }
                    }
                    // Here are all the lines of your table.
                    this.itemsIndexed(
                        items = subjects,
                        contentType = { _, _ -> },
                        key = { _, subject -> subject.id },
                    ) { index, subject ->
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
                                text = subject.id.toString(),
                                weight = weights[1],
                                textStyle = TextStyle(textDecoration = TextDecoration.Underline)
                            )
                            TableCell(
                                text = subject.name,
                                weight = weights[2]
                            )
                            TableCell(
                                text = subject.teachers.joinToString { teacher -> teacher.fullName },
                                weight = weights[3]
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Button(
                        onClick = {
                            showAddSubjectPage = true
                        },
                    ) {
                        Text(
                            "Добавить предмет",
                        )
                    }
                }
            }
        }
    }
}
