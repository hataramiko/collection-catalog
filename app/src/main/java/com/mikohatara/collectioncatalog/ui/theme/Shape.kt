package com.mikohatara.collectioncatalog.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

val Shapes = Shapes()

data class CustomShapes(
    val card8: RoundedCornerShape = RoundedCornerShape(8.dp),
    val card20: RoundedCornerShape = RoundedCornerShape(20.dp),
)

val LocalCustomShapes = staticCompositionLocalOf { CustomShapes() }
