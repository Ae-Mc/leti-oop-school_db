import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.jetbrains.exposed.sql.Database
import pages.TeachersPage

@Composable
@Preview
fun App(database: Database) {
    Column {
        TeachersPage(database)
    }
}

fun main() = application {
    val database = DatabaseFactory().getDatabase()
    val state = WindowState(width = 1920.dp, height = 1080.dp, position = WindowPosition.Aligned(Alignment.Center))
    Window(onCloseRequest = ::exitApplication, state = state) {
        App(database = database)
    }
}
