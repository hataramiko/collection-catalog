package com.mikohatara.collectioncatalog.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

fun String.toFormattedDate(
    localeCode: String,
    dateInstanceStyle: Int = SimpleDateFormat.LONG
): String {
    val locale = Locale.getDefault() //TODO Locale(localeCode)
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", locale)
    val outputFormat = SimpleDateFormat.getDateInstance(dateInstanceStyle, locale)

    return try {
        val date = inputFormat.parse(this)
        date?.let { outputFormat.format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}

fun String.toTimestamp(): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")

    return try {
        val date = dateFormat.parse(this)
        date?.time ?: 0L
    } catch (e: Exception) {
        0L
    }
}

fun Long.toDateString(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")

    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.timeInMillis = this

    return try {
        dateFormat.format(calendar.time)
    } catch (e: Exception) {
        ""
    }
}
