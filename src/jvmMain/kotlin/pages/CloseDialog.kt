package pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog

@Composable
fun CloseDialog(
    title: String,
    text: String = title,
    confirmCallback: () -> Unit = {},
    cancelCallback: () -> Unit = {}
) {
    Dialog(title = title, onCloseRequest = { }) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text,
                style = MaterialTheme.typography.h5.copy(textAlign = TextAlign.Center)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Button(onClick = confirmCallback) {
                    Text("Да")
                }
                Button(onClick = cancelCallback) {
                    Text("Нет")
                }
            }
        }
    }
}