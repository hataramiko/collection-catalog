package com.mikohatara.collectioncatalog.util

import androidx.compose.ui.res.stringResource
import java.util.Locale

fun String.toCountryCode(): String {
    val countryCode = Locale.getAvailableLocales().find {
        it.displayCountry == this
    }?.country ?: Locale.getDefault().country ?: "FI"

    return countryCode
}

fun String.toDisplayCountry(): String {
    val displayCountry = Locale.getAvailableLocales().find {
        it.country == this
    }?.displayCountry ?: Locale.getDefault().displayCountry

    return displayCountry
}
