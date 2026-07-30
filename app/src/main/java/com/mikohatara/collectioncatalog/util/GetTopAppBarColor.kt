package com.mikohatara.collectioncatalog.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.mikohatara.collectioncatalog.ui.components.customTopAppBarColors

@Composable
fun getTopAppBarColor(
    isSelectionMode: Boolean,
    isTopRowHidden: Boolean,
    contentOffset: Float,
    collapsedFraction: Float
): Color {
    val isDarkTheme = isSystemInDarkTheme()
    val collapsedAlpha = if (isDarkTheme) 0.5f else 0.9f

    return when {
        isSelectionMode -> customTopAppBarColors().containerColor
        isTopRowHidden -> colorScheme.surfaceContainer.copy(alpha = collapsedAlpha)
        contentOffset < -1f -> customTopAppBarColors().scrolledContainerColor
        else -> lerp(
            customTopAppBarColors().containerColor,
            customTopAppBarColors().scrolledContainerColor,
            collapsedFraction
        )
    }
}
