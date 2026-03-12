package com.mikohatara.collectioncatalog.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.icu.util.MeasureUnit
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Collection
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.ui.catalog.FilterData
import com.mikohatara.collectioncatalog.ui.catalog.SortBy
import com.mikohatara.collectioncatalog.util.getMeasurementUnitSymbol
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

@Composable
private fun testColor() = colorScheme.surfaceContainerHigh

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
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                stickyHeader {
                    FilterGroupSubheader(
                        text = stringResource(R.string.common_details_label),
                        isFirst = true
                    )
                }
                stickyHeader {
                    FilterLabel(
                        label = stringResource(R.string.country),
                        activeFilters = filters.country,
                        isExpanded = isCountriesExpanded,
                        onExpand = { isCountriesExpanded = !isCountriesExpanded },
                        topCornerRadius = 20.dp
                    )
                }
                item {
                    FilterCheckboxList(
                        filterOptions = countries,
                        activeFilters = filters.country,
                        isExpanded = isCountriesExpanded,
                        onToggleFilter = { toggleCountry(it) }
                    )
                }
                stickyHeader {
                    FilterLabelBottomExtension(isExpanded = isCountriesExpanded)
                }
                stickyHeader {
                    FilterLabel(
                        label = stringResource(R.string.type),
                        activeFilters = filters.type,
                        isExpanded = isTypesExpanded,
                        onExpand = { isTypesExpanded = !isTypesExpanded }
                    )
                }
                item {
                    FilterCheckboxList(
                        filterOptions = types,
                        activeFilters = filters.type,
                        isExpanded = isTypesExpanded,
                        onToggleFilter = { toggleType(it) }
                    )
                }
                stickyHeader {
                    FilterLabelBottomExtension(isExpanded = isTypesExpanded)
                }
                if (yearSliderRange != null && periodSliderPosition != null &&
                    onPeriodSliderChange != null) {
                    stickyHeader {
                        val minValue = periodSliderPosition.start.roundToInt()
                        val maxValue = periodSliderPosition.endInclusive.roundToInt()

                        FilterLabel(
                            label = stringResource(R.string.period),
                            activeFilters = emptySet(),
                            isExpanded = isPeriodExpanded,
                            onExpand = { isPeriodExpanded = !isPeriodExpanded },
                            value = "$minValue–$maxValue",
                            isSliderActive = periodSliderPosition != yearSliderRange.start
                                ..yearSliderRange.endInclusive
                        )
                    }
                    item {
                        FilterSliderRangeFields(
                            sliderRange = yearSliderRange,
                            sliderPosition = periodSliderPosition,
                            isExpanded = isPeriodExpanded,
                            onValueChange = { newValue ->
                                onPeriodSliderChange(newValue)
                            },
                            cardColor = testColor(),
                            localeCode = localeCode
                        )
                        FilterSlider(
                            sliderRange = yearSliderRange,
                            sliderPosition = periodSliderPosition,
                            isExpanded = isPeriodExpanded,
                            onSliderChange = { newSliderPosition ->
                                onPeriodSliderChange(newSliderPosition)
                            }
                        )
                    }
                    stickyHeader {
                        FilterLabelBottomExtension(isExpanded = isPeriodExpanded)
                    }
                }
                if (yearSliderRange != null && yearSliderPosition != null &&
                    onYearSliderChange != null) {
                    stickyHeader {
                        val minValue = yearSliderPosition.start.roundToInt()
                        val maxValue = yearSliderPosition.endInclusive.roundToInt()

                        FilterLabel(
                            label = stringResource(R.string.year),
                            activeFilters = emptySet(),
                            isExpanded = isYearExpanded,
                            onExpand = { isYearExpanded = !isYearExpanded },
                            value = "$minValue–$maxValue",
                            isSliderActive = yearSliderPosition != yearSliderRange.start
                                ..yearSliderRange.endInclusive,
                            bottomCornerRadius = 20.dp
                        )
                    }
                    item {
                        FilterSliderRangeFields(
                            sliderRange = yearSliderRange,
                            sliderPosition = yearSliderPosition,
                            isExpanded = isYearExpanded,
                            onValueChange = { newValue ->
                                onYearSliderChange(newValue)
                            },
                            cardColor = testColor(),
                            localeCode = localeCode
                        )
                        FilterSlider(
                            sliderRange = yearSliderRange,
                            sliderPosition = yearSliderPosition,
                            isExpanded = isYearExpanded,
                            onSliderChange = { newSliderPosition ->
                                onYearSliderChange(newSliderPosition)
                            }
                        )
                    }
                    stickyHeader {
                        FilterLabelBottomExtension(
                            isExpanded = isYearExpanded,
                            bottomCornerRadius = 20.dp
                        )
                    }
                }
                if (itemType != ItemType.WANTED_PLATE) {
                    stickyHeader {
                        FilterGroupSubheader(text = stringResource(R.string.unique_details_label))
                    }
                }
                if (itemType != ItemType.WANTED_PLATE && toggleVehicleSwitch != null) {
                    stickyHeader {
                        FilterSwitch(
                            label = stringResource(R.string.vehicle),
                            isFilterActive = hasVehicle,
                            onToggle = toggleVehicleSwitch,
                            topCornerRadius = 20.dp
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
                                .toFormattedDate(
                                    localeCode,
                                    SimpleDateFormat.SHORT
                                )
                            val maxValueString = maxValue
                                .toDateString()
                                .toFormattedDate(
                                    localeCode,
                                    SimpleDateFormat.SHORT
                                )

                            FilterLabel(
                                label = stringResource(R.string.date),
                                activeFilters = emptySet(),
                                isExpanded = isDateExpanded,
                                onExpand = { isDateExpanded = !isDateExpanded },
                                value = "$minValueString–\n$maxValueString",
                                isSliderActive = dateSliderPosition != dateSliderRange.start
                                    ..dateSliderRange.endInclusive
                            )
                        }
                        item {
                            FilterSliderDateRangeFields(
                                sliderRange = dateSliderRange,
                                sliderPosition = dateSliderPosition,
                                isExpanded = isDateExpanded,
                                onValueChange = { newValue ->
                                    onDateSliderChange(newValue)
                                },
                                cardColor = testColor(),
                                localeCode = localeCode
                            )
                            FilterSlider(
                                sliderRange = dateSliderRange,
                                sliderPosition = dateSliderPosition,
                                isExpanded = isDateExpanded,
                                onSliderChange = { newSliderPosition ->
                                    onDateSliderChange(newSliderPosition)
                                }
                            )
                        }
                        stickyHeader {
                            FilterLabelBottomExtension(isExpanded = isDateExpanded)
                        }
                    }
                    if (costSliderRange != null && costSliderPosition != null &&
                        onCostSliderChange != null) {
                        val bottomCornerRadius = if (itemType == ItemType.FORMER_PLATE) {
                            20.dp
                        } else {
                            8.dp
                        }

                        stickyHeader {
                            val minValue = costSliderPosition.start.roundToLong()
                            val maxValue = costSliderPosition.endInclusive.roundToLong()

                            FilterLabel(
                                label = stringResource(R.string.cost),
                                activeFilters = emptySet(),
                                isExpanded = isCostExpanded,
                                onExpand = { isCostExpanded = !isCostExpanded },
                                value = minValue.toCurrencyString(localeCode) +
                                    "–${maxValue.toCurrencyString(localeCode)}",
                                isSliderActive = costSliderPosition != costSliderRange.start
                                    ..costSliderRange.endInclusive,
                                bottomCornerRadius = bottomCornerRadius
                            )
                        }
                        item {
                            FilterSliderRangeFields(
                                sliderRange = costSliderRange,
                                sliderPosition = costSliderPosition,
                                isExpanded = isCostExpanded,
                                onValueChange = { newValue ->
                                    onCostSliderChange(newValue)
                                },
                                cardColor = testColor(),
                                isCurrency = true,
                                localeCode = localeCode
                            )
                            FilterSlider(
                                sliderRange = costSliderRange,
                                sliderPosition = costSliderPosition,
                                isExpanded = isCostExpanded,
                                onSliderChange = { newSliderPosition ->
                                    onCostSliderChange(newSliderPosition)
                                }
                            )
                        }
                        stickyHeader {
                            FilterLabelBottomExtension(
                                isExpanded = isCostExpanded,
                                bottomCornerRadius = bottomCornerRadius
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

                            FilterLabel(
                                label = stringResource(R.string.value),
                                activeFilters = emptySet(),
                                isExpanded = isValueExpanded,
                                onExpand = { isValueExpanded = !isValueExpanded },
                                value = minValue.toCurrencyString(localeCode) +
                                    "–${maxValue.toCurrencyString(localeCode)}",
                                isSliderActive = valueSliderPosition != valueSliderRange.start
                                    ..valueSliderRange.endInclusive
                            )
                        }
                        item {
                            FilterSliderRangeFields(
                                sliderRange = valueSliderRange,
                                sliderPosition = valueSliderPosition,
                                isExpanded = isValueExpanded,
                                onValueChange = { newValue ->
                                    onValueSliderChange(newValue)
                                },
                                cardColor = testColor(),
                                isCurrency = true,
                                localeCode = localeCode
                            )
                            FilterSlider(
                                sliderRange = valueSliderRange,
                                sliderPosition = valueSliderPosition,
                                isExpanded = isValueExpanded,
                                onSliderChange = { newSliderPosition ->
                                    onValueSliderChange(newSliderPosition)
                                }
                            )
                        }
                        stickyHeader {
                            FilterLabelBottomExtension(isExpanded = isValueExpanded)
                        }
                    }
                    if (locations != null && toggleLocation != null) {
                        stickyHeader {
                            FilterLabel(
                                label = stringResource(R.string.location),
                                activeFilters = filters.location,
                                isExpanded = isLocationsExpanded,
                                onExpand = { isLocationsExpanded = !isLocationsExpanded },
                                bottomCornerRadius = 20.dp
                            )
                        }
                        item {
                            FilterCheckboxList(
                                filterOptions = locations,
                                activeFilters = filters.location,
                                isExpanded = isLocationsExpanded,
                                onToggleFilter = { toggleLocation(it) }
                            )
                        }
                        stickyHeader {
                            FilterLabelBottomExtension(
                                isExpanded = isLocationsExpanded,
                                bottomCornerRadius = 20.dp
                            )
                        }
                    }
                }
                if (colorsMain != null || colorsSecondary != null) {
                    stickyHeader {
                        FilterGroupSubheader(text = stringResource(R.string.size_and_color))
                    }
                }
                if (widthSliderRange != null && widthSliderPosition != null &&
                    onWidthSliderChange != null) {
                    stickyHeader {
                        val minValue = widthSliderPosition.start.roundToInt()
                        val maxValue = widthSliderPosition.endInclusive.roundToInt()

                        FilterLabel(
                            label = stringResource(R.string.width),
                            activeFilters = emptySet(),
                            isExpanded = isWidthExpanded,
                            onExpand = { isWidthExpanded = !isWidthExpanded },
                            value = minValue.toMeasurementString(lengthUnit, localeCode) +
                                "–${maxValue.toMeasurementString(lengthUnit, localeCode)}",
                            isSliderActive = widthSliderPosition != widthSliderRange.start
                                ..widthSliderRange.endInclusive,
                            topCornerRadius = 20.dp
                        )
                    }
                    item {
                        FilterSliderRangeFields(
                            sliderRange = widthSliderRange,
                            sliderPosition = widthSliderPosition,
                            isExpanded = isWidthExpanded,
                            onValueChange = { newValue ->
                                onWidthSliderChange(newValue)
                            },
                            cardColor = testColor(),
                            localeCode = localeCode,
                            isMeasurement = true,
                            measurementUnit = getMeasurementUnitSymbol(lengthUnit)
                        )
                        FilterSlider(
                            sliderRange = widthSliderRange,
                            sliderPosition = widthSliderPosition,
                            isExpanded = isWidthExpanded,
                            onSliderChange = { newSliderPosition ->
                                onWidthSliderChange(newSliderPosition)
                            }
                        )
                    }
                    stickyHeader {
                        FilterLabelBottomExtension(isExpanded = isWidthExpanded)
                    }
                }
                if (colorsMain != null && toggleColorMain != null) {
                    stickyHeader {
                        FilterLabel(
                            label = stringResource(R.string.color_main),
                            activeFilters = filters.colorMain,
                            isExpanded = isColorsMainExpanded,
                            onExpand = { isColorsMainExpanded = !isColorsMainExpanded }
                        )
                    }
                    item {
                        FilterCheckboxList(
                            filterOptions = colorsMain,
                            activeFilters = filters.colorMain,
                            isExpanded = isColorsMainExpanded,
                            onToggleFilter = { toggleColorMain(it) }
                        )
                    }
                    stickyHeader {
                        FilterLabelBottomExtension(isExpanded = isColorsMainExpanded)
                    }
                }
                if (colorsSecondary != null && toggleColorSecondary != null) {
                    stickyHeader {
                        FilterLabel(
                            label = stringResource(R.string.color_secondary),
                            activeFilters = filters.colorSecondary,
                            isExpanded = isColorsSecondaryExpanded,
                            onExpand = { isColorsSecondaryExpanded = !isColorsSecondaryExpanded },
                            bottomCornerRadius = 20.dp
                        )
                    }
                    item {
                        FilterCheckboxList(
                            filterOptions = colorsSecondary,
                            activeFilters = filters.colorSecondary,
                            isExpanded = isColorsSecondaryExpanded,
                            onToggleFilter = { toggleColorSecondary(it) }
                        )
                    }
                    stickyHeader {
                        FilterLabelBottomExtension(
                            isExpanded = isColorsSecondaryExpanded,
                            bottomCornerRadius = 20.dp
                        )
                    }
                }
                if (itemType != ItemType.WANTED_PLATE) {
                    stickyHeader {
                        FilterGroupSubheader(text = stringResource(R.string.source))
                    }
                    if (sourceTypes != null && toggleSourceType != null) {
                        stickyHeader {
                            FilterLabel(
                                label = stringResource(R.string.source_type),
                                activeFilters = filters.sourceType,
                                isExpanded = isSourceTypesExpanded,
                                onExpand = { isSourceTypesExpanded = !isSourceTypesExpanded },
                                topCornerRadius = 20.dp
                            )
                        }
                        item {
                            FilterCheckboxList(
                                filterOptions = sourceTypes,
                                activeFilters = filters.sourceType,
                                isExpanded = isSourceTypesExpanded,
                                onToggleFilter = { toggleSourceType(it) }
                            )
                        }
                        stickyHeader {
                            FilterLabelBottomExtension(isExpanded = isSourceTypesExpanded)
                        }
                    }
                    if (sourceCountries != null && toggleSourceCountry != null) {
                        stickyHeader {
                            FilterLabel(
                                label = stringResource(R.string.source_country),
                                activeFilters = filters.sourceCountry,
                                isExpanded = isSourceCountriesExpanded,
                                onExpand = {
                                    isSourceCountriesExpanded = !isSourceCountriesExpanded
                                },
                                bottomCornerRadius = 20.dp
                            )
                        }
                        item {
                            FilterCheckboxList(
                                filterOptions = sourceCountries,
                                activeFilters = filters.sourceCountry,
                                isExpanded = isSourceCountriesExpanded,
                                onToggleFilter = { toggleSourceCountry(it) }
                            )
                        }
                        stickyHeader {
                            FilterLabelBottomExtension(
                                isExpanded = isSourceCountriesExpanded,
                                bottomCornerRadius = 20.dp
                            )
                        }
                    }
                }
                if (itemType == ItemType.FORMER_PLATE) {
                    stickyHeader {
                        FilterGroupSubheader(text = stringResource(R.string.archival))
                    }
                    if (archivalDateSliderRange != null && archivalDateSliderPosition != null &&
                        onArchivalDateSliderChange != null) {
                        stickyHeader {
                            val minValue = archivalDateSliderPosition.start.roundToLong()
                            val maxValue = archivalDateSliderPosition.endInclusive.roundToLong()
                            val minValueString = minValue
                                .toDateString()
                                .toFormattedDate(
                                    localeCode,
                                    SimpleDateFormat.SHORT
                                )
                            val maxValueString = maxValue
                                .toDateString()
                                .toFormattedDate(
                                    localeCode,
                                    SimpleDateFormat.SHORT
                                )

                            FilterLabel(
                                label = stringResource(R.string.archival_date),
                                activeFilters = emptySet(),
                                isExpanded = isArchivalDateExpanded,
                                onExpand = { isArchivalDateExpanded = !isArchivalDateExpanded },
                                value = "$minValueString–\n$maxValueString",
                                isSliderActive = archivalDateSliderPosition !=
                                    archivalDateSliderRange.start
                                    ..archivalDateSliderRange.endInclusive,
                                topCornerRadius = 20.dp
                            )
                        }
                        item {
                            FilterSliderDateRangeFields(
                                sliderRange = archivalDateSliderRange,
                                sliderPosition = archivalDateSliderPosition,
                                isExpanded = isArchivalDateExpanded,
                                onValueChange = { newValue ->
                                    onArchivalDateSliderChange(newValue)
                                },
                                cardColor = testColor(),
                                localeCode = localeCode
                            )
                            FilterSlider(
                                sliderRange = archivalDateSliderRange,
                                sliderPosition = archivalDateSliderPosition,
                                isExpanded = isArchivalDateExpanded,
                                onSliderChange = { newSliderPosition ->
                                    onArchivalDateSliderChange(newSliderPosition)
                                }
                            )
                        }
                        stickyHeader {
                            FilterLabelBottomExtension(isExpanded = isArchivalDateExpanded)
                        }
                    }
                    if (archivalReasons != null && toggleArchivalReason != null) {
                        stickyHeader {
                            FilterLabel(
                                label = stringResource(R.string.archival_reason),
                                activeFilters = filters.archivalReason,
                                isExpanded = isArchivalReasonsExpanded,
                                onExpand = {
                                    isArchivalReasonsExpanded = !isArchivalReasonsExpanded
                                }
                            )
                        }
                        item {
                            FilterCheckboxList(
                                filterOptions = archivalReasons,
                                activeFilters = filters.archivalReason,
                                isExpanded = isArchivalReasonsExpanded,
                                onToggleFilter = { toggleArchivalReason(it) }
                            )
                        }
                        stickyHeader {
                            FilterLabelBottomExtension(isExpanded = isArchivalReasonsExpanded)
                        }
                    }
                    if (recipientCountries != null && toggleRecipientCountry != null) {
                        stickyHeader {
                            FilterLabel(
                                label = stringResource(R.string.recipient_country),
                                activeFilters = filters.recipientCountry,
                                isExpanded = isRecipientCountriesExpanded,
                                onExpand = {
                                    isRecipientCountriesExpanded = !isRecipientCountriesExpanded
                                },
                                bottomCornerRadius = 20.dp
                            )
                        }
                        item {
                            FilterCheckboxList(
                                filterOptions = recipientCountries,
                                activeFilters = filters.recipientCountry,
                                isExpanded = isRecipientCountriesExpanded,
                                onToggleFilter = { toggleRecipientCountry(it) }
                            )
                        }
                        stickyHeader {
                            FilterLabelBottomExtension(
                                isExpanded = isRecipientCountriesExpanded,
                                bottomCornerRadius = 20.dp
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
                    RadioButtonDefaults.colors(
                        color,
                        color,
                        color,
                        color
                    )
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
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .offset(y = (-2).dp)
            ) {
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
private fun FilterLabel(
    label: String,
    activeFilters: Set<String>,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    value: String? = null,
    isSliderActive: Boolean = false,
    topCornerRadius: Dp = 8.dp,
    bottomCornerRadius: Dp = 8.dp
) {
    val onClick = remember { Modifier.clickable { onExpand() } }
    val valueTextColor by animateColorAsState(
        targetValue = if (!isExpanded) colorScheme.onSurfaceVariant else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "FilterListLabelValueTextColor"
    )
    val shape = if (isExpanded) RoundedCornerShape(
        topStart = topCornerRadius,
        topEnd = topCornerRadius,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    ) else RoundedCornerShape(
        topStart = topCornerRadius,
        topEnd = topCornerRadius,
        bottomStart = bottomCornerRadius,
        bottomEnd = bottomCornerRadius
    )

    FilterLabelHorizontalSpacer()
    Card(
        shape = shape,
        colors = CardDefaults.cardColors(testColor()),
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.surfaceContainerLow)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(onClick)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row {
                    Text(
                        text = label,
                        modifier = Modifier.padding(vertical = 12.dp)
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
                Spacer(modifier = Modifier.weight(1f))
                value?.let {
                    Text(
                        text = it,
                        color = valueTextColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                Icon(
                    painter = if (isExpanded) {
                        painterResource(R.drawable.rounded_keyboard_arrow_up_24)
                    } else {
                        painterResource(R.drawable.rounded_keyboard_arrow_down_24)
                    },
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun FilterLabelBottomExtension(
    isExpanded: Boolean,
    bottomCornerRadius: Dp = 8.dp
) {
    val shape = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = bottomCornerRadius,
        bottomEnd = bottomCornerRadius
    )

    Card(
        shape = shape,
        colors = CardDefaults.cardColors(testColor()),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            )
        }
    }
}

@Composable
private fun FilterCheckboxList(
    filterOptions: Set<String>,
    activeFilters: Set<String>,
    isExpanded: Boolean,
    onToggleFilter: (String) -> Unit
) {
    val shape = RoundedCornerShape(0.dp)

    Card(
        shape = shape,
        colors = CardDefaults.cardColors(testColor()),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
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
                        modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 12.dp)
                    )
                    Text(
                        text = option,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                        modifier = Modifier.padding(start = 12.dp, end = 20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterSlider(
    sliderRange: ClosedRange<Float>,
    sliderPosition: ClosedRange<Float>,
    onSliderChange: (ClosedRange<Float>) -> Unit,
    onSliderChangeFinished: () -> Unit = {},
    isExpanded: Boolean
) {
    val shape = RoundedCornerShape(0.dp)

    Card(
        shape = shape,
        colors = CardDefaults.cardColors(testColor()),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        if (isExpanded) {
            Spacer(modifier = Modifier.height(16.dp))
            RangeSlider(
                value = sliderPosition as ClosedFloatingPointRange<Float>,
                valueRange = sliderRange as ClosedFloatingPointRange<Float>,
                onValueChange = { newSliderPosition ->
                    onSliderChange(newSliderPosition)
                },
                onValueChangeFinished = { onSliderChangeFinished() },
                modifier = Modifier
                    .height(40.dp)
                    .padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun FilterSwitch(
    label: String,
    isFilterActive: Boolean,
    onToggle: () -> Unit,
    topCornerRadius: Dp = 8.dp,
    bottomCornerRadius: Dp = 8.dp
) {
    val onClick = remember { Modifier.clickable { onToggle() } }
    val shape = RoundedCornerShape(
        topStart = topCornerRadius,
        topEnd = topCornerRadius,
        bottomStart = bottomCornerRadius,
        bottomEnd = bottomCornerRadius
    )

    Card(
        shape = shape,
        colors = CardDefaults.cardColors(testColor()),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .then(onClick)
        ) {
            Row {
                Text(
                    text = label,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp)
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
                modifier = Modifier
                    .scale(0.9f)
                    .padding(end = 8.dp)
            )
        }
    }
}

@Composable
private fun FilterLabelHorizontalSpacer() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(colorScheme.surfaceContainerLow)
    )
}

@Composable
private fun FilterGroupSubheader(text: String, isFirst: Boolean = false) {
    val spacerHeight = if (isFirst) 12.dp else 20.dp

    Spacer(modifier = Modifier.height(spacerHeight))
    Text(
        text = text,
        color = colorScheme.outline,
        style = typography.labelLarge,
        modifier = Modifier.padding(12.dp)
    )
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
        modifier = Modifier.offset {
                IntOffset(
                    0,
                    (sheetHeight - sheetState.requireOffset() - sheetHeight).toInt()
                )
            }
    ) {
        Column(
            modifier = Modifier.background(backgroundColor)
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
