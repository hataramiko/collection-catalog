package com.mikohatara.collectioncatalog.util

import com.mikohatara.collectioncatalog.data.ArchivalDetails
import com.mikohatara.collectioncatalog.data.Color
import com.mikohatara.collectioncatalog.data.CommonDetails
import com.mikohatara.collectioncatalog.data.FormerPlate
import com.mikohatara.collectioncatalog.data.ItemDetails
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.Size
import com.mikohatara.collectioncatalog.data.Source
import com.mikohatara.collectioncatalog.data.UniqueDetails
import com.mikohatara.collectioncatalog.data.WantedPlate

fun ItemDetails.toPlate(): Plate = Plate(
    id ?: 0,
    CommonDetails(
        country ?: "",
        region1st?.takeIf { it.isNotBlank() },
        region2nd?.takeIf { it.isNotBlank() },
        region3rd?.takeIf { it.isNotBlank() },
        type ?: "",
        periodStart?.takeIf { it.isValidYear() },
        periodEnd?.takeIf { it.isValidYear() },
        year?.takeIf { it.isValidYear() }
    ),
    UniqueDetails(
        regNo ?: "",
        imagePath,
        notes?.takeIf { it.isNotBlank() },
        vehicle?.takeIf { it.isNotBlank() },
        date?.takeIf { it.isNotBlank() },
        cost,
        value,
        status?.takeIf { it.isNotBlank() }
    ),
    Size(
        width,
        height,
        weight
    ),
    Color(
        colorMain?.takeIf { it.isNotBlank() },
        colorSecondary?.takeIf { it.isNotBlank() }
    ),
    Source(
        sourceName?.takeIf { it.isNotBlank() },
        sourceAlias?.takeIf { it.isNotBlank() },
        sourceType?.takeIf { it.isNotBlank() },
        sourceDetails?.takeIf { it.isNotBlank() },
        sourceCountry?.takeIf { it.isNotBlank() }
    )
)

fun ItemDetails.toWantedPlate(): WantedPlate = WantedPlate(
    id ?: 0,
    regNo?.takeIf { it.isNotBlank() },
    imagePath?.takeIf { it.isNotBlank() },
    notes?.takeIf { it.isNotBlank() },
    CommonDetails(
        country ?: "",
        region1st?.takeIf { it.isNotBlank() },
        region2nd?.takeIf { it.isNotBlank() },
        region3rd?.takeIf { it.isNotBlank() },
        type ?: "",
        periodStart?.takeIf { it.isValidYear() },
        periodEnd?.takeIf { it.isValidYear() },
        year?.takeIf { it.isValidYear() }
    ),
    Size(
        width,
        height,
        weight
    ),
    Color(
        colorMain?.takeIf { it.isNotBlank() },
        colorSecondary?.takeIf { it.isNotBlank() }
    )
)

fun ItemDetails.toFormerPlate(): FormerPlate = FormerPlate(
    id ?: 0,
    CommonDetails(
        country ?: "",
        region1st?.takeIf { it.isNotBlank() },
        region2nd?.takeIf { it.isNotBlank() },
        region3rd?.takeIf { it.isNotBlank() },
        type ?: "",
        periodStart?.takeIf { it.isValidYear() },
        periodEnd?.takeIf { it.isValidYear() },
        year?.takeIf { it.isValidYear() }
    ),
    UniqueDetails(
        regNo ?: "",
        imagePath,
        notes?.takeIf { it.isNotBlank() },
        vehicle?.takeIf { it.isNotBlank() },
        date?.takeIf { it.isNotBlank() },
        cost,
        value,
        status?.takeIf { it.isNotBlank() }
    ),
    Size(
        width,
        height,
        weight
    ),
    Color(
        colorMain?.takeIf { it.isNotBlank() },
        colorSecondary?.takeIf { it.isNotBlank() }
    ),
    Source(
        sourceName?.takeIf { it.isNotBlank() },
        sourceAlias?.takeIf { it.isNotBlank() },
        sourceType?.takeIf { it.isNotBlank() },
        sourceDetails?.takeIf { it.isNotBlank() },
        sourceCountry?.takeIf { it.isNotBlank() }
    ),
    ArchivalDetails(
        archivalDate?.takeIf { it.isNotBlank() },
        recipientName?.takeIf { it.isNotBlank() },
        recipientAlias?.takeIf { it.isNotBlank() },
        archivalType?.takeIf { it.isNotBlank() },
        archivalDetails?.takeIf { it.isNotBlank() },
        price,
        recipientCountry?.takeIf { it.isNotBlank() }
    )
)

fun Plate.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    // CommonDetails
    country = commonDetails.country,
    region1st = commonDetails.region1st,
    region2nd = commonDetails.region2nd,
    region3rd = commonDetails.region3rd,
    type = commonDetails.type,
    periodStart = commonDetails.periodStart,
    periodEnd = commonDetails.periodEnd,
    year = commonDetails.year,
    // UniqueDetails
    regNo = uniqueDetails.regNo,
    imagePath = uniqueDetails.imagePath,
    notes = uniqueDetails.notes,
    vehicle = uniqueDetails.vehicle,
    date = uniqueDetails.date,
    cost = uniqueDetails.cost,
    value = uniqueDetails.value,
    status = uniqueDetails.status,
    // Size
    width = size.width,
    height = size.height,
    weight = size.weight,
    // Color
    colorMain = color.main,
    colorSecondary = color.secondary,
    // Source
    sourceName = source.name,
    sourceAlias = source.alias,
    sourceType = source.type,
    sourceDetails = source.details,
    sourceCountry = source.country,
)

fun WantedPlate.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    regNo = regNo,
    imagePath = imagePath,
    notes = notes,
    // CommonDetails
    country = commonDetails.country,
    region1st = commonDetails.region1st,
    region2nd = commonDetails.region2nd,
    region3rd = commonDetails.region3rd,
    type = commonDetails.type,
    periodStart = commonDetails.periodStart,
    periodEnd = commonDetails.periodEnd,
    year = commonDetails.year,
    // Size
    width = size.width,
    height = size.height,
    weight = size.weight,
    // Color
    colorMain = color.main,
    colorSecondary = color.secondary,
)

fun FormerPlate.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    // CommonDetails
    country = commonDetails.country,
    region1st = commonDetails.region1st,
    region2nd = commonDetails.region2nd,
    region3rd = commonDetails.region3rd,
    type = commonDetails.type,
    periodStart = commonDetails.periodStart,
    periodEnd = commonDetails.periodEnd,
    year = commonDetails.year,
    // UniqueDetails
    regNo = uniqueDetails.regNo,
    imagePath = uniqueDetails.imagePath,
    notes = uniqueDetails.notes,
    vehicle = uniqueDetails.vehicle,
    date = uniqueDetails.date,
    cost = uniqueDetails.cost,
    value = uniqueDetails.value,
    status = uniqueDetails.status,
    // Size
    width = size.width,
    height = size.height,
    weight = size.weight,
    // Color
    colorMain = color.main,
    colorSecondary = color.secondary,
    // Source
    sourceName = source.name,
    sourceAlias = source.alias,
    sourceType = source.type,
    sourceDetails = source.details,
    sourceCountry = source.country,
    // ArchivalDetails
    archivalDate = archivalDetails.archivalDate,
    recipientName = archivalDetails.recipientName,
    recipientAlias = archivalDetails.recipientAlias,
    archivalType = archivalDetails.archivalReason,
    archivalDetails = archivalDetails.archivalDetails,
    price = archivalDetails.price,
    recipientCountry = archivalDetails.recipientCountry
)

fun ItemDetails.pasteItemDetails(copy: ItemDetails): ItemDetails = ItemDetails(
    id = id,
    // CommonDetails
    country = copy.country?.takeIf { it.isNotBlank() } ?: country,
    region1st = copy.region1st?.takeIf { it.isNotBlank() } ?: region1st,
    region2nd = copy.region2nd?.takeIf { it.isNotBlank() } ?: region2nd,
    region3rd = copy.region3rd?.takeIf { it.isNotBlank() } ?: region3rd,
    type = copy.type?.takeIf { it.isNotBlank() } ?: type,
    periodStart = copy.periodStart?.takeIf { it.isValidYear() } ?: periodStart,
    periodEnd = copy.periodEnd?.takeIf { it.isValidYear() } ?: periodEnd,
    year = copy.year?.takeIf { it.isValidYear() } ?: year,
    // UniqueDetails
    regNo = copy.regNo?.takeIf { it.isNotBlank() } ?: regNo,
    imagePath = copy.imagePath?.takeIf { it.isNotBlank() } ?: imagePath,
    notes = copy.notes?.takeIf { it.isNotBlank() } ?: notes,
    vehicle = copy.vehicle?.takeIf { it.isNotBlank() } ?: vehicle,
    date = copy.date?.takeIf { it.isNotBlank() } ?: date,
    cost = copy.cost?.takeIf { true } ?: cost,
    value = copy.value?.takeIf { true } ?: value,
    status = copy.status?.takeIf { it.isNotBlank() } ?: status,
    // Size
    width = copy.width?.takeIf { true } ?: width,
    height = copy.height?.takeIf { true } ?: height,
    weight = copy.weight?.takeIf { true } ?: weight,
    // Color
    colorMain = copy.colorMain?.takeIf { it.isNotBlank() } ?: colorMain,
    colorSecondary = copy.colorSecondary?.takeIf { it.isNotBlank() } ?: colorSecondary,
    // Source
    sourceName = copy.sourceName?.takeIf { it.isNotBlank() } ?: sourceName,
    sourceAlias = copy.sourceAlias?.takeIf { it.isNotBlank() } ?: sourceAlias,
    sourceType = copy.sourceType?.takeIf { it.isNotBlank() } ?: sourceType,
    sourceDetails = copy.sourceDetails?.takeIf { it.isNotBlank() } ?: sourceDetails,
    sourceCountry = copy.sourceCountry?.takeIf { it.isNotBlank() } ?: sourceCountry,
    // ArchivalDetails
    archivalDate = copy.archivalDate?.takeIf { it.isNotBlank() } ?: archivalDate,
    recipientName = copy.recipientName?.takeIf { it.isNotBlank() } ?: recipientName,
    recipientAlias = copy.recipientAlias?.takeIf { it.isNotBlank() } ?: recipientAlias,
    archivalType = copy.archivalType?.takeIf { it.isNotBlank() } ?: archivalType,
    archivalDetails = copy.archivalDetails?.takeIf { it.isNotBlank() } ?: archivalDetails,
    price = copy.price?.takeIf { true } ?: price,
    recipientCountry = copy.recipientCountry?.takeIf { it.isNotBlank() } ?: recipientCountry
)
