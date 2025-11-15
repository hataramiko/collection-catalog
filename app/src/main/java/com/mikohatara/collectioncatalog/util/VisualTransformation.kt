package com.mikohatara.collectioncatalog.util

import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.core.text.isDigitsOnly

class VisualTransformationOffsetMapping(
    originalText: String, formattedText: String
) : OffsetMapping {

    private val originalLength = originalText.length
    private val indices = findDigitIndices(originalText, formattedText)

    private fun findDigitIndices(originalText: String, formattedText: String): List<Int> {
        val indices = mutableListOf<Int>()
        var currentIndex = 0

        for (digit in originalText) {
            val index = formattedText.indexOf(digit, currentIndex)
            if (index != -1) {
                indices.add(index)
                currentIndex = index + 1
            } else {
                return emptyList()
            }
        }
        return indices
    }

    override fun originalToTransformed(offset: Int): Int {
        if (offset >= originalLength) {
            return if (indices.isNotEmpty()) indices.last() + 1 else 0
        }
        return if (indices.isNotEmpty()) {
            indices.getOrElse(offset) { indices.last() + 1 }
        } else offset
    }

    override fun transformedToOriginal(offset: Int): Int {
        return indices.indexOfFirst { it >= offset }.takeIf { it != -1 } ?: originalLength
    }
}

private class CurrencyVisualTransformation(countryCode: String) : VisualTransformation {

    private val locale = getLocale(countryCode)
    private val currency = Currency.getInstance(locale) ?: "USD".let { Currency.getInstance(it) }
    private val fractions = currency.defaultFractionDigits

    private val numberFormatter = NumberFormat.getCurrencyInstance(locale).apply {
        this.currency = this@CurrencyVisualTransformation.currency
    }

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text.trim()

        if (originalText.isEmpty() || !originalText.isDigitsOnly()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val longValue = originalText.toLongOrNull() ?: 0L
        val displayAmount = when (fractions) {
            3 -> longValue / 1000.0
            2 -> longValue / 100.0
            1 -> longValue / 10.0
            else -> longValue
        }
        val formattedText = numberFormatter.format(displayAmount)

        return TransformedText(
            AnnotatedString(formattedText),
            VisualTransformationOffsetMapping(originalText, formattedText)
        )
    }
}

@Composable
fun rememberCurrencyVisualTransformation(localeCode: String): VisualTransformation {
    val inspectionMode = LocalInspectionMode.current
    return remember(localeCode) {
        if (inspectionMode) {
            VisualTransformation.None
        } else {
            CurrencyVisualTransformation(localeCode)
        }
    }
}

private class MeasurementVisualTransformation(
    unit: String,
    countryCode: String
) : VisualTransformation {

    private val measurementUnit = unit
    private val locale = getLocale(countryCode)

    private val integerFormatter = NumberFormat.getNumberInstance(locale).apply {
        isGroupingUsed = true
        maximumFractionDigits = 0
    }
    private val decimalFormatter = NumberFormat.getNumberInstance(locale).apply {
        isGroupingUsed = true
        minimumFractionDigits = 1
        maximumFractionDigits = 1
    }

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text.trim()

        if (originalText.isEmpty() || !originalText.isDigitsOnly()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val intValue = originalText.toIntOrNull() ?: 0
        val formattedValue = when (measurementUnit) {
            "in", "oz" -> {
                val decimalValue = intValue / 10.0
                decimalFormatter.format(decimalValue)
            }
            else -> {
                integerFormatter.format(intValue)
            }
        }
        val formattedText = "$formattedValue $measurementUnit"

        return TransformedText(
            AnnotatedString(formattedText),
            VisualTransformationOffsetMapping(originalText, formattedText)
        )
    }
}

@Composable
fun rememberMeasurementVisualTransformation(
    unit: String,
    localeCode: String
): VisualTransformation {
    val inspectionMode = LocalInspectionMode.current
    return remember(unit, localeCode) {
        if (inspectionMode) {
            VisualTransformation.None
        } else {
            MeasurementVisualTransformation(unit, localeCode)
        }
    }
}
