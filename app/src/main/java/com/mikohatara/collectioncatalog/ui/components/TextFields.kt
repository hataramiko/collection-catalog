package com.mikohatara.collectioncatalog.ui.components

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.util.getCalendarLocale
import com.mikohatara.collectioncatalog.util.rememberCurrencyVisualTransformation
import com.mikohatara.collectioncatalog.util.rememberMeasurementVisualTransformation
import com.mikohatara.collectioncatalog.util.toFormattedDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.math.roundToInt

@Composable
fun EntryField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    hasTrailingIcon: Boolean = true,
    hasEntryDialog: Boolean = false,
    isCurrency: Boolean = false,
    localeCode: String = "",
    isMeasurement: Boolean = false,
    measurementUnit: String = ""
) {
    var showEntryDialog by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusManager = LocalFocusManager.current

    val currentValue = remember(value, isCurrency, localeCode, isMeasurement, measurementUnit) {
        if (isCurrency && value.isNotBlank()) {
            val longValue: Long = value.toLongOrNull() ?: 0L
            longValue.toString()
        } else if (isMeasurement && value.isNotBlank()) {
            val intValue: Int = value.toIntOrNull() ?: 0
            intValue.toString()
        } else {
            value
        }
    }
    val visualTransformation = if (isCurrency) {
        rememberCurrencyVisualTransformation(localeCode)
    } else if (isMeasurement) {
        rememberMeasurementVisualTransformation(measurementUnit, localeCode)
    } else VisualTransformation.None

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        OutlinedTextField(
            value = currentValue,
            onValueChange = { newValue ->
                if (isCurrency || isMeasurement) {
                    val rawValue = newValue.replace(Regex("\\D"), "")
                    onValueChange(rawValue)
                } else {
                    onValueChange(newValue)
                }
            },
            keyboardOptions = KeyboardOptions(
                capitalization = capitalization,
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            label = {
                Text(
                    text = label,
                    maxLines = if (singleLine) 1 else Int.MAX_VALUE,
                    overflow = TextOverflow.Ellipsis
                )
            },
            placeholder = placeholder,
            trailingIcon = { if (hasTrailingIcon && isFocused && currentValue.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_close_24),
                        contentDescription = null
                    )
                }
            }},
            modifier = Modifier.fillMaxWidth().weight(1f),
            enabled = enabled,
            singleLine = singleLine,
            interactionSource = interactionSource,
            visualTransformation = visualTransformation
        )
        if (hasEntryDialog && isFocused) {
            FilledTonalIconButton(
                onClick = { showEntryDialog = !showEntryDialog  },
                modifier = Modifier.padding(start = 10.dp, top = 8.dp, end = 2.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.rounded_edit_square),
                    contentDescription = null
                )
            }
        }
    }
    if (showEntryDialog) {
        EntryDialog(
            value = currentValue,
            onValueChange = { onValueChange(it) },
            label = label,
            onDismiss = { showEntryDialog = false },
            onConfirm = {
                showEntryDialog = false
                if (imeAction == ImeAction.Next) focusManager.moveFocus(FocusDirection.Next)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    dateValue: String,
    onDateSelected: (String) -> Unit,
    userCountry: String,
    modifier: Modifier = Modifier,
    dateFormat: Int = SimpleDateFormat.LONG
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusManager = LocalFocusManager.current

    val displayValue = dateValue.toFormattedDate(userCountry, dateFormat)
    val locale = getCalendarLocale(userCountry)
    val inputFormat = remember(locale) { SimpleDateFormat("yyyy-MM-dd", locale) }

    val initialSelectedDateMillis = remember(dateValue) {
        if (dateValue.isNotBlank()) {
            try {
                inputFormat.parse(dateValue)?.time
            } catch (e: Exception) {
                Log.e("DatePickerField", "Error parsing date: $dateValue", e)
                Calendar.getInstance().timeInMillis
            }
        } else {
            Calendar.getInstance().timeInMillis
        }
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis
    )

    // Any mention of SimpleDateFormat.SHORT || LONG from here on out is to differentiate
    // between behavior in FilterBottomSheet and ItemEntryScreen etc.
    // The logic could be more sound, but for the current use cases it should be okay.
    LaunchedEffect(dateFormat, isFocused) {
        if (dateFormat == SimpleDateFormat.SHORT && isFocused) {
            showDatePicker = true
        }
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        OutlinedTextField(
            value = displayValue,
            onValueChange = { showDatePicker = true },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            label = { Text(label) },
            maxLines = 1,
            readOnly = dateFormat == SimpleDateFormat.SHORT,
            trailingIcon = if (isFocused && displayValue.isNotEmpty()
                && dateFormat == SimpleDateFormat.LONG) {
                {
                    IconButton(onClick = { onDateSelected("") }) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_close_24),
                            contentDescription = null
                        )
                    }
                }
            } else null,
            interactionSource = interactionSource,
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
        if (isFocused && dateFormat == SimpleDateFormat.LONG) {
            FilledTonalIconButton(
                onClick = { showDatePicker = !showDatePicker  },
                modifier = Modifier.padding(start = 10.dp, top = 8.dp, end = 2.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.rounded_event),
                    contentDescription = null
                )
            }
        }
    }
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
                if (dateFormat == SimpleDateFormat.SHORT) {
                    focusManager.clearFocus()
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Date(millis)
                            onDateSelected(inputFormat.format(date))
                        }
                        if (dateFormat != SimpleDateFormat.SHORT) {
                            focusManager.moveFocus(FocusDirection.Next)
                        } else {
                            focusManager.clearFocus()
                        }
                    },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text(stringResource(R.string.ok_text))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        if (dateFormat == SimpleDateFormat.SHORT) {
                            focusManager.clearFocus()
                        }
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}

