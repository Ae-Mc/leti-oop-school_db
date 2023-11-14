import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.application
import org.jetbrains.exposed.sql.Database
import pages.MainPage

@Composable
@Preview
fun App(database: Database) {
    MaterialTheme {
        MainPage(database)
    }
}

fun main() = application {
    val database = DatabaseFactory().getDatabase()

    App(database = database)
}