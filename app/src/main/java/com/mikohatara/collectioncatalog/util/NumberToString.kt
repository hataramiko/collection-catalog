package com.mikohatara.collectioncatalog.util

import android.icu.text.NumberFormat
import android.icu.util.Currency
import java.util.Locale

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

fun Int.toLengthString(): String {
    return "$this mm"
}

fun Double.toWeightString(): String {
    return "$this kg"
}
