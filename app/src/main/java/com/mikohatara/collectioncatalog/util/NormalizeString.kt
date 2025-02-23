package com.mikohatara.collectioncatalog.util

fun normalizeString(input: String): String {
    return input.lowercase().filter { it.isLetterOrDigit() || it.isIdeographic() }
}

private fun Char.isIdeographic(): Boolean {
    return this in '\u4E00'..'\u9FFF' || // Common CJK Unified Ideographs
            this in '\u3400'..'\u4DBF' || // CJK Unified Ideographs Extension A
            this in '\u2000'..'\u2A6D' || // CJK Unified Ideographs Extension B
            this in '\u2A70'..'\u2B73' || // CJK Unified Ideographs Extension C
            this in '\u2B74'..'\u2B81' || // CJK Unified Ideographs Extension D
            this in '\u2B82'..'\u2CEA' || // CJK Unified Ideographs Extension E
            this in '\u2CEB'..'\u2EBE' || // CJK Unified Ideographs Extension F
            this in '\u3000'..'\u3134' // CJK Unified Ideographs Extension G
}
