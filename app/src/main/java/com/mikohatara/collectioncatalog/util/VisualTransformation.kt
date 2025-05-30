package com.mikohatara.collectioncatalog.util

import android.icu.text.NumberFormat
import android.icu.util.Currency
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.core.text.isDigitsOnly
import java.util.Locale

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

private class CurrencyVisualTransformation(localeCode: String) : VisualTransformation {

    private val locale = Locale(localeCode, localeCode)
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
        val displayAmount = if (fractions > 0) (longValue / 100.0) else longValue
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

private class MeasurementVisualTransformation(unit: String) : VisualTransformation {

    private val measurementUnit = unit

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text.trim()

        if (originalText.isEmpty() || !originalText.isDigitsOnly()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val intValue = originalText.toIntOrNull() ?: 0
        val formattedText = "$intValue $measurementUnit"

        return TransformedText(
            AnnotatedString(formattedText),
            VisualTransformationOffsetMapping(originalText, formattedText)
        )
    }
}

@Composable
fun rememberMeasurementVisualTransformation(unit: String): VisualTransformation {
    val inspectionMode = LocalInspectionMode.current
    return remember(unit) {
        if (inspectionMode) {
            VisualTransformation.None
        } else {
            MeasurementVisualTransformation(unit)
        }
    }
}
