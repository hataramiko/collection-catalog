package com.mikohatara.collectioncatalog.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "samples")
data class Sample(
    val country: String,
    val number: String,
    val type: String,
    @PrimaryKey val id: String
)
