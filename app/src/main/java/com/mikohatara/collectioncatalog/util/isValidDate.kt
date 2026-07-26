package com.mikohatara.collectioncatalog.util

import android.util.Log
import java.time.LocalDate
import java.time.format.DateTimeParseException

fun String.isValidDate(): Boolean {
    if (this.isBlank()) return false

    return try {
        LocalDate.parse(this)
        true
    } catch (e: DateTimeParseException) {
        Log.e("isValidDate",e.message, e)
        false
    }
}
