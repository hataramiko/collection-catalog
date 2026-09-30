package com.mikohatara.collectioncatalog.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.ItemDetails
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

class MassChangeField(
    val property: KProperty1<ItemDetails, *>,
    val databaseColumnName: String,
    @StringRes val labelResId: Int,
    @StringRes val labelPrefixResId: Int? = null,
    val valueType: KClass<*> = String::class,
    val hasDividerBefore: Boolean = false,
) {
    val isString: Boolean get() = valueType == String::class
    val isInt: Boolean get() = valueType == Int::class
    val isLong: Boolean get() = valueType == Long::class

    fun getLabel(context: Context): String {
        return if (labelPrefixResId != null) {
            "${context.getString(labelPrefixResId)}・${context.getString(labelResId)}"
        } else {
            context.getString(labelResId)
        }
    }
}

// TODO lines commented out with /**/ are pending implementation
val MASS_CHANGE_FIELDS: List<MassChangeField> = listOf(
    // CommonDetails
    MassChangeField(ItemDetails::country, "country", R.string.country),
    MassChangeField(ItemDetails::region1st, "region_1st", R.string.subdivision),
    MassChangeField(ItemDetails::region2nd, "region_2nd", R.string.region),
    MassChangeField(ItemDetails::region3rd, "region_3rd", R.string.region_second),
    MassChangeField(ItemDetails::type, "type", R.string.type),
    /*MassChangeField(ItemDetails::periodStart, "period_start", R.string.period_start, Int::class),
    MassChangeField(ItemDetails::periodEnd, "period_end", R.string.period_end, Int::class),
    MassChangeField(ItemDetails::year, "year", R.string.year, Int::class),*/
    // UniqueDetails
    /*MassChangeField(ItemDetails::notes, "notes", R.string.notes),
    MassChangeField(ItemDetails::vehicle, "vehicle", R.string.vehicle),
    MassChangeField(ItemDetails::date, "date", R.string.date),
    MassChangeField(ItemDetails::cost, "cost", R.string.cost, Long::class),
    MassChangeField(ItemDetails::value, "value", R.string.value, Long::class),*/
    MassChangeField(ItemDetails::status, "status", R.string.location, hasDividerBefore = true),
    // Size
    /*MassChangeField(ItemDetails::width, "width", R.string.width, Int::class),
    MassChangeField(ItemDetails::height, "height", R.string.height, Int::class),
    MassChangeField(ItemDetails::weight, "weight", R.string.weight, Int::class),*/
    // Color
    MassChangeField(ItemDetails::colorMain, "color_main", R.string.color_base, R.string.color, hasDividerBefore = true),
    MassChangeField(ItemDetails::colorSecondary, "color_secondary", R.string.color_characters, R.string.color),
    // Source
    MassChangeField(ItemDetails::sourceName, "source_name", R.string.source_name, R.string.source, hasDividerBefore = true),
    MassChangeField(ItemDetails::sourceAlias, "source_alias", R.string.source_alias, R.string.source),
    MassChangeField(ItemDetails::sourceType, "source_type", R.string.source_type, R.string.source),
    MassChangeField(ItemDetails::sourceCountry, "source_country", R.string.source_country, R.string.source),
    /*MassChangeField(ItemDetails::sourceDetails, "source_details", R.string.source_details, R.string.source),*/
    // ArchivalDetails
    /*MassChangeField(ItemDetails::archivalDate, "archival_date", R.string.archival_date, R.string.archival),*/
    MassChangeField(ItemDetails::archivalType, "archival_type", R.string.archival_reason, R.string.archival, hasDividerBefore = true),
    /*MassChangeField(ItemDetails::price, "price", R.string.sold_price, R.string.archival, Long::class),*/
    MassChangeField(ItemDetails::recipientName, "recipient_name", R.string.recipient_name, R.string.archival),
    MassChangeField(ItemDetails::recipientAlias, "recipient_alias", R.string.recipient_alias, R.string.archival),
    MassChangeField(ItemDetails::recipientCountry, "recipient_country", R.string.recipient_country, R.string.archival),
    /*MassChangeField(ItemDetails::archivalDetails, "archival_details", R.string.archival_details, R.string.archival)*/
)

// Currently limited valueType to String::class, replace with null for other types
fun getMassChangeFields(valueType: KClass<*>? = String::class): List<MassChangeField> {
    return if (valueType != null) {
        MASS_CHANGE_FIELDS.filter { it.valueType == valueType }
    } else {
        MASS_CHANGE_FIELDS
    }
}

@Composable
fun MassChangeField.getLabel(): String {
    return getLabel(LocalContext.current)
}
