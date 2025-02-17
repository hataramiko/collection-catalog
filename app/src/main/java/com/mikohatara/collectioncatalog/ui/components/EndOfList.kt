package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EndOfList(
    modifier: Modifier = Modifier,
    hasCircle: Boolean = false,
    text: String? = null
) {
    val color = MaterialTheme.colorScheme.outlineVariant

    if (hasCircle) {
        Canvas(
            modifier = Modifier.size(48.dp)
        ) {
            drawCircle(
                color = color,
                radius = size.minDimension / 13
            )
        }
        text?.let {
            Text(
                text = text,
                color = color,
                modifier = Modifier.padding(bottom = 22.dp, top = 2.dp)
            )
        }
    } else if (text != null) {
        HorizontalDivider(
            color = color,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        )
        Text(
            text = text,
            color = color,
            modifier = Modifier.padding(bottom = 22.dp, top = 2.dp)
        )
    } else {
        Spacer(modifier = modifier.height(48.dp))
    }
}
