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
    @ColumnInfo(name = "color") val color: CollectionColor = CollectionColor.DEFAULT,
    @ColumnInfo(name = "order") val order: Int = 0
)

enum class CollectionColor(val color: Color) {
    DEFAULT(Color.Unspecified),
    RED(Color(0xFFF44336)),
    ORANGE(Color(0xFFFF9800)),
    YELLOW(Color(0xFFFFEB3B)),
    GREEN(Color(0xFF4CAF50)),
    BLUE(Color(0xFF2196F3)),
    PURPLE(Color(0xFF793AB7)),
}
