package com.mikohatara.collectioncatalog.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.ui.components.customTopAppBarColors

@Composable
fun getTopAppBarColor(
    isSelectionMode: Boolean,
    isTopRowHidden: Boolean,
    contentOffset: Float,
    collapsedFraction: Float
): Color {
    val isDarkTheme = isSystemInDarkTheme()
    // The colors are close estimates made to match the system nav bar. Should be improved.
    val (collapsedColor, collapsedAlpha) = if (isDarkTheme) {
        colorScheme.surfaceColorAtElevation(4.dp) to 0.5f
    } else colorScheme.surfaceContainerLowest to 0.9f

    return when {
        isSelectionMode -> colorScheme.surfaceContainerLow
        isTopRowHidden -> collapsedColor.copy(alpha = collapsedAlpha)
        contentOffset < -1f -> customTopAppBarColors().scrolledContainerColor
        else -> lerp(
            customTopAppBarColors().containerColor,
            customTopAppBarColors().scrolledContainerColor,
            collapsedFraction
        )
    }
}
