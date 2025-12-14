package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikohatara.collectioncatalog.ui.theme.RekkaryTheme

@Composable
fun CardButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    value: String? = null,
    enabled: Boolean = true,
    mainIconPainter: Painter? = null,
    trailingIconPainter: Painter? = null,
    isMainIconColorTertiary: Boolean = false,
    isMainIconColorSecondary: Boolean = false
) {
    val (labelColor, valueColor) = if (enabled) {
        colorScheme.onBackground to colorScheme.onSurfaceVariant
    } else {
        colorScheme.outline to colorScheme.outline
    }
    val (mainIconContainerColor, mainIconColor) = if (isMainIconColorTertiary) {
        colorScheme.tertiaryContainer to colorScheme.onTertiaryContainer
    } else if (isMainIconColorSecondary) {
        colorScheme.secondaryContainer to colorScheme.onSecondaryContainer
    } else {
        colorScheme.primaryContainer to colorScheme.onPrimaryContainer
    }

    Card(
        shape = RekkaryTheme.shapes.card8,
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainer),
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.padding(14.dp)
        ) {
            if (mainIconPainter != null) {
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(mainIconContainerColor),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            painter = mainIconPainter,
                            contentDescription = null,
                            tint = mainIconColor
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column(modifier = Modifier.padding(start = 4.dp, end = 16.dp).weight(1f)) {
                Text(
                    text = label,
                    color = labelColor,
                    fontSize = 18.sp,
                    /*overflow = TextOverflow.Ellipsis,
                    maxLines = 2*/
                )
                value?.let {
                    Text(
                        text = value,
                        color = valueColor
                    )
                }
            }
            trailingIconPainter?.let {
                Icon(
                    painter = trailingIconPainter,
                    contentDescription = null,
                    tint = colorScheme.outline
                )
            }
        }
    }
}
