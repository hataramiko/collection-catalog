package com.mikohatara.collectioncatalog.data

sealed class Item {
    data class PlateItem(val plate: Plate) : Item()
    data class WantedPlateItem(val wantedPlate: WantedPlate) : Item()
    //data class FormerPlateItem(val formerPlate: FormerPlate) : Item()
}

enum class ItemType {
    PLATE,
    WANTED_PLATE,
    //FORMER_PLATE
}

data class ItemDetails(
    val id: Int? = null,
    // CommonDetails
    val country: String? = null,
    val region1st: String? = null,
    val region2nd: String? = null,
    val region3rd: String? = null,
    val type: String? = null,
    val periodStart: Int? = null,
    val periodEnd: Int? = null,
    val year: Int? = null,
    // UniqueDetails
    val regNo: String? = null,
    val imagePath: String? = null,
    val notes: String? = null,
    val vehicle: String? = null,
    val date: String? = null,
    val cost: Long? = null,
    val value: Long? = null,
    val status: String? = null,
    // Grading
    val isKeeper: Boolean = false,
    val isForTrade: Boolean = false,
    // Size
    val width: Int? = null,
    val height: Int? = null,
    val weight: Double? = null,
    // Color
    val colorMain: String? = null,
    val colorSecondary: String? = null,
    // Source
    val sourceName: String? = null,
    val sourceAlias: String? = null,
    val sourceType: String? = null,
    val sourceDetails: String? = null,
    val sourceCountry: String? = null,
    // ArchivalDetails
    val archivalDate: String? = null,
    val recipientName: String? = null,
    val recipientAlias: String? = null,
    val actionType: String? = null,
    val actionDetails: String? = null,
    val price: Long? = null,
    val recipientCountry: String? = null
)