package com.mikohatara.collectioncatalog.util

import android.util.Log
import java.util.Currency
import java.util.Locale

// Parked for the time being. Might be useful if and when adding a separate setting for currency
fun getCurrencyCompound(currencyCode: String, countryCode: String): String {
    val appLangLocale = Locale.getDefault()
    val userLocale = getLocale(countryCode)

    return try {
        val currency = Currency.getInstance(currencyCode)

        val currencyDisplayName = currency.getDisplayName(appLangLocale)
        val currencySymbol = currency.getSymbol(userLocale)

        if (currencyCode == currencySymbol) {
            "$currencyCode・$currencyDisplayName"
        } else {
            "$currencyCode ($currencySymbol)・$currencyDisplayName"
        }
    } catch (e: Exception) {
        Log.e("getCurrencyCompound", e.message, e)
        currencyCode
    }
}
