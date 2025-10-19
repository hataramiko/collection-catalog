package com.mikohatara.collectioncatalog.util

fun String.toCurrencyLongOrNull(): Long? {
    return this.filter { it.isDigit() }.toLongOrNull()
}

fun String.toMeasurementIntOrNull(): Int? {
    return this.filter { it.isDigit() }.toIntOrNull()?.takeIf { it > 0 }
}
