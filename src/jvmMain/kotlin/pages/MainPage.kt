package pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import entities.Student
import io.github.aakira.napier.Napier
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

// Main screen for switching between tables
@Composable
fun MainPage(database: Database) {
    var isTeachersPage by remember { mutableStateOf(false) }
    var isStudentsPage by remember { mutableStateOf(false) }
    var isClassesPage by remember { mutableStateOf(false) }
    var isSubjectsPage by remember { mutableStateOf(false) }
    var isOpen by remember { mutableStateOf(true) }
    var isAskingToClose by remember { mutableStateOf(false) }
    var showGenerateReportPage by remember { mutableStateOf(false) }

    // New Window creation
    if (isOpen) {
        Napier.d("Main page opened")
        Window(
            onCloseRequest = { isAskingToClose = true },
            state = WindowState(
                width = 1200.dp,
                height = 720.dp,
                position = WindowPosition.Aligned(Alignment.Center)
            ),
        ) {
            if (isAskingToClose) {
                CloseDialog(
                    title = "Выход",
                    text = "Вы уверены, что хотите выйти?",
                    confirmCallback = { isOpen = false },
                    cancelCallback = { isAskingToClose = false },
                )
            }
            if (showGenerateReportPage) {
                GenerateReportPage(database) { showGenerateReportPage = false }
            } else
                if (isTeachersPage) {
                    TeachersPage(database = database, callback = {
                        isTeachersPage = false
                    })
                } else if (isSubjectsPage) {
                    SubjectsPage(database = database, callback = {
                        isSubjectsPage = false
                    })
                } else if (isClassesPage) {
                    ClassesPage(database = database, callback = {
                        isClassesPage = false
                    })
                } else if (isStudentsPage) {
                    StudentsPage(database = database, callback = {
                        isStudentsPage = false
                    })
                } else {
                    val studentCount = transaction(database) {
                        Student.count()
                    }
                    Text(
                        "Количество учеников в школе: $studentCount",
                        modifier = Modifier.padding(16.dp)
                    )
                    // Column with buttons
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly.also {
                            Arrangement.spacedBy(10.dp)
                        }) {
                        Button(onClick = {
                            isTeachersPage = true
                        }) {
                            Text("Список учителей")
                        }
                        Button(onClick = {
                            isStudentsPage = true
                        }) {
                            Text("Список учеников")
                        }
                        Button(onClick = {
                            isClassesPage = true
                        }) {
                            Text("Список классов")
                        }
                        Button(onClick = {
                            isSubjectsPage = true
                        }) {
                            Text("Список предметов")
                        }
                        Button(onClick = {
                            showGenerateReportPage = true
                        }) {
                            Text("Отчёт")
                        }
                    }
                }
        }
    }
}