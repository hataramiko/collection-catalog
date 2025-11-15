package com.mikohatara.collectioncatalog.util

import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.icu.util.MeasureUnit
import android.util.Log
import java.util.Locale
import kotlin.math.pow

fun Int.toFormattedString(countryCode: String): String {
    val locale = getLocale(countryCode)
    val format = NumberFormat.getInstance(locale)

    return format.format(this)
}

fun Long.toCurrencyString(countryCode: String): String {
    val locale = getLocale(countryCode)
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
    val locale = getLocale(countryCode)
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

fun Int.toMeasurementString(unit: MeasureUnit, countryCode: String): String {
    return when (unit) {
        MeasureUnit.MILLIMETER -> "${this.toFormattedString(countryCode)} mm"
        MeasureUnit.GRAM -> "${this.toFormattedString(countryCode)} g"
        MeasureUnit.INCH, MeasureUnit.OUNCE -> {
            val symbol = if (unit == MeasureUnit.INCH) " in" else " oz"

            if (this % 10 == 0) {
                "${this / 10}$symbol"
            } else {
                val locale = getLocale(countryCode)
                val numberFormatter = NumberFormat.getNumberInstance(locale).apply {
                    isGroupingUsed = true
                    minimumFractionDigits = 1
                    maximumFractionDigits = 1
                }
                val decimalValue = this / 10.0
                val formattedValue = numberFormatter.format(decimalValue)
                "$formattedValue$symbol"
            }
        }
        else -> this.toFormattedString(countryCode)
    }
}

fun getCurrencySymbol(countryCode: String): String {
    val locale = getLocale(countryCode)
    val currency = Currency.getInstance(locale) ?: "USD".let { Currency.getInstance(it) }

    return currency.getSymbol(locale)
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

fun Long?.toExportNumeralString(fractions: Int, isCurrency: Boolean = false): String? {
    // If the numeral value is null, or less than or equal to zero, return a null string.
    // However, when dealing with currency values, zero is a valid output.
    if (this == null || !isCurrency && this <= 0) return null

    // In case of no fractions, do a simple string conversion.
    if (fractions <= 0) return this.toString()

    // Otherwise, calculate and format the correct value based on the number of fractions.
    // Fallback to a string value stating error if something goes wrong.
    try {
        val displayAmount = this / 10.0.pow(fractions)
        return "%.${fractions}f".format(Locale.ROOT, displayAmount)
    } catch (e: Exception) {
        Log.e("Long?.toExportNumeralString", e.message, e)
        return "EXPORT-ERROR" //TODO localized error output?
    }
}

fun Int?.toExportNumeralString(fractions: Int, isCurrency: Boolean = false): String? {
    return this?.toLong().toExportNumeralString(fractions, isCurrency)
}
