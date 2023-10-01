package widgets

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InputRow(
    label: String,
    value: String,
    isError: Boolean = false,
    onValueChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(all = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label)
        Spacer(Modifier.width(10.dp))
        OutlinedTextField(
            value,
            onValueChange,
            label = { Text(label) },
            isError = isError,
        )
    }
}