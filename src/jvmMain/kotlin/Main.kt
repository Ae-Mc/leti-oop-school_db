import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.jetbrains.exposed.sql.Database
import pages.CloseDialog
import pages.TeachersPage

@Composable
@Preview
fun App(database: Database) {
    MaterialTheme {
        TeachersPage(database)
    }
}

fun main() = application {
    val database = DatabaseFactory().getDatabase()
    val state = WindowState(width = 1920.dp, height = 1080.dp, position = WindowPosition.Aligned(Alignment.Center))
    var isAskingToClose by remember { mutableStateOf(false) }
    var isOpen by remember { mutableStateOf(true) }

    if (isOpen) {
        Window(onCloseRequest = { isAskingToClose = true }, state = state) {
            if (isAskingToClose) {
                CloseDialog(
                    title = "Выход",
                    text = "Вы уверены, что хотите выйти?",
                    confirmCallback = { isOpen = false },
                    cancelCallback = { isAskingToClose = false },
                )
            }

            App(database = database)
        }
    }
}
