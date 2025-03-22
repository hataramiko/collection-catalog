package com.mikohatara.collectioncatalog.util

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.CollectionColor

fun getCollectionColor(collectionColor: CollectionColor, context: Context): String {
    val stringResId = when (collectionColor) {
        CollectionColor.DEFAULT -> R.string.collection_color_default
        CollectionColor.RED -> R.string.collection_color_red
        CollectionColor.ORANGE -> R.string.collection_color_orange
        CollectionColor.YELLOW -> R.string.collection_color_yellow
        CollectionColor.GREEN -> R.string.collection_color_green
        CollectionColor.BLUE -> R.string.collection_color_blue
        CollectionColor.PURPLE -> R.string.collection_color_violet
    }
    return context.getString(stringResId)
}

fun String.isCollectionColor(context: Context): Boolean {
    return CollectionColor.entries.any { getCollectionColor(it, context) == this }
}

fun String.toColor(context: Context): Color {
    return CollectionColor.entries.find {
        getCollectionColor(it, context) == this
    }?.color ?: Color.Unspecified
}
