package com.mikohatara.collectioncatalog.util

import java.text.SimpleDateFormat
import java.util.Locale

fun String.toFormattedDate(localeCode: String): String {
    val locale = Locale.getDefault() //TODO Locale(localeCode)
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", locale)
    val outputFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, locale)

    return try {
        val date = inputFormat.parse(this)
        date?.let { outputFormat.format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}
