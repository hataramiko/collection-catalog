package com.mikohatara.collectioncatalog.util

import com.mikohatara.collectioncatalog.data.CommonDetails
import com.mikohatara.collectioncatalog.data.Grading
import com.mikohatara.collectioncatalog.data.Measurements
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.Source
import com.mikohatara.collectioncatalog.data.UniqueDetails
import com.mikohatara.collectioncatalog.ui.item.ItemDetails

fun ItemDetails.toPlate(): Plate = Plate(
    CommonDetails(
        country,
        region?.takeIf { it.isNotBlank() },
        area?.takeIf { it.isNotBlank() },
        type,
        period?.takeIf { it.isNotBlank() },
        year//?.takeIf { it != 0 }
    ),
    UniqueDetails(
        number,
        variant,
        imagePath,
        vehicle?.takeIf { it.isNotBlank() },
        notes?.takeIf { it.isNotBlank() },
        date?.takeIf { it.isNotBlank() },
        cost,
        value,
        status?.takeIf { it.isNotBlank() }
    ),
    Grading(
        isKeeper,
        isForTrade,
        condition?.takeIf { it.isNotBlank() }
    ),
    Source(
        sourceName?.takeIf { it.isNotBlank() },
        sourceAlias?.takeIf { it.isNotBlank() },
        sourceDetails?.takeIf { it.isNotBlank() },
        sourceType?.takeIf { it.isNotBlank() },
        sourceCountry?.takeIf { it.isNotBlank() }
    ),
    Measurements(
        width,
        height,
        weight
    )
)

fun Plate.toItemDetails(): ItemDetails = ItemDetails(
    // CommonDetails
    country = commonDetails.country,
    region = commonDetails.region,
    area = commonDetails.area,
    type = commonDetails.type,
    period = commonDetails.period,
    year = commonDetails.year,
    // UniqueDetails
    number = uniqueDetails.number,
    variant = uniqueDetails.variant,
    imagePath = uniqueDetails.imagePath,
    vehicle = uniqueDetails.vehicle,
    notes = uniqueDetails.notes,
    date = uniqueDetails.date,
    cost = uniqueDetails.cost,
    value = uniqueDetails.value,
    status = uniqueDetails.status,
    // Grading
    isKeeper = grading.isKeeper,
    isForTrade = grading.isForTrade,
    condition = grading.condition,
    // Source
    sourceName = source.sourceName,
    sourceAlias = source.sourceAlias,
    sourceDetails = source.sourceDetails,
    sourceType = source.sourceType,
    sourceCountry = source.sourceCountry,
    // Measurements
    width = measurements.width,
    height = measurements.height,
    weight = measurements.weight,
)
