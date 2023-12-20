package widgets

import androidx.compose.material.*
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> DropDown(
    items: List<T>,
    selectedItem: T?,
    onSelect: (selectedItem: T?) -> Unit,
    toString: ((x: T) -> String)? = null,
    addNoElement: Boolean = true,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val realToString: (x: T) -> String = if (toString == null) {
        { x -> x.toString() }
    } else {
        toString
    }
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded }
    ) {
        TextField(
            value = if (selectedItem == null) "Нет" else realToString(
                selectedItem
            ),
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = isExpanded
                )
            },
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            if (addNoElement) {
                DropdownMenuItem(
                    onClick = {
                        isExpanded = false
                        onSelect(null)
                    }
                ) {
                    Text(text = "Нет")
                }
            }
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        isExpanded = false
                        onSelect(item)
                    }
                ) {
                    Text(text = realToString(item))
                }
            }
        }
    }

}