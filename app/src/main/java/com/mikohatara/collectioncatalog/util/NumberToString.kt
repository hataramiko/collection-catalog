package com.mikohatara.collectioncatalog.util

import android.icu.text.DecimalFormat
import android.icu.text.MeasureFormat
import android.icu.text.NumberFormat
import android.icu.util.Currency
import java.util.Locale

fun Long.toCurrencyString(currencyCode: String): String {
    /*val format = NumberFormat.getCurrencyInstance()
    format.currency = Currency.getInstance(currencyCode)
    val fractions = Currency.getInstance(currencyCode).defaultFractionDigits
    val displayAmount = if (fractions > 0) this / 100.0 else this

    return format.format(displayAmount)*/

    val locale = Locale.getDefault()
    val format = NumberFormat.getCurrencyInstance(locale)
    (format as DecimalFormat).currency = Currency.getInstance(currencyCode)

    val fractions = Currency.getInstance(currencyCode).defaultFractionDigits
    val displayAmount = if (fractions > 0) this / 100.0 else this

    return format.format(displayAmount)
}

fun Int.toLengthString(): String {
    return "$this mm"
}

fun Double.toWeightString(): String {
    return "$this kg"
}
