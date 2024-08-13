package com.mikohatara.collectioncatalog.util

import com.mikohatara.collectioncatalog.data.Color
import com.mikohatara.collectioncatalog.data.CommonDetails
import com.mikohatara.collectioncatalog.data.Grading
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.Size
import com.mikohatara.collectioncatalog.data.Source
import com.mikohatara.collectioncatalog.data.UniqueDetails
import com.mikohatara.collectioncatalog.ui.item.ItemDetails

fun ItemDetails.toPlate(): Plate = Plate(
    CommonDetails(
        country,
        region1st?.takeIf { it.isNotBlank() },
        region2nd?.takeIf { it.isNotBlank() },
        region3rd?.takeIf { it.isNotBlank() },
        type,
        periodStart,//.takeIf { it.isNotBlank() },
        periodEnd,//.takeIf { it.isNotBlank() },
        year//?.takeIf { it != 0 }
    ),
    UniqueDetails(
        regNo,
        variant,
        imagePath,
        notes?.takeIf { it.isNotBlank() },
        vehicle?.takeIf { it.isNotBlank() },
        date?.takeIf { it.isNotBlank() },
        cost,
        value,
        status?.takeIf { it.isNotBlank() }
    ),
    Grading(
        isKeeper,
        isForTrade
    ),
    Size(
        width,
        height,
        weight
    ),
    Color(
        main,
        secondary
    ),
    Source(
        sourceName?.takeIf { it.isNotBlank() },
        sourceAlias?.takeIf { it.isNotBlank() },
        sourceDetails?.takeIf { it.isNotBlank() },
        sourceType?.takeIf { it.isNotBlank() },
        sourceCountry?.takeIf { it.isNotBlank() }
    )
)

fun Plate.toItemDetails(): ItemDetails = ItemDetails(
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
    variant = uniqueDetails.variant,
    imagePath = uniqueDetails.imagePath,
    notes = uniqueDetails.notes,
    vehicle = uniqueDetails.vehicle,
    date = uniqueDetails.date,
    cost = uniqueDetails.cost,
    value = uniqueDetails.value,
    status = uniqueDetails.status,
    // Grading
    isKeeper = grading.isKeeper,
    isForTrade = grading.isForTrade,
    // Size
    width = size.width,
    height = size.height,
    weight = size.weight,
    // Color
    main = color.main,
    secondary = color.secondary,
    // Source
    sourceName = source.name,
    sourceAlias = source.alias,
    sourceType = source.type,
    sourceDetails = source.details,
    sourceCountry = source.country,
)
