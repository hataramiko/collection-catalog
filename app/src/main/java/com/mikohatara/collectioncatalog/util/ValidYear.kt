package com.mikohatara.collectioncatalog.util

fun String.isValidYear(): Boolean {
    val value = this.toIntOrNull() ?: return false
    return value in 1..9999
}
