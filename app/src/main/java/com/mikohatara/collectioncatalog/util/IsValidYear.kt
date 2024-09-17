package com.mikohatara.collectioncatalog.util

fun <T>T.isValidYear(): Boolean {
    val value = when (this) {
        is Int -> this
        is String -> this.toIntOrNull()
        else -> null
    } ?: return false
    return value in 1..9999
}
