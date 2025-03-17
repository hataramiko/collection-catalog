package com.mikohatara.collectioncatalog.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun generatePalette(bitmap: Bitmap): Color? {
    return withContext(Dispatchers.IO) {
        val palette = Palette.from(bitmap).generate()
        val vibrantSwatch = palette.vibrantSwatch
        val lightVibrantSwatch = palette.lightVibrantSwatch
        val darkVibrantSwatch = palette.darkVibrantSwatch
        val mutedSwatch = palette.mutedSwatch
        val lightMutedSwatch = palette.lightMutedSwatch
        val darkMutedSwatch = palette.darkMutedSwatch

        val color = listOfNotNull(
            vibrantSwatch, lightVibrantSwatch, darkVibrantSwatch,
            mutedSwatch, lightMutedSwatch, darkMutedSwatch
        ).firstOrNull()?.rgb

        if (color != null) {
            Color(color)
        } else null
    }
}

fun getBitmapFromEdges(sourceBitmap: Bitmap, sidesOnly: Boolean = false): Bitmap {
    val width = sourceBitmap.width
    val height = sourceBitmap.height

    if (width <= 2 || height <= 2) {
        return sourceBitmap
    }

    val edgeSize = 8 // Maybe increase to 10 or more if needed?

    val edgePixels = createBitmap(width, height)
    val canvas = android.graphics.Canvas(edgePixels)

    if (!sidesOnly) {
        canvas.drawBitmap( // Top edge
            sourceBitmap,
            android.graphics.Rect(0, 0, width, edgeSize),
            android.graphics.Rect(0, 0, width, edgeSize),
            null
        )
        canvas.drawBitmap( // Bottom edge
            sourceBitmap,
            android.graphics.Rect(0, height - edgeSize, width, height),
            android.graphics.Rect(0, height - edgeSize, width, height),
            null
        )
    }
    canvas.drawBitmap( // Left edge
        sourceBitmap,
        android.graphics.Rect(0, edgeSize, edgeSize, height - edgeSize),
        android.graphics.Rect(0, edgeSize, edgeSize, height - edgeSize),
        null
    )
    canvas.drawBitmap( // Right edge
        sourceBitmap,
        android.graphics.Rect(width - edgeSize, edgeSize, width, height - edgeSize),
        android.graphics.Rect(width - edgeSize, edgeSize, width, height - edgeSize),
        null
    )

    return edgePixels
}
