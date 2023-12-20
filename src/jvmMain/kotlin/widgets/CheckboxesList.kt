package widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> CheckboxesList(
    items: List<T>,
    onSelect: (item: T) -> Unit,
    selectedItems: List<T> = listOf(),
    toString: ((x: T) -> String)? = null,
) {
    val realToString: (x: T) -> String = toString ?: { x -> x.toString() }

    LazyColumn {
        items(items.size) { index ->
            Row(
                modifier = Modifier.clickable(enabled = true, onClick = {
                    onSelect(items[index])
                }).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = realToString(items[index]))
                Checkbox(
                    checked = selectedItems.contains(items[index]),
                    onCheckedChange = null
                )
            }
        }
    }

}