package pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import org.jetbrains.exposed.sql.Database

// Main screen for switching between tables
@Composable
fun MainPage(database: Database) {
    var isTeachersPage by remember { mutableStateOf(false) }
    var isStudentsPage by remember { mutableStateOf(false) }
    var isOpen by remember { mutableStateOf(true) }
    var isAskingToClose by remember { mutableStateOf(false) }

    // New Window creation
    if (isOpen) {
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
            if (isTeachersPage) {
                TeachersPage(database = database, callback = {
                    isTeachersPage = false
                })
            } else if (isStudentsPage) {
                StudentsPage(database = database, callback = {
                    isStudentsPage = false
                })
            } else {
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
                }
            }
        }
    }
}