package com.mikohatara.collectioncatalog.util

import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.WantedPlate

fun getItemType(item: Any): ItemType {
    return if (item is WantedPlate) {
        ItemType.WANTED_PLATE
    } else {
        ItemType.PLATE
    }
}
