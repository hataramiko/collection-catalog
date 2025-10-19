package com.mikohatara.collectioncatalog.util

import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.icu.util.MeasureUnit
import java.util.Locale

fun Int.toFormattedString(countryCode: String): String {
    val locale = Locale(countryCode, countryCode)
    val format = NumberFormat.getInstance(locale)

    return format.format(this)
}

fun Long.toCurrencyString(countryCode: String): String {
    val locale = Locale(countryCode, countryCode)
    val currency = Currency.getInstance(locale) ?: "USD".let { Currency.getInstance(it) }
    val format = NumberFormat.getCurrencyInstance(locale).apply {
        this.currency = currency
    }
    val fractions = currency.defaultFractionDigits
    val displayAmount = when (fractions) {
        3 -> this / 1000.0
        2 -> this / 100.0
        1 -> this / 10.0
        else -> this
    }

    return format.format(displayAmount)
}

fun Float.toPercentage(countryCode: String): String {
    val locale = Locale(countryCode, countryCode)
    val format = NumberFormat.getPercentInstance(locale).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }

    return if (this < 0f || this.isNaN() || this.isInfinite()) {
        format.format(0f)
    } else {
        format.format(this)
    }
}

fun Int.toMeasurementString(unit: MeasureUnit): String {
    return when (unit) {
        MeasureUnit.MILLIMETER -> "$this mm"
        MeasureUnit.GRAM -> "$this g"
        MeasureUnit.INCH, MeasureUnit.OUNCE -> {
            val symbol = if (unit == MeasureUnit.INCH) " in" else " oz"

            if (this % 10 == 0) {
                "${this / 10}$symbol"
            } else {
                val numberFormatter = NumberFormat.getNumberInstance(Locale.ROOT).apply {
                    isGroupingUsed = false
                    minimumFractionDigits = 1
                    maximumFractionDigits = 1
                }
                val decimalValue = this / 10.0
                val formattedValue = numberFormatter.format(decimalValue)
                "$formattedValue$symbol"
            }
        }
        else -> this.toString()
    }
}

fun getMeasurementUnitSymbol(unit: MeasureUnit): String {
    return when (unit) {
        MeasureUnit.MILLIMETER -> "mm"
        MeasureUnit.GRAM -> "g"
        MeasureUnit.INCH -> "in"
        MeasureUnit.OUNCE -> "oz"
        else -> ""
    }
}