@Composable
fun FilterSliderRangeFields(
    sliderRange: ClosedRange<Float>,
    sliderPosition: ClosedRange<Float>,
    isExpanded: Boolean,
    onValueChange: (ClosedRange<Float>) -> Unit,
    cardColor: Color,
    isCurrency: Boolean = false,
    localeCode: String = "",
    isMeasurement: Boolean = false,
    measurementUnit: String = ""
) {
    var localMinTextValue by remember { mutableStateOf("") }
    var localMaxTextValue by remember { mutableStateOf("") }

    LaunchedEffect(sliderPosition) {
        localMinTextValue = sliderPosition.start.roundToInt().toString()
        localMaxTextValue = sliderPosition.endInclusive.roundToInt().toString()
    }

    Card(
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(cardColor),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        if (isExpanded) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                EntryField(
                    label = "",
                    value = localMinTextValue,
                    onValueChange = { localMinTextValue = it },
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                    isCurrency = isCurrency,
                    localeCode = localeCode,
                    isMeasurement = isMeasurement,
                    measurementUnit = measurementUnit,
                    modifier = Modifier
                        .weight(1f)
                        .offset(y = (-4).dp)
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused) {
                                val parsed = localMinTextValue
                                    .toFloatOrNull() ?: sliderRange.start
                                val validated = parsed
                                    .coerceIn(sliderRange.start, sliderPosition.endInclusive)

                                onValueChange(validated..sliderPosition.endInclusive)
                                localMinTextValue = validated.roundToInt().toString()
                            }
                        }
                )
                Text(
                    text = "–",
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                EntryField(
                    label = "",
                    value = localMaxTextValue,
                    onValueChange = { localMaxTextValue = it },
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                    isCurrency = isCurrency,
                    localeCode = localeCode,
                    isMeasurement = isMeasurement,
                    measurementUnit = measurementUnit,
                    modifier = Modifier
                        .weight(1f)
                        .offset(y = (-4).dp)
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused) {
                                val parsed = localMaxTextValue
                                    .toFloatOrNull() ?: sliderRange.endInclusive
                                val validated = parsed
                                    .coerceIn(sliderPosition.start, sliderRange.endInclusive)

                                onValueChange(sliderPosition.start..validated)
                                localMaxTextValue = validated.roundToInt().toString()
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun FilterSliderDateRangeFields(
    sliderRange: ClosedRange<Float>,
    sliderPosition: ClosedRange<Float>,
    isExpanded: Boolean,
    onValueChange: (ClosedRange<Float>) -> Unit,
    cardColor: Color,
    localeCode: String = "",
) {
    var localMinTextValue by remember { mutableStateOf("") }
    var localMaxTextValue by remember { mutableStateOf("") }

    val locale = getCalendarLocale(localeCode)
    val dateFormat = remember(locale) { SimpleDateFormat("yyyy-MM-dd", locale) }

    LaunchedEffect(sliderPosition) {
        val minDate = Date(sliderPosition.start.toLong())
        val maxDate = Date(sliderPosition.endInclusive.toLong())
        localMinTextValue = dateFormat.format(minDate)
        localMaxTextValue = dateFormat.format(maxDate)
    }

    Card(
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(cardColor),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        if (isExpanded) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                DatePickerField(
                    label = "",
                    dateValue = localMinTextValue,
                    onDateSelected = { newDateString ->
                        localMinTextValue = newDateString

                        try {
                            val time = dateFormat
                                .parse(newDateString)?.time ?: return@DatePickerField
                            val sliderRangeStart = sliderRange.start.toLong()
                            val sliderPositionEnd = sliderPosition.endInclusive.toLong()
                            val validated = time.coerceIn(sliderRangeStart, sliderPositionEnd)

                            onValueChange(validated.toFloat()..sliderPosition.endInclusive)
                        } catch (e: Exception) {
                            Log.e("FilterListDateRangeFields", "Parsing error", e)
                        }
                    },
                    userCountry = localeCode,
                    dateFormat = SimpleDateFormat.SHORT,
                    modifier = Modifier
                        .weight(1f)
                        .offset(y = (-4).dp)
                )
                Text(
                    text = "–",
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                DatePickerField(
                    label = "",
                    dateValue = localMaxTextValue,
                    onDateSelected = { newDateString ->
                        localMaxTextValue = newDateString

                        try {
                            val time = dateFormat
                                .parse(newDateString)?.time ?: return@DatePickerField
                            val sliderPositionStart = sliderPosition.start.toLong()
                            val sliderRangeEnd = sliderRange.endInclusive.toLong()
                            val validated = time.coerceIn(sliderPositionStart, sliderRangeEnd)

                            onValueChange(sliderPosition.start..validated.toFloat())
                        } catch (e: Exception) {
                            Log.e("FilterListDateRangeFields", "Parsing error", e)
                        }
                    },
                    userCountry = localeCode,
                    dateFormat = SimpleDateFormat.SHORT,
                    modifier = Modifier
                        .weight(1f)
                        .offset(y = (-4).dp)
                )
            }
        }
    }
}
