package widgets

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    onClick: (() -> Unit)? = null,
) {
    if (onClick == null) {
        Text(
            text = text,
            modifier = Modifier.border(1.dp, Color.Black).weight(weight)
                .padding(8.dp).then(modifier),
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            style = LocalTextStyle.current.merge(textStyle),
        )
    } else {
        ClickableText(
            text = AnnotatedString(text),
            modifier = Modifier.border(1.dp, Color.Black).weight(weight)
                .padding(8.dp).then(modifier),
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            style = LocalTextStyle.current.merge(textStyle),
            onClick = { onClick() }
        )
    }
}