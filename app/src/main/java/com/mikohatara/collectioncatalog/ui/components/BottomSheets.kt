package com.mikohatara.collectioncatalog.ui.components

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Collection
import com.mikohatara.collectioncatalog.ui.home.FilterData
import com.mikohatara.collectioncatalog.ui.home.SortBy
import com.mikohatara.collectioncatalog.util.getSortByText
import com.mikohatara.collectioncatalog.util.isCollectionColor
import com.mikohatara.collectioncatalog.util.toColor
import kotlin.math.roundToInt

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
        sheetState = sheetState
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
    onDismiss: () -> Unit,
    filters: FilterData,
    onApply: () -> Unit,
    onReset: () -> Unit,
    countries: Set<String>,
    toggleCountry: (String) -> Unit,
    types: Set<String>,
    toggleType: (String) -> Unit,
    yearSliderRange: ClosedRange<Float>? = null,
    yearSliderPosition: ClosedRange<Float>? = null,
    onYearSliderChange: ((ClosedRange<Float>) -> Unit)? = null,
    locations: Set<String>? = null,
    toggleLocation: ((String) -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState()
    var sheetHeight by remember { mutableIntStateOf(0) }

    var isCountriesExpanded by remember { mutableStateOf(false) }
    var isTypesExpanded by remember { mutableStateOf(false) }
    var isYearExpanded by remember { mutableStateOf(false) }
    var isLocationsExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        modifier = Modifier.onGloballyPositioned { sheetHeight = it.size.height }
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
                item {
                    Spacer(modifier = Modifier.height(128.dp))
                }
            }
        }
        FilterFooter(
            sheetState = sheetState,
            sheetHeight = sheetHeight,
            filterCount = filters.country.size + filters.type.size + filters.location.size +
                if (yearSliderRange != null && yearSliderPosition != null &&
                    yearSliderPosition != yearSliderRange.start..yearSliderRange.endInclusive
                ) 1 else 0,
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
        sheetState = sheetState
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
        sheetState = sheetState
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
                        Text(
                            text = collection.emoji,
                            modifier = Modifier.padding(start = 30.dp, end = 16.dp)
                        )
                    } else {
                        IconCollectionLabel(
                            color = collection.color.color,
                            modifier = Modifier.padding(start = 30.dp, end = 16.dp)
                        )
                    }
                    Text(
                        text = collection.name,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                        modifier = Modifier.padding(end = 16.dp, top = 8.dp, bottom = 8.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (collection.name == selectedCollection) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            tint = colorScheme.primary,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 30.dp)
                        )
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
        Row {
            Text(
                label,
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
                    /*if (value != null) {
                        Text(value)
                    } else {
                        Text("1")
                    }*/
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        value?.let {
            Text(
                text = it,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
        Icon(
            imageVector = if (isExpanded) Icons.Rounded.KeyboardArrowUp
            else Icons.Rounded.KeyboardArrowDown,
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
                            Badge {
                                Text(filterCount.toString())
                            }
                        }
                    }
                ) {
                    Button(
                        onClick = onApply,
                        modifier = Modifier.fillMaxWidth()//.weight(1f)
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
