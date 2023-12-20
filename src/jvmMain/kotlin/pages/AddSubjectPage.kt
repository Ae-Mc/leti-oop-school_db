package pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import entities.Subject
import io.github.aakira.napier.Napier
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import widgets.InputRow

@Composable
fun AddSubjectPage(database: Database, callback: () -> Unit) {
    var isOpen by remember { mutableStateOf(true) }

    var name by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    if (isOpen) {
        Napier.d("Add subject page opened")
        Column(
            modifier = Modifier.padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                content = { Text("Назад") },
                onClick = {
                    isOpen = false
                    callback()
                }
            )
            Text(
                "Добавление предмета",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            InputRow(
                "Название", name
            ) {
                name = it
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
                    try {
                        transaction(database) {
                            Subject.new { this.name = name }
                        }
                        isOpen = false
                        callback()
                    } catch (_: ExposedSQLException) {
                        errorText =
                            "Ошибка! Возможно, предмет с таким названием уже существует?"
                    }
                }
            )
            {
                Text("Добавить")
            }
        }
    }
}
