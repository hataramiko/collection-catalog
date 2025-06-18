package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.CollectionColor

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
fun IconCollectionLabel(
    color: Color,
    modifier: Modifier = Modifier
) {
    val defaultTint = LocalContentColor.current
    val tint = if (color != CollectionColor.DEFAULT.color) color else defaultTint

    Box(contentAlignment = Alignment.Center) {
        Icon(
            painter = painterResource(R.drawable.rounded_label),
            contentDescription = null,
            tint = tint,
            modifier = modifier.offset(x = 1.dp)
        )
        if (color != CollectionColor.DEFAULT.color) {
            Icon(
                painter = painterResource(R.drawable.baseline_label_24),
                contentDescription = null,
                tint = tint.copy(alpha = 0.15f),
                modifier = modifier.scale(scaleX = 0.9f, scaleY = 0.9f)
            )
        }
    }
}

@Composable
fun IconCollectionColor(
    color: Color,
    modifier: Modifier = Modifier
) {
    // This should be the same as the color set in "val tint" of CollectionEntryScreenContent
    val isDefaultColor = color == MaterialTheme.colorScheme.primary

    if (isDefaultColor) {
        Icon(
            painter = painterResource(R.drawable.rounded_format_color_reset_24),
            contentDescription = null,
            tint = color,
            modifier = modifier
        )
    } else {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(R.drawable.rounded_invert_colors_24),
                contentDescription = null,
                tint = color,
                modifier = modifier
            )
            Icon(
                painter = painterResource(R.drawable.rounded_invert_colors_24),
                contentDescription = null,
                tint = color,
                modifier = modifier.scale(scaleX = -0.9f, scaleY = 0.9f).offset(x = (-0.3).dp)
            )
        }
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
