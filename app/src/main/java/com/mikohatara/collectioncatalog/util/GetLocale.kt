package com.mikohatara.collectioncatalog.util

import java.util.Locale

fun getLocale(userLocale: String): Locale {
    val appLocale = Locale.getDefault()
    val appLanguage = appLocale.language ?: "en"
    val currentLocale = Locale.Builder()
        .setLanguage(appLanguage)
        .setRegion(userLocale)
        .build()

    return currentLocale
}
