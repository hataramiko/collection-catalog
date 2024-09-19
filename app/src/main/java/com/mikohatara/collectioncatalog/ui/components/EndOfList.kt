package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EndOfList(
    modifier: Modifier = Modifier,
    hasCircle: Boolean = false
) {
    if (hasCircle) {
        val circleColor = MaterialTheme.colorScheme.outline
        Canvas(
            modifier = Modifier.size(48.dp)
        ) {
            drawCircle(
                color = circleColor,
                radius = size.minDimension / 13,
                alpha = 0.33f
            )
        }
    } else {
        Spacer(modifier = modifier.height(48.dp))
    }
}
