package com.mikohatara.collectioncatalog.util

import com.mikohatara.collectioncatalog.data.Item
import com.mikohatara.collectioncatalog.data.ItemType

fun getItemType(item: Any): ItemType {
    return when (item) {
        is Item.PlateItem -> ItemType.PLATE
        is Item.WantedPlateItem -> ItemType.WANTED_PLATE
        is Item.FormerPlateItem -> ItemType.FORMER_PLATE
        else -> throw IllegalArgumentException("getItemType: Invalid item type")
    }
}

fun getItemId(item: Any): Int {
    return when (item) {
        is Item.PlateItem -> item.plate.id
        is Item.WantedPlateItem -> item.wantedPlate.id
        is Item.FormerPlateItem -> item.formerPlate.id
        else -> throw IllegalArgumentException("getItemId: Invalid item type")
    }
}
