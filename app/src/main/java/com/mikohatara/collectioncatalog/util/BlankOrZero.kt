package com.mikohatara.collectioncatalog.util

fun String.isBlankOrZero(): Boolean {
    if (this.isBlank()) return true
    if (this.trim() == "0") return true
    try {
        val doubleValue = this.toDouble()
        return doubleValue == 0.0
    } catch (e: NumberFormatException) {
        return false
    }
}
