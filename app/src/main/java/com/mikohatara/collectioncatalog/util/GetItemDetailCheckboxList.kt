package com.mikohatara.collectioncatalog.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.ItemDetails
import kotlin.reflect.KProperty1

data class ItemDetailsCheckbox(
    val label: String,
    val value: String? = null
)

private data class CheckboxField(
    @StringRes val labelResId: Int,
    val property: KProperty1<ItemDetails, *>,
    @StringRes val prefixResId: Int? = null
)

private val CHECKBOX_FIELDS = listOf(
    CheckboxField(R.string.reg_no, ItemDetails::regNo),
    // CommonDetails
    CheckboxField(R.string.country, ItemDetails::country),
    CheckboxField(R.string.subdivision, ItemDetails::region1st),
    CheckboxField(R.string.region, ItemDetails::region2nd),
    CheckboxField(R.string.region_second, ItemDetails::region3rd),
    CheckboxField(R.string.type, ItemDetails::type),
    CheckboxField(R.string.period_start, ItemDetails::periodStart),
    CheckboxField(R.string.period_end, ItemDetails::periodEnd),
    CheckboxField(R.string.year, ItemDetails::year),
    // UniqueDetails, minus regNo & imagePath
    // regNo is on top of the list, imagePath is a separate item
    CheckboxField(R.string.notes, ItemDetails::notes),
    CheckboxField(R.string.vehicle, ItemDetails::vehicle),
    CheckboxField(R.string.date, ItemDetails::date),
    CheckboxField(R.string.cost, ItemDetails::cost),
    CheckboxField(R.string.value, ItemDetails::value),
    CheckboxField(R.string.location, ItemDetails::status),
    // Size
    CheckboxField(R.string.width, ItemDetails::width),
    CheckboxField(R.string.height, ItemDetails::height),
    CheckboxField(R.string.weight, ItemDetails::weight),
    // Color
    CheckboxField(R.string.color_main, ItemDetails::colorMain),
    CheckboxField(R.string.color_secondary, ItemDetails::colorSecondary),
    // Source
    CheckboxField(R.string.source_name, ItemDetails::sourceName, R.string.source),
    CheckboxField(R.string.source_alias, ItemDetails::sourceAlias, R.string.source),
    CheckboxField(R.string.source_type, ItemDetails::sourceType, R.string.source),
    CheckboxField(R.string.source_details, ItemDetails::sourceDetails, R.string.source),
    CheckboxField(R.string.source_country, ItemDetails::sourceCountry, R.string.source),
    // ArchivalDetails
    CheckboxField(R.string.archival_date, ItemDetails::archivalDate),
    CheckboxField(R.string.recipient_name, ItemDetails::recipientName),
    CheckboxField(R.string.recipient_alias, ItemDetails::recipientAlias),
    CheckboxField(R.string.archival_reason, ItemDetails::archivalType),
    CheckboxField(R.string.archival_details, ItemDetails::archivalDetails),
    CheckboxField(R.string.sold_price, ItemDetails::price),
    CheckboxField(R.string.recipient_country, ItemDetails::recipientCountry)
)

@Composable
fun getItemDetailsCheckboxList(itemDetails: ItemDetails): List<ItemDetailsCheckbox> {
    return CHECKBOX_FIELDS.map { field ->
        val label = if (field.prefixResId != null) {
            "${stringResource(field.prefixResId)}・${stringResource(field.labelResId)}"
        } else {
            stringResource(field.labelResId)
        }

        ItemDetailsCheckbox(
            label = label,
            value = field.property.get(itemDetails)?.toString()
        )
    }
}
