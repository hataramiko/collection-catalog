package com.mikohatara.collectioncatalog.util

import java.util.Calendar

fun getCurrentYear(): Int {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.YEAR)
}

fun getDateExample(): String {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    return "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
}
