package com.mikohatara.collectioncatalog.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.ItemDetails

data class ItemDetailsCheckbox(
    val label: String,
    val value: String? = null
)

@Composable
fun getItemDetailsCheckboxList(itemDetails: ItemDetails): List<ItemDetailsCheckbox> {
    val checkboxList = mutableListOf<ItemDetailsCheckbox>()

    checkboxList.add(ItemDetailsCheckbox(stringResource(R.string.reg_no), itemDetails.regNo))
    // CommonDetails
    checkboxList.add(ItemDetailsCheckbox(stringResource(R.string.country), itemDetails.country))
    checkboxList
        .add(ItemDetailsCheckbox(stringResource(R.string.subdivision), itemDetails.region1st))
    checkboxList
        .add(ItemDetailsCheckbox(stringResource(R.string.region), itemDetails.region2nd))
    checkboxList
        .add(ItemDetailsCheckbox(stringResource(R.string.region_second), itemDetails.region3rd))
    checkboxList.add(ItemDetailsCheckbox(stringResource(R.string.type), itemDetails.type))
    checkboxList.add(ItemDetailsCheckbox(
        stringResource(R.string.period_start), itemDetails.periodStart?.toString())
    )
    checkboxList.add(ItemDetailsCheckbox(
        stringResource(R.string.period_end), itemDetails.periodEnd?.toString())
    )
    checkboxList
        .add(ItemDetailsCheckbox(stringResource(R.string.year), itemDetails.year?.toString()))
    // UniqueDetails, minus regNo & imagePath
    // regNo is on top of the list, imagePath is a separate item
    checkboxList.add(ItemDetailsCheckbox(stringResource(R.string.notes), itemDetails.notes))
    checkboxList.add(ItemDetailsCheckbox(stringResource(R.string.vehicle), itemDetails.vehicle))
    checkboxList.add(ItemDetailsCheckbox(stringResource(R.string.date), itemDetails.date))
    checkboxList
        .add(ItemDetailsCheckbox(stringResource(R.string.cost), itemDetails.cost?.toString()))
    checkboxList
        .add(ItemDetailsCheckbox(stringResource(R.string.value), itemDetails.value?.toString()))
    checkboxList.add(ItemDetailsCheckbox(stringResource(R.string.location), itemDetails.status))
    // Size
    checkboxList
        .add(ItemDetailsCheckbox(stringResource(R.string.width), itemDetails.width?.toString()))
    checkboxList
        .add(ItemDetailsCheckbox(stringResource(R.string.height), itemDetails.height?.toString()))
    checkboxList
        .add(ItemDetailsCheckbox(stringResource(R.string.weight), itemDetails.weight?.toString()))
    // Color
    checkboxList.add(ItemDetailsCheckbox(
        stringResource(R.string.color_main), itemDetails.colorMain)
    )
    checkboxList.add(ItemDetailsCheckbox(
        stringResource(R.string.color_secondary), itemDetails.colorSecondary)
    )
    // Source
    checkboxList.add(ItemDetailsCheckbox(
        stringResource(R.string.source) + "・" + stringResource(R.string.source_name),
        itemDetails.sourceName)
    )
    checkboxList.add(ItemDetailsCheckbox(
        stringResource(R.string.source) + "・" + stringResource(R.string.source_alias),
        itemDetails.sourceAlias)
    )
    checkboxList.add(ItemDetailsCheckbox(
        stringResource(R.string.source) + "・" + stringResource(R.string.source_type),
        itemDetails.sourceType)
    )
    checkboxList.add(ItemDetailsCheckbox(
        stringResource(R.string.source) + "・" + stringResource(R.string.source_details),
        itemDetails.sourceDetails)
    )
    checkboxList.add(ItemDetailsCheckbox(
        stringResource(R.string.source) + "・" + stringResource(R.string.source_country),
        itemDetails.sourceCountry)
    )
    // ArchivalDetails
    checkboxList.add(ItemDetailsCheckbox(
        stringResource(R.string.archival_date), itemDetails.archivalDate))
    checkboxList.add(ItemDetailsCheckbox(
        stringResource(R.string.recipient_name), itemDetails.recipientName))
    checkboxList.add(ItemDetailsCheckbox(
        stringResource(R.string.recipient_alias), itemDetails.recipientAlias))
    checkboxList.add(ItemDetailsCheckbox(
        stringResource(R.string.archival_reason), itemDetails.archivalType))
    checkboxList.add(ItemDetailsCheckbox(
        stringResource(R.string.archival_details), itemDetails.archivalDetails))
    checkboxList.add(ItemDetailsCheckbox(
        stringResource(R.string.sold_price), itemDetails.price?.toString()))
    checkboxList.add(ItemDetailsCheckbox(
        stringResource(R.string.recipient_country), itemDetails.recipientCountry))

    return checkboxList
}
