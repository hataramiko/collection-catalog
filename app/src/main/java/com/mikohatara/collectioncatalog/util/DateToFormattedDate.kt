package com.mikohatara.collectioncatalog.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

fun String.toFormattedDate(
    countryCode: String,
    dateInstanceStyle: Int = SimpleDateFormat.LONG
): String {
    val calendarLocale = getCalendarLocale(countryCode)

    val inputFormat = SimpleDateFormat("yyyy-MM-dd", calendarLocale)
    val outputFormat = SimpleDateFormat.getDateInstance(dateInstanceStyle, calendarLocale)

    return try {
        val date = inputFormat.parse(this)
        date?.let { outputFormat.format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}

fun String.toTimestamp(timeModifier: Long = 0): Long {
    // There is some discrepancy between the timestamps of "minDates" and
    // the intended date for display, e.g. "2000-01-01" displays as "1999-12-31".
    // The timeModifier is a temporary solution to circumvent this.
    // TODO implement a better solution for the "minDate" to display value discrepancy
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")

    return try {
        val date = dateFormat.parse(this)
        (date?.time?.plus(timeModifier)) ?: 0L
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
