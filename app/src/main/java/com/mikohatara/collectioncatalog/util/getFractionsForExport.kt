package com.mikohatara.collectioncatalog.util

import android.icu.util.Currency
import android.icu.util.MeasureUnit
import java.util.Locale

fun getCurrencyFractions(countryCode: String): Int {
    val locale = Locale(countryCode, countryCode)
    val currency = Currency.getInstance(locale) ?: "USD".let { Currency.getInstance(it) }
    return currency.defaultFractionDigits
}

fun getMeasurementUnitFractions(unit: MeasureUnit): Int {
    return when (unit) {
        MeasureUnit.MILLIMETER -> 0
        MeasureUnit.GRAM -> 0
        MeasureUnit.INCH -> 1
        MeasureUnit.OUNCE -> 1
        else -> 0
    }
}
