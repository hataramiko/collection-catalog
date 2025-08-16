package com.mikohatara.collectioncatalog.util

import android.icu.text.NumberFormat
import android.icu.util.Currency
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
    val displayAmount = if (fractions > 0) this / 100.0 else this

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

fun Int.toMeasurementString(unit: String): String {
    return "$this $unit"
}
