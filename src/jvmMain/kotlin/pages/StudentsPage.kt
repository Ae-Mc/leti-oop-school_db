package pages

import androidx.compose.desktop.ui.tooling.preview.Preview
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
import entities.Student
import io.github.aakira.napier.Napier
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import widgets.TableCell


// List of teachers
@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun StudentsPage(database: Database, callback: () -> Unit) {
    // Used to close window
    var isOpen by remember { mutableStateOf(true) }

    var students by remember { mutableStateOf(emptyList<Student>()) }
    val weights = floatArrayOf(0.2f, 1.3f, 1f, 0.2f, 1f, 0.3f)
    val columnState = LazyListState()
    var showAddStudentPage by remember { mutableStateOf(false) }
    var editingStudent by remember { mutableStateOf<Student?>(null) }

    students = refreshStudents(database)

    Napier.d("Students page opened")
    if (showAddStudentPage) {
        AddStudentPage(
            database,
            callback = {
                showAddStudentPage = false
                students = refreshStudents(database)
            },
        )
    } else if (editingStudent is Student) {
        // Shows edit teacher page
        EditStudentPage(
            database = database,
            student = editingStudent!!,
            callback = {
                editingStudent = null
                students = refreshStudents(database)
            })
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            Button(onClick = {
                isOpen = false
                callback()
            }) {
                Text("Назад")
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
                        TableCell(text = "ФИО", weight = weights[2])
                        TableCell(text = "Класс", weight = weights[3])
                        TableCell(text = "Оценки", weight = weights[4])
                        TableCell(text = "", weight = weights[5])
                    }
                }
                // Here are all the lines of your table.
                this.itemsIndexed(
                    items = students,
                    contentType = { _, _ -> },
                    key = { _, teacher -> teacher.id },
                ) { index, student ->
                    Row(
                        modifier = Modifier.onClick {
                            editingStudent = student
                        },
                    ) {
                        TableCell(
                            text = index.toString(),
                            weight = weights[0]
                        )
                        TableCell(
                            text = student.id.toString(),
                            weight = weights[1],
                            textStyle = TextStyle(textDecoration = TextDecoration.Underline)
                        )
                        TableCell(
                            text = student.fullName,
                            weight = weights[2]
                        )
                        TableCell(
                            text = student.studentClass.toString(),
                            weight = weights[3]
                        )
                        TableCell(
                            text = student.marks.joinToString(", ") { mark -> mark.mark.toString() },
                            weight = weights[4]
                        )
                        TableCell(
                            text = "Удалить",
                            weight = weights[5],
                            onClick = {
                                transaction(database) {
                                    student.delete()
                                }
                                students = refreshStudents(database)
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
                        showAddStudentPage = true
                    },
                ) {
                    Text(
                        "Добавить ученика",
                    )
                }
            }
        }
    }
}

fun refreshStudents(database: Database): List<Student> {
    return transaction(database) {
        Student.all()
            .with(Student::studentClass, Student::marks)
            .toList()
    }
}