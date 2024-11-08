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
    colorMain = color.main,
    colorSecondary = color.secondary,
    // CommonDetails
    country = commonDetails.country,
    region1st = commonDetails.region1st,
    region2nd = commonDetails.region2nd,
    region3rd = commonDetails.region3rd,
    type = commonDetails.type,
    periodStart = commonDetails.periodStart,
    periodEnd = commonDetails.periodEnd,
    year = commonDetails.year
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
