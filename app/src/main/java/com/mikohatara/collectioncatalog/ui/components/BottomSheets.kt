package com.mikohatara.collectioncatalog.ui.components

import android.annotation.SuppressLint
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.home.HomeUiState
import com.mikohatara.collectioncatalog.ui.home.HomeViewModel
import com.mikohatara.collectioncatalog.util.getSortByText

//TODO maybe combine SortByBottomSheet and FilterBottomSheet into one BottomSheet?
enum class BottomSheetType {
    SORT_BY,
    FILTER
}

@Composable
fun BottomSheet(
    type: BottomSheetType,
    onDismiss: () -> Unit
) {
    /*when (type) {
        BottomSheetType.SORT_BY -> SortByBottomSheet(onDismiss)
        BottomSheetType.FILTER -> FilterBottomSheet(onDismiss)
    }*/
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortByBottomSheet(
    onDismiss: () -> Unit,
    uiState: HomeUiState,
    viewModel: HomeViewModel
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sortByOptions = viewModel.getSortByOptions()

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
            sortByOptions.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (option == uiState.sortBy),
                            onClick = { viewModel.setSortBy(option) },
                            role = Role.RadioButton
                        )
                ) {
                    RadioButton(
                        selected = (option == uiState.sortBy),
                        onClick = null,
                        modifier = Modifier
                            .padding(start = 32.dp, top = 12.dp, end = 16.dp, bottom = 12.dp)
                    )
                    Text(getSortByText(option))
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
    uiState: HomeUiState,
    viewModel: HomeViewModel
) {
    val sheetState = rememberModalBottomSheetState()
    var sheetHeight by remember { mutableIntStateOf(0) }

    val countries = viewModel.getCountries()
    val types = viewModel.getTypes()
    val locations = viewModel.getLocations()

    var isCountriesExpanded by remember { mutableStateOf(false) }
    var isTypesExpanded by remember { mutableStateOf(false) }
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
                        activeFilters = uiState.filters.country,
                        isExpanded = isCountriesExpanded,
                        onExpand = { isCountriesExpanded = !isCountriesExpanded }
                    )
                }
                item {
                    FilterListContent(
                        filterOptions = countries,
                        activeFilters = uiState.filters.country,
                        isExpanded = isCountriesExpanded,
                        onToggleFilter = { viewModel.toggleCountryFilter(it) }
                    )
                }
                stickyHeader {
                    FilterListLabel(
                        label = stringResource(R.string.type),
                        activeFilters = uiState.filters.type,
                        isExpanded = isTypesExpanded,
                        onExpand = { isTypesExpanded = !isTypesExpanded }
                    )
                }
                item {
                    FilterListContent(
                        filterOptions = types,
                        activeFilters = uiState.filters.type,
                        isExpanded = isTypesExpanded,
                        onToggleFilter = { viewModel.toggleTypeFilter(it) }
                    )
                }
                stickyHeader {
                    FilterListLabel(
                        label = stringResource(R.string.location),
                        activeFilters = uiState.filters.location,
                        isExpanded = isLocationsExpanded,
                        onExpand = { isLocationsExpanded = !isLocationsExpanded }
                    )
                }
                item {
                    FilterListContent(
                        filterOptions = locations,
                        activeFilters = uiState.filters.location,
                        isExpanded = isLocationsExpanded,
                        onToggleFilter = { viewModel.toggleLocationFilter(it) }
                    )
                }
            }
        }
        FilterFooter(
            sheetState = sheetState,
            sheetHeight = sheetHeight,
            filterCount = uiState.filters.country.size + uiState.filters.type.size +
                uiState.filters.location.size,
            onReset = { viewModel.resetFilter() },
            onApply = {
                viewModel.setFilter()
                onDismiss()
            }
        )
    }
}

@Composable
private fun BottomSheetHeader(
    label: String
) {
    Text(
        text = label,
        fontSize = 20.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp, bottom = 16.dp)
    )
    HorizontalDivider(modifier = Modifier.fillMaxWidth())
}

@Composable
private fun FilterListLabel(
    label: String,
    activeFilters: Set<String>,
    isExpanded: Boolean,
    onExpand: () -> Unit
) {
    val onClick = remember { Modifier.clickable { onExpand() } }
    val backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.95f)

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
        }
        Spacer(modifier = Modifier.weight(1f))
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
private fun FilterListContent(
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
                    Text(option)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
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
    val backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow

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
