package com.mikohatara.collectioncatalog.data

import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class Collection(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "emoji") val emoji: String?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "color") val color: CollectionColor = CollectionColor.DEFAULT
)

enum class CollectionColor(val color: Color) {
    DEFAULT(Color.Unspecified),
    RED(Color.Red),
    YELLOW(Color.Yellow),
    GREEN(Color.Green),
}
