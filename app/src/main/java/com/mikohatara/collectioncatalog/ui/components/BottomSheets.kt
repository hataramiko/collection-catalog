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
import androidx.compose.foundation.lazy.LazyListScope
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

data class FilterListData(
    val values: Set<String>,
    val onToggleValue: (String) -> Unit
)

data class FilterSliderData(
    val range: ClosedRange<Float>,
    val position: ClosedRange<Float>,
    val onValueChange: (ClosedRange<Float>) -> Unit
)

data class FilterSwitchData(
    val isTrue: Boolean,
    val onToggleSwitch: () -> Unit
)

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
    countryList: FilterListData? = null,
    typeList: FilterListData? = null,
    periodSlider: FilterSliderData? = null,
    yearSlider: FilterSliderData? = null,
    vehicleSwitch: FilterSwitchData? = null,
    dateSlider: FilterSliderData? = null,
    costSlider: FilterSliderData? = null,
    valueSlider: FilterSliderData? = null,
    locationList: FilterListData? = null,
    widthSlider: FilterSliderData? = null,
    colorsMainList: FilterListData? = null,
    colorsSecondaryList: FilterListData? = null,
    sourceTypeList: FilterListData? = null,
    sourceCountryList: FilterListData? = null,
    archivalDateSlider: FilterSliderData? = null,
    archivalReasonList: FilterListData? = null,
    recipientCountryList: FilterListData? = null
) {
    val sheetState = rememberModalBottomSheetState()
    var sheetHeight by remember { mutableIntStateOf(0) }

    var expandedSection by rememberSaveable { mutableStateOf<Int?>(null) }
    val toggleSection = { section: Int? ->
        expandedSection = if (expandedSection == section) null else section
    }

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
                filterCheckboxListSection(
                    labelResId = R.string.country,
                    listData = countryList,
                    activeFilters = filters.country,
                    expandedSectionId = expandedSection,
                    onToggle = toggleSection,
                    topCornerRadius = 20.dp
                )
                filterCheckboxListSection(
                    labelResId = R.string.type,
                    listData = typeList,
                    activeFilters = filters.type,
                    expandedSectionId = expandedSection,
                    onToggle = toggleSection
                )
                filterSliderSection(
                    labelResId = R.string.period,
                    sliderData = periodSlider,
                    expandedSectionId = expandedSection,
                    onToggle = toggleSection,
                    countryCode = localeCode
                )
                filterSliderSection(
                    labelResId = R.string.year,
                    sliderData = yearSlider,
                    expandedSectionId = expandedSection,
                    onToggle = toggleSection,
                    countryCode = localeCode,
                    bottomCornerRadius = 20.dp
                )
                if (itemType != ItemType.WANTED_PLATE) {
                    stickyHeader {
                        FilterGroupSubheader(text = stringResource(R.string.unique_details_label))
                    }
                    // Need to add more switches? Create another extension function for scalability
                    vehicleSwitch?.let {
                        stickyHeader {
                            FilterSwitch(
                                label = stringResource(R.string.vehicle),
                                isFilterActive = vehicleSwitch.isTrue,
                                onToggle = vehicleSwitch.onToggleSwitch,
                                topCornerRadius = 20.dp
                            )
                        }
                    }
                    filterSliderSection(
                        labelResId = R.string.date,
                        sliderData = dateSlider,
                        expandedSectionId = expandedSection,
                        onToggle = toggleSection,
                        countryCode = localeCode,
                        isDate = true
                    )
                    filterSliderSection(
                        labelResId = R.string.cost,
                        sliderData = costSlider,
                        expandedSectionId = expandedSection,
                        onToggle = toggleSection,
                        countryCode = localeCode,
                        bottomCornerRadius = if (itemType == ItemType.FORMER_PLATE) 20.dp else 8.dp,
                        isCurrency = true
                    )
                }
                if (itemType == ItemType.PLATE) {
                    filterSliderSection(
                        labelResId = R.string.value,
                        sliderData = valueSlider,
                        expandedSectionId = expandedSection,
                        onToggle = toggleSection,
                        countryCode = localeCode,
                        isCurrency = true
                    )
                    filterCheckboxListSection(
                        labelResId = R.string.location,
                        listData = locationList,
                        activeFilters = filters.location,
                        expandedSectionId = expandedSection,
                        onToggle = toggleSection,
                        bottomCornerRadius = 20.dp
                    )
                }
                if (widthSlider != null || colorsMainList != null || colorsSecondaryList != null) {
                    stickyHeader {
                        FilterGroupSubheader(text = stringResource(R.string.size_and_color))
                    }
                }
                filterSliderSection(
                    labelResId = R.string.width,
                    sliderData = widthSlider,
                    expandedSectionId = expandedSection,
                    onToggle = toggleSection,
                    countryCode = localeCode,
                    topCornerRadius = 20.dp,
                    isMeasurement = true,
                    measurementUnit = lengthUnit
                )
                filterCheckboxListSection(
                    labelResId = R.string.color_main,
                    listData = colorsMainList,
                    activeFilters = filters.colorMain,
                    expandedSectionId = expandedSection,
                    onToggle = toggleSection
                )
                filterCheckboxListSection(
                    labelResId = R.string.color_secondary,
                    listData = colorsSecondaryList,
                    activeFilters = filters.colorSecondary,
                    expandedSectionId = expandedSection,
                    onToggle = toggleSection,
                    bottomCornerRadius = 20.dp
                )
                if (itemType != ItemType.WANTED_PLATE) {
                    stickyHeader {
                        FilterGroupSubheader(text = stringResource(R.string.source))
                    }
                    filterCheckboxListSection(
                        labelResId = R.string.source_type,
                        listData = sourceTypeList,
                        activeFilters = filters.sourceType,
                        expandedSectionId = expandedSection,
                        onToggle = toggleSection,
                        topCornerRadius = 20.dp
                    )
                    filterCheckboxListSection(
                        labelResId = R.string.source_country,
                        listData = sourceCountryList,
                        activeFilters = filters.sourceCountry,
                        expandedSectionId = expandedSection,
                        onToggle = toggleSection,
                        bottomCornerRadius = 20.dp
                    )
                }
                if (itemType == ItemType.FORMER_PLATE) {
                    stickyHeader {
                        FilterGroupSubheader(text = stringResource(R.string.archival))
                    }
                    filterSliderSection(
                        labelResId = R.string.archival_date,
                        sliderData = archivalDateSlider,
                        expandedSectionId = expandedSection,
                        onToggle = toggleSection,
                        countryCode = localeCode,
                        topCornerRadius = 20.dp,
                        isDate = true
                    )
                    filterCheckboxListSection(
                        labelResId = R.string.archival_reason,
                        listData = archivalReasonList,
                        activeFilters = filters.archivalReason,
                        expandedSectionId = expandedSection,
                        onToggle = toggleSection
                    )
                    filterCheckboxListSection(
                        labelResId = R.string.recipient_country,
                        listData = recipientCountryList,
                        activeFilters = filters.recipientCountry,
                        expandedSectionId = expandedSection,
                        onToggle = toggleSection,
                        bottomCornerRadius = 20.dp
                    )
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

private fun LazyListScope.filterCheckboxListSection(
    labelResId: Int,
    listData: FilterListData?,
    activeFilters: Set<String>,
    expandedSectionId: Int?,
    onToggle: (Int) -> Unit,
    topCornerRadius: Dp = 8.dp,
    bottomCornerRadius: Dp = 8.dp
) {
    listData?.let { data ->
        val isExpanded = expandedSectionId == labelResId

        stickyHeader {
            FilterLabel(
                label = stringResource(labelResId),
                activeFilters = activeFilters,
                isExpanded = isExpanded,
                onExpand = { onToggle(labelResId) },
                topCornerRadius = topCornerRadius,
                bottomCornerRadius = bottomCornerRadius
            )
        }
        item {
            FilterCheckboxList(
                filterOptions = data.values,
                activeFilters = activeFilters,
                isExpanded = isExpanded,
                onToggleFilter = { data.onToggleValue(it) }
            )
        }
        stickyHeader {
            FilterLabelBottomExtension(
                isExpanded = isExpanded,
                bottomCornerRadius = bottomCornerRadius
            )
        }
    }
}

private fun LazyListScope.filterSliderSection(
    labelResId: Int,
    sliderData: FilterSliderData?,
    expandedSectionId: Int?,
    onToggle: (Int) -> Unit,
    countryCode: String,
    topCornerRadius: Dp = 8.dp,
    bottomCornerRadius: Dp = 8.dp,
    isDate: Boolean = false,
    isCurrency: Boolean = false,
    isMeasurement: Boolean = false,
    measurementUnit: MeasureUnit? = null
) {
    sliderData?.let { data ->
        val isExpanded = expandedSectionId == labelResId
        val isSliderActive = data.position != data.range.start..data.range.endInclusive

        val minPosition = data.position.start
        val maxPosition = data.position.endInclusive

        val displayValue = when {
            isDate -> {
                val minValueString = minPosition
                    .roundToLong()
                    .toDateString()
                    .toFormattedDate(countryCode, SimpleDateFormat.SHORT)
                val maxValueString = maxPosition
                    .roundToLong()
                    .toDateString()
                    .toFormattedDate(countryCode, SimpleDateFormat.SHORT)
                "$minValueString–\n$maxValueString"
            }
            isCurrency -> {
                val minValueString = minPosition
                    .roundToLong()
                    .toCurrencyString(countryCode)
                val maxValueString = maxPosition
                    .roundToLong()
                    .toCurrencyString(countryCode)
                "$minValueString–$maxValueString"
            }
            isMeasurement && measurementUnit != null -> {
                val minValueString = minPosition
                    .roundToInt()
                    .toMeasurementString(measurementUnit, countryCode)
                val maxValueString = maxPosition
                    .roundToInt()
                    .toMeasurementString(measurementUnit, countryCode)
                "$minValueString–$maxValueString"
            }
            else -> "${minPosition.roundToInt()}–${maxPosition.roundToInt()}"
        }

        stickyHeader {
            FilterLabel(
                label = stringResource(labelResId),
                activeFilters = emptySet(),
                isExpanded = isExpanded,
                onExpand = { onToggle(labelResId) },
                value = displayValue,
                isSliderActive = isSliderActive,
                topCornerRadius = topCornerRadius,
                bottomCornerRadius = bottomCornerRadius
            )
        }
        item {
            if (isDate) {
                FilterSliderDateRangeFields(
                    sliderRange = data.range,
                    sliderPosition = data.position,
                    isExpanded = isExpanded,
                    onValueChange = data.onValueChange,
                    cardColor = testColor(),
                    countryCode = countryCode
                )
            } else {
                FilterSliderRangeFields(
                    sliderRange = data.range,
                    sliderPosition = data.position,
                    isExpanded = isExpanded,
                    onValueChange = data.onValueChange,
                    cardColor = testColor(),
                    countryCode = countryCode,
                    isCurrency = isCurrency,
                    isMeasurement = isMeasurement,
                    measurementUnit = measurementUnit
                        ?.let { getMeasurementUnitSymbol(it) } ?: ""
                )
            }
            FilterSlider(
                sliderRange = data.range,
                sliderPosition = data.position,
                isExpanded = isExpanded,
                onSliderChange = data.onValueChange
            )
        }
        stickyHeader {
            FilterLabelBottomExtension(
                isExpanded = isExpanded, bottomCornerRadius = bottomCornerRadius
            )
        }
    }
}
