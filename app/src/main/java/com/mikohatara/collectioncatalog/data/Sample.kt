package com.mikohatara.collectioncatalog.data

import androidx.room.Entity

@Entity(tableName = "samples")
data class Sample(
    val country: String,
    val number: String,
    val type: String
)
