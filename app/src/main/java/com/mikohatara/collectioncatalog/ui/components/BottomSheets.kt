package com.mikohatara.collectioncatalog.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.icu.util.MeasureUnit
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Collection
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.ui.catalog.FilterData
import com.mikohatara.collectioncatalog.ui.catalog.SortBy
import com.mikohatara.collectioncatalog.util.getSortByText
import com.mikohatara.collectioncatalog.util.isCollectionColor
import com.mikohatara.collectioncatalog.util.toColor
import com.mikohatara.collectioncatalog.util.toCurrencyString
import com.mikohatara.collectioncatalog.util.toDateString
import com.mikohatara.collectioncatalog.util.toFormattedDate
import com.mikohatara.collectioncatalog.util.toMeasurementString
import java.text.SimpleDateFormat
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortByBottomSheet(
    onDismiss: () -> Unit,
    onClick: (SortBy) -> Unit,
    sortByOptions: List<SortBy>,
    selectedSortBy: SortBy
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
    ) {
        BottomSheetHeader(stringResource(R.string.sort_by))
        Column(
            modifier = Modifier
                .selectableGroup()
                .padding(top = 8.dp, bottom = 16.dp)
        ) {
            sortByOptions.forEach { sortByOption ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (sortByOption == selectedSortBy),
                            onClick = { onClick(sortByOption) },
                            role = Role.RadioButton
                        )
                ) {
                    RadioButton(
                        selected = (sortByOption == selectedSortBy),
                        onClick = null,
                        modifier = Modifier
                            .padding(start = 32.dp, top = 12.dp, end = 16.dp, bottom = 12.dp)
                    )
                    Text(getSortByText(sortByOption))
                }
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FilterBottomSheet(
    itemType: ItemType,
    onDismiss: () -> Unit,
    filters: FilterData,
    filterCount: Int,
    onApply: () -> Unit,
    onReset: () -> Unit,
    localeCode: String,
    lengthUnit: MeasureUnit,
    countries: Set<String>,
    toggleCountry: (String) -> Unit,
    types: Set<String>,
    toggleType: (String) -> Unit,
    // Period uses yearSliderRange alongside Year
    periodSliderPosition: ClosedRange<Float>? = null,
    onPeriodSliderChange: ((ClosedRange<Float>) -> Unit)? = null,
    yearSliderRange: ClosedRange<Float>? = null,
    yearSliderPosition: ClosedRange<Float>? = null,
    onYearSliderChange: ((ClosedRange<Float>) -> Unit)? = null,
    hasVehicle: Boolean = false,
    toggleVehicleSwitch: (() -> Unit)? = null,
    dateSliderRange: ClosedRange<Float>? = null,
    dateSliderPosition: ClosedRange<Float>? = null,
    onDateSliderChange: ((ClosedRange<Float>) -> Unit)? = null,
    costSliderRange: ClosedRange<Float>? = null,
    costSliderPosition: ClosedRange<Float>? = null,
    onCostSliderChange: ((ClosedRange<Float>) -> Unit)? = null,
    valueSliderRange: ClosedRange<Float>? = null,
    valueSliderPosition: ClosedRange<Float>? = null,
    onValueSliderChange: ((ClosedRange<Float>) -> Unit)? = null,
    locations: Set<String>? = null,
    toggleLocation: ((String) -> Unit)? = null,
    widthSliderRange: ClosedRange<Float>? = null,
    widthSliderPosition: ClosedRange<Float>? = null,
    onWidthSliderChange: ((ClosedRange<Float>) -> Unit)? = null,
    colorsMain: Set<String>? = null,
    toggleColorMain: ((String) -> Unit)? = null,
    colorsSecondary: Set<String>? = null,
    toggleColorSecondary: ((String) -> Unit)? = null,
    sourceTypes: Set<String>? = null,
    toggleSourceType: ((String) -> Unit)? = null,
    sourceCountries: Set<String>? = null,
    toggleSourceCountry: ((String) -> Unit)? = null,
    archivalDateSliderRange: ClosedRange<Float>? = null,
    archivalDateSliderPosition: ClosedRange<Float>? = null,
    onArchivalDateSliderChange: ((ClosedRange<Float>) -> Unit)? = null,
    archivalReasons: Set<String>? = null,
    toggleArchivalReason: ((String) -> Unit)? = null,
    recipientCountries: Set<String>? = null,
    toggleRecipientCountry: ((String) -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState()
    var sheetHeight by remember { mutableIntStateOf(0) }

    var isCountriesExpanded by remember { mutableStateOf(false) }
    var isTypesExpanded by remember { mutableStateOf(false) }
    var isPeriodExpanded by remember { mutableStateOf(false) }
    var isYearExpanded by remember { mutableStateOf(false) }
    var isDateExpanded by remember { mutableStateOf(false) }
    var isCostExpanded by remember { mutableStateOf(false) }
    var isValueExpanded by remember { mutableStateOf(false) }
    var isLocationsExpanded by remember { mutableStateOf(false) }
    var isWidthExpanded by remember { mutableStateOf(false) }
    var isColorsMainExpanded by remember { mutableStateOf(false) }
    var isColorsSecondaryExpanded by remember { mutableStateOf(false) }
    var isSourceTypesExpanded by remember { mutableStateOf(false) }
    var isSourceCountriesExpanded by remember { mutableStateOf(false) }
    var isArchivalDateExpanded by remember { mutableStateOf(false) }
    var isArchivalReasonsExpanded by remember { mutableStateOf(false) }
    var isRecipientCountriesExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .onGloballyPositioned { sheetHeight = it.size.height }
    ) {
        BottomSheetHeader(stringResource(R.string.filter))
        Box(
            modifier = Modifier.weight(1f)
        ) {
            LazyColumn {
                stickyHeader {
                    FilterListLabel(
                        label = stringResource(R.string.country),
                        activeFilters = filters.country,
                        isExpanded = isCountriesExpanded,
                        onExpand = { isCountriesExpanded = !isCountriesExpanded }
                    )
                }
                item {
                    FilterListCheckboxes(
                        filterOptions = countries,
                        activeFilters = filters.country,
                        isExpanded = isCountriesExpanded,
                        onToggleFilter = { toggleCountry(it) }
                    )
                }
                stickyHeader {
                    FilterListLabel(
                        label = stringResource(R.string.type),
                        activeFilters = filters.type,
                        isExpanded = isTypesExpanded,
                        onExpand = { isTypesExpanded = !isTypesExpanded }
                    )
                }
                item {
                    FilterListCheckboxes(
                        filterOptions = types,
                        activeFilters = filters.type,
                        isExpanded = isTypesExpanded,
                        onToggleFilter = { toggleType(it) }
                    )
                }
                if (yearSliderRange != null && periodSliderPosition != null &&
                    onPeriodSliderChange != null) {
                    stickyHeader {
                        val minValue = periodSliderPosition.start.roundToInt()
                        val maxValue = periodSliderPosition.endInclusive.roundToInt()

                        FilterListLabel(
                            label = stringResource(R.string.period),
                            activeFilters = emptySet(),
                            isExpanded = isPeriodExpanded,
                            onExpand = { isPeriodExpanded = !isPeriodExpanded },
                            value = "$minValue – $maxValue",
                            isSliderActive = periodSliderPosition != yearSliderRange.start..yearSliderRange.endInclusive
                        )
                    }
                    item {
                        FilterListSlider(
                            sliderRange = yearSliderRange,
                            sliderPosition = periodSliderPosition,
                            isExpanded = isPeriodExpanded,
                            onSliderChange = { newSliderPosition ->
                                onPeriodSliderChange(newSliderPosition)
                            }
                        )
                    }
                }
                if (yearSliderRange != null && yearSliderPosition != null &&
                    onYearSliderChange != null) {
                    stickyHeader {
                        val minValue = yearSliderPosition.start.roundToInt()
                        val maxValue = yearSliderPosition.endInclusive.roundToInt()

                        FilterListLabel(
                            label = stringResource(R.string.year),
                            activeFilters = emptySet(),
                            isExpanded = isYearExpanded,
                            onExpand = { isYearExpanded = !isYearExpanded },
                            value = "$minValue – $maxValue",
                            isSliderActive = yearSliderPosition != yearSliderRange.start..yearSliderRange.endInclusive
                        )
                    }
                    item {
                        FilterListSlider(
                            sliderRange = yearSliderRange,
                            sliderPosition = yearSliderPosition,
                            isExpanded = isYearExpanded,
                            onSliderChange = { newSliderPosition ->
                                onYearSliderChange(newSliderPosition)
                            }
                        )
                    }
                }
                if (itemType != ItemType.WANTED_PLATE && toggleVehicleSwitch != null) {
                    stickyHeader {
                        FilterListSwitch(
                            label = stringResource(R.string.vehicle),
                            isFilterActive = hasVehicle,
                            onToggle = toggleVehicleSwitch
                        )
                    }
                }
                if (itemType != ItemType.WANTED_PLATE) {
                    if (dateSliderRange != null && dateSliderPosition != null &&
                        onDateSliderChange != null) {
                        stickyHeader {
                            val minValue = dateSliderPosition.start.roundToLong()
                            val maxValue = dateSliderPosition.endInclusive.roundToLong()
                            val minValueString = minValue
                                .toDateString()
                                .toFormattedDate(localeCode, SimpleDateFormat.SHORT)
                            val maxValueString = maxValue
                                .toDateString()
                                .toFormattedDate(localeCode, SimpleDateFormat.SHORT)

                            FilterListLabel(
                                label = stringResource(R.string.date),
                                activeFilters = emptySet(),
                                isExpanded = isDateExpanded,
                                onExpand = { isDateExpanded = !isDateExpanded },
                                value = "$minValueString – $maxValueString",
                                isSliderActive = dateSliderPosition != dateSliderRange.start..dateSliderRange.endInclusive
                            )
                        }
                        item {
                            FilterListSlider(
                                sliderRange = dateSliderRange,
                                sliderPosition = dateSliderPosition,
                                isExpanded = isDateExpanded,
                                onSliderChange = { newSliderPosition ->
                                    onDateSliderChange(newSliderPosition)
                                }
                            )
                        }
                    }
                    if (costSliderRange != null && costSliderPosition != null &&
                        onCostSliderChange != null) {
                        stickyHeader {
                            val minValue = costSliderPosition.start.roundToLong()
                            val maxValue = costSliderPosition.endInclusive.roundToLong()

                            FilterListLabel(
                                label = stringResource(R.string.cost),
                                activeFilters = emptySet(),
                                isExpanded = isCostExpanded,
                                onExpand = { isCostExpanded = !isCostExpanded },
                                value = "${minValue.toCurrencyString(localeCode)} – ${maxValue.toCurrencyString(localeCode)}",
                                isSliderActive = costSliderPosition != costSliderRange.start..costSliderRange.endInclusive
                            )
                        }
                        item {
                            FilterListSlider(
                                sliderRange = costSliderRange,
                                sliderPosition = costSliderPosition,
                                isExpanded = isCostExpanded,
                                onSliderChange = { newSliderPosition ->
                                    onCostSliderChange(newSliderPosition)
                                }
                            )
                        }
                    }
                }
                if (itemType == ItemType.PLATE) {
                    if (valueSliderRange != null && valueSliderPosition != null &&
                        onValueSliderChange != null) {
                        stickyHeader {
                            val minValue = valueSliderPosition.start.roundToLong()
                            val maxValue = valueSliderPosition.endInclusive.roundToLong()

                            FilterListLabel(
                                label = stringResource(R.string.value),
                                activeFilters = emptySet(),
                                isExpanded = isValueExpanded,
                                onExpand = { isValueExpanded = !isValueExpanded },
                                value = "${minValue.toCurrencyString(localeCode)} – ${maxValue.toCurrencyString(localeCode)}",
                                isSliderActive = valueSliderPosition != valueSliderRange.start..valueSliderRange.endInclusive
                            )
                        }
                        item {
                            FilterListSlider(
                                sliderRange = valueSliderRange,
                                sliderPosition = valueSliderPosition,
                                isExpanded = isValueExpanded,
                                onSliderChange = { newSliderPosition ->
                                    onValueSliderChange(newSliderPosition)
                                }
                            )
                        }
                    }
                    if (locations != null && toggleLocation != null) {
                        stickyHeader {
                            FilterListLabel(
                                label = stringResource(R.string.location),
                                activeFilters = filters.location,
                                isExpanded = isLocationsExpanded,
                                onExpand = { isLocationsExpanded = !isLocationsExpanded }
                            )
                        }
                        item {
                            FilterListCheckboxes(
                                filterOptions = locations,
                                activeFilters = filters.location,
                                isExpanded = isLocationsExpanded,
                                onToggleFilter = { toggleLocation(it) }
                            )
                        }
                    }
                }
                if (colorsMain != null || colorsSecondary != null) {
                    stickyHeader { FilterSubheader(text = stringResource(R.string.physical_attributes)) }
                }
                if (widthSliderRange != null && widthSliderPosition != null &&
                    onWidthSliderChange != null) {
                    stickyHeader {
                        val minValue = widthSliderPosition.start.roundToInt()
                        val maxValue = widthSliderPosition.endInclusive.roundToInt()

                        FilterListLabel(
                            label = stringResource(R.string.width),
                            activeFilters = emptySet(),
                            isExpanded = isWidthExpanded,
                            onExpand = { isWidthExpanded = !isWidthExpanded },
                            value = "${minValue.toMeasurementString(lengthUnit, localeCode)} – ${maxValue.toMeasurementString(lengthUnit, localeCode)}",
                            isSliderActive = widthSliderPosition != widthSliderRange.start..widthSliderRange.endInclusive
                        )
                    }
                    item {
                        FilterListSlider(
                            sliderRange = widthSliderRange,
                            sliderPosition = widthSliderPosition,
                            isExpanded = isWidthExpanded,
                            onSliderChange = { newSliderPosition ->
                                onWidthSliderChange(newSliderPosition)
                            }
                        )
                    }
                }
                if (colorsMain != null && toggleColorMain != null) {
                    stickyHeader {
                        FilterListLabel(
                            label = stringResource(R.string.color_main),
                            activeFilters = filters.colorMain,
                            isExpanded = isColorsMainExpanded,
                            onExpand = { isColorsMainExpanded = !isColorsMainExpanded }
                        )
                    }
                    item {
                        FilterListCheckboxes(
                            filterOptions = colorsMain,
                            activeFilters = filters.colorMain,
                            isExpanded = isColorsMainExpanded,
                            onToggleFilter = { toggleColorMain(it) }
                        )
                    }
                }
                if (colorsSecondary != null && toggleColorSecondary != null) {
                    stickyHeader {
                        FilterListLabel(
                            label = stringResource(R.string.color_secondary),
                            activeFilters = filters.colorSecondary,
                            isExpanded = isColorsSecondaryExpanded,
                            onExpand = { isColorsSecondaryExpanded = !isColorsSecondaryExpanded }
                        )
                    }
                    item {
                        FilterListCheckboxes(
                            filterOptions = colorsSecondary,
                            activeFilters = filters.colorSecondary,
                            isExpanded = isColorsSecondaryExpanded,
                            onToggleFilter = { toggleColorSecondary(it) }
                        )
                    }
                }
                if (itemType != ItemType.WANTED_PLATE) {
                    stickyHeader { FilterSubheader(text = stringResource(R.string.source)) }
                    if (sourceTypes != null && toggleSourceType != null) {
                        stickyHeader {
                            FilterListLabel(
                                label = stringResource(R.string.source_type),
                                activeFilters = filters.sourceType,
                                isExpanded = isSourceTypesExpanded,
                                onExpand = { isSourceTypesExpanded = !isSourceTypesExpanded }
                            )
                        }
                        item {
                            FilterListCheckboxes(
                                filterOptions = sourceTypes,
                                activeFilters = filters.sourceType,
                                isExpanded = isSourceTypesExpanded,
                                onToggleFilter = { toggleSourceType(it) }
                            )
                        }
                    }
                    if (sourceCountries != null && toggleSourceCountry != null) {
                        stickyHeader {
                            FilterListLabel(
                                label = stringResource(R.string.source_country),
                                activeFilters = filters.sourceCountry,
                                isExpanded = isSourceCountriesExpanded,
                                onExpand = {
                                    isSourceCountriesExpanded = !isSourceCountriesExpanded
                                }
                            )
                        }
                        item {
                            FilterListCheckboxes(
                                filterOptions = sourceCountries,
                                activeFilters = filters.sourceCountry,
                                isExpanded = isSourceCountriesExpanded,
                                onToggleFilter = { toggleSourceCountry(it) }
                            )
                        }
                    }
                }
                if (itemType == ItemType.FORMER_PLATE) {
                    stickyHeader { FilterSubheader(text = stringResource(R.string.archival)) }
                    if (archivalDateSliderRange != null && archivalDateSliderPosition != null &&
                        onArchivalDateSliderChange != null) {
                        stickyHeader {
                            val minValue = archivalDateSliderPosition.start.roundToLong()
                            val maxValue = archivalDateSliderPosition.endInclusive.roundToLong()
                            val minValueString = minValue
                                .toDateString()
                                .toFormattedDate(localeCode, SimpleDateFormat.SHORT)
                            val maxValueString = maxValue
                                .toDateString()
                                .toFormattedDate(localeCode, SimpleDateFormat.SHORT)

                            FilterListLabel(
                                label = stringResource(R.string.archival_date),
                                activeFilters = emptySet(),
                                isExpanded = isArchivalDateExpanded,
                                onExpand = { isArchivalDateExpanded = !isArchivalDateExpanded },
                                value = "$minValueString – $maxValueString",
                                isSliderActive =archivalDateSliderPosition != archivalDateSliderRange.start..archivalDateSliderRange.endInclusive
                            )
                        }
                        item {
                            FilterListSlider(
                                sliderRange = archivalDateSliderRange,
                                sliderPosition = archivalDateSliderPosition,
                                isExpanded = isArchivalDateExpanded,
                                onSliderChange = { newSliderPosition ->
                                    onArchivalDateSliderChange(newSliderPosition)
                                }
                            )
                        }
                    }
                    if (archivalReasons != null && toggleArchivalReason != null) {
                        stickyHeader {
                            FilterListLabel(
                                label = stringResource(R.string.archival_reason),
                                activeFilters = filters.archivalReason,
                                isExpanded = isArchivalReasonsExpanded,
                                onExpand = {
                                    isArchivalReasonsExpanded = !isArchivalReasonsExpanded
                                }
                            )
                        }
                        item {
                            FilterListCheckboxes(
                                filterOptions = archivalReasons,
                                activeFilters = filters.archivalReason,
                                isExpanded = isArchivalReasonsExpanded,
                                onToggleFilter = { toggleArchivalReason(it) }
                            )
                        }
                    }
                    if (recipientCountries != null && toggleRecipientCountry != null) {
                        stickyHeader {
                            FilterListLabel(
                                label = stringResource(R.string.recipient_country),
                                activeFilters = filters.recipientCountry,
                                isExpanded = isRecipientCountriesExpanded,
                                onExpand = {
                                    isRecipientCountriesExpanded = !isRecipientCountriesExpanded
                                }
                            )
                        }
                        item {
                            FilterListCheckboxes(
                                filterOptions = recipientCountries,
                                activeFilters = filters.recipientCountry,
                                isExpanded = isRecipientCountriesExpanded,
                                onToggleFilter = { toggleRecipientCountry(it) }
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(128.dp))
                }
            }
        }
        FilterFooter(
            sheetState = sheetState,
            sheetHeight = sheetHeight,
            filterCount = filterCount,
            onReset = { onReset() },
            onApply = {
                onApply()
                onDismiss()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    label: String,
    context: Context,
    options: List<String> = emptyList(),
    selectedOption: String,
    onToggleSelection: (String) -> Unit,
    onDismiss: () -> Unit,
    skipPartiallyExpanded: Boolean = false,
    infoText: String? = null
) {
    //val skipPartiallyExpanded = options.size < 12
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
    ) {
        BottomSheetHeader(label, infoText)
        LazyColumn {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            items(options) { option ->
                val color = if (option.isCollectionColor(context)) {
                    option.toColor(context)
                } else null
                val radioButtonColors = if (color != null) {
                    RadioButtonDefaults.colors(color, color, color, color)
                } else {
                    RadioButtonDefaults.colors()
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (option == selectedOption),
                            onClick = { onToggleSelection(option) },
                            role = Role.RadioButton
                        )
                ) {
                    RadioButton(
                        selected = (option == selectedOption),
                        onClick = null,
                        colors = radioButtonColors,
                        modifier = Modifier
                            .padding(start = 32.dp, top = 12.dp, end = 16.dp, bottom = 12.dp)
                    )
                    Text(
                        text = option,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                        modifier = Modifier.padding(end = 24.dp)
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCollectionBottomSheet(
    collections: List<Collection>,
    selectedCollection: String,
    onSelect: (Collection) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
    ) {
        BottomSheetHeader(stringResource(R.string.select_collection))
        LazyColumn {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            items(collections) { collection ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (collection.name == selectedCollection),
                            onClick = { onSelect(collection) },
                            role = Role.RadioButton
                        )
                ) {
                    if (collection.emoji != null) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(start = 30.dp)
                                .size(24.dp)
                        ) {
                            Text(text = collection.emoji)
                        }
                    } else {
                        IconCollectionLabel(
                            color = collection.color.color,
                            modifier = Modifier.padding(start = 30.dp)
                        )
                    }
                    Text(
                        text = collection.name,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .weight(1f)
                    )
                    if (collection.name == selectedCollection) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_check_24),
                            tint = colorScheme.primary,
                            contentDescription = null,
                            modifier = Modifier.padding(start = 4.dp, end = 30.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun BottomSheetHeader(
    label: String,
    infoText: String? = null
) {
    val showInfo = rememberSaveable { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 32.dp, bottom = 16.dp)
    ) {
        Text(
            text = label,
            fontSize = 20.sp
        )
        infoText?.let {
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.size(32.dp).offset(y = (-2).dp)) {
                IconButton(onClick = { showInfo.value = true }) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_info),
                        contentDescription = null,
                        tint = colorScheme.outline
                    )
                }
            }
        }
    }
    HorizontalDivider(modifier = Modifier.fillMaxWidth())

    if (showInfo.value) {
        InfoDialog(
            onDismissRequest = { showInfo.value = false },
            text = infoText ?: ""
        )
    }
}

@Composable
private fun FilterListLabel(
    label: String,
    activeFilters: Set<String>,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    value: String? = null,
    isSliderActive: Boolean = false
) {
    val onClick = remember { Modifier.clickable { onExpand() } }
    val backgroundColor = colorScheme.surfaceContainerLow.copy(alpha = 0.95f)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .then(onClick)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Row {
                Text(
                    text = label,
                    modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp)
                )
                if (activeFilters.isNotEmpty()) {
                    Badge(
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                    ) {
                        Text(activeFilters.size.toString())
                    }
                }
                if (isSliderActive) {
                    Badge(
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                    ) {
                        Text("1")
                    }
                }
            }
            value?.let {
                Text(
                    text = it,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
        Icon(
            painter = if (isExpanded) painterResource(R.drawable.rounded_keyboard_arrow_up_24)
            else painterResource(R.drawable.rounded_keyboard_arrow_down_24),
            contentDescription = null,
            modifier = Modifier.padding(end = 32.dp)
        )
    }
    FilterHorizontalDivider(color = Color.Transparent)
}

@Composable
private fun FilterListCheckboxes(
    filterOptions: Set<String>,
    activeFilters: Set<String>,
    isExpanded: Boolean,
    onToggleFilter: (String) -> Unit
) {
    Column(
        modifier = Modifier.animateContentSize()
    ) {
        if (isExpanded) {
            filterOptions.forEach { option ->
                val onClickOption = remember { Modifier.clickable { onToggleFilter(option) } }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(onClickOption)
                ) {
                    Checkbox(
                        checked = activeFilters.any { it == option },
                        onCheckedChange = null,
                        modifier = Modifier
                            .padding(start = 32.dp, top = 12.dp, bottom = 12.dp, end = 10.dp)
                    )
                    Text(
                        text = option,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                        modifier = Modifier.padding(end = 32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    FilterHorizontalDivider()
}

@Composable
private fun FilterListSlider(
    sliderRange: ClosedRange<Float>,
    sliderPosition: ClosedRange<Float>,
    onSliderChange: (ClosedRange<Float>) -> Unit,
    isExpanded: Boolean,
    independentLabel: String? = null
) {
    Column(
        modifier = Modifier
            .animateContentSize()
            .padding(horizontal = 32.dp)
    ) {
        if (isExpanded) {
            if (independentLabel != null) {
                Column(modifier = Modifier.padding(bottom = 8.dp, top = 0.dp)) {
                    Text(text = independentLabel)
                    Text(
                        text = "${sliderPosition.start.roundToInt()} – " +
                                "${sliderPosition.endInclusive.roundToInt()}",
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            RangeSlider(
                value = sliderPosition as ClosedFloatingPointRange<Float>,
                valueRange = sliderRange as ClosedFloatingPointRange<Float>,
                onValueChange = { newSliderPosition ->
                    onSliderChange(newSliderPosition)
                },
                onValueChangeFinished = { onSliderChange(sliderPosition) },
                modifier = Modifier.height(32.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
    FilterHorizontalDivider()
}

@Composable
private fun FilterListSwitch(
    label: String,
    isFilterActive: Boolean,
    onToggle: () -> Unit
) {
    val onClick = remember { Modifier.clickable { onToggle() } }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .then(onClick)
    ) {
        Row {
            Text(
                text = label,
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp)
            )
            if (isFilterActive) {
                Badge(
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                ) {
                    Text(text = "1")
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = isFilterActive,
            onCheckedChange = null,
            modifier = Modifier.scale(0.9f).padding(end = 24.dp)
        )
    }
    FilterHorizontalDivider()
}

@Composable
private fun FilterHorizontalDivider(
    color: Color = DividerDefaults.color,
) {
    HorizontalDivider(
        color = color,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    )
}

@Composable
private fun FilterSubheader(text: String) {
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = text,
        color = colorScheme.primary,
        fontWeight = FontWeight.Bold,
        style = typography.labelLarge,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
    )
    FilterHorizontalDivider()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterFooter(
    sheetState: SheetState,
    sheetHeight: Int,
    filterCount: Int,
    onReset: () -> Unit,
    onApply: () -> Unit,
) {
    val backgroundColor = colorScheme.surfaceContainerLow

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    0,
                    (sheetHeight - sheetState.requireOffset() - sheetHeight).toInt()
                )
            }
    ) {
        Column(
            modifier = Modifier
                .background(backgroundColor)
        ) {
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(8.dp)
            ) {
                TextButton(
                    onClick = onReset,
                    modifier = Modifier.weight(0.9f)
                ) {
                    Text(stringResource(R.string.reset))
                }
                Spacer(modifier = Modifier.width(8.dp))
                BadgedBox(
                    modifier = Modifier.weight(1f),
                    badge = {
                        if (filterCount > 0) {
                            Badge(modifier = Modifier.offset(x = (-16).dp)) {
                                Text(filterCount.toString())
                            }
                        }
                    }
                ) {
                    Button(
                        onClick = onApply,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.filter_apply))
                    }
                }
            }
        }
        /*  The Column above contains the actual content of the footer.
        *   The Box below is a hack to fill in the gap from the system navigation bar.
        * */
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 64.dp)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(backgroundColor)
            )
        }
    }
}
