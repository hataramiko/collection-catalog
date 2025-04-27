package com.mikohatara.collectioncatalog.util

import java.util.Calendar

fun getCurrentYear(): Int {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.YEAR)
}
