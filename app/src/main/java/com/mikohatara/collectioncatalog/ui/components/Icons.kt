package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.R

@Composable
fun IconAbc123(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(24.dp)) {
        Icon(
            painter = painterResource(R.drawable.rounded_abc),
            contentDescription = null,
            modifier = Modifier.absoluteOffset(y = (-5).dp)
        )
        Icon(
            painter = painterResource(R.drawable.rounded_123),
            contentDescription = null,
            modifier = Modifier.absoluteOffset(y = 6.dp)
        )
    }
}

@Composable
fun IconQuotationMark(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.outline,
    isFlipped: Boolean = false
) {
    val scale = if (isFlipped) -1f else 1f

    Box(
        modifier = modifier.scale(scaleX = scale, scaleY = scale)
    ) {
        Icon(
            painter = painterResource(R.drawable.outline_format_quote),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(size)
        )
        Icon(
            painter = painterResource(R.drawable.baseline_circle),
            contentDescription = null,
            tint = tint,
            modifier = Modifier
                .size(size / 4)
                .offset(x = (size / 4.7f), y = (size / 3.4f))
        )
        Icon(
            painter = painterResource(R.drawable.baseline_circle),
            contentDescription = null,
            tint = tint,
            modifier = Modifier
                .size(size / 4)
                .offset(x = (size / 1.7f), y = (size / 3.4f))
        )
    }
}
