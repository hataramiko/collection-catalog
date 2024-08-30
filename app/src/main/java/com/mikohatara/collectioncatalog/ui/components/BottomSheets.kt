package com.mikohatara.collectioncatalog.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.home.HomeUiState
import com.mikohatara.collectioncatalog.ui.home.HomeViewModel
import com.mikohatara.collectioncatalog.ui.home.SortBy
import com.mikohatara.collectioncatalog.util.getSortByText

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
    val sortByOptions = viewModel.sortByOptions

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState
    ) {
        Header(stringResource(R.string.sort_by))
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
                            onClick = {
                                when (option) {
                                    SortBy.COUNTRY_ASC -> viewModel.setSortBy(SortBy.COUNTRY_ASC)
                                    SortBy.COUNTRY_DESC -> viewModel.setSortBy(SortBy.COUNTRY_DESC)
                                    SortBy.COUNTRY_AND_TYPE_ASC ->
                                        viewModel.setSortBy(SortBy.COUNTRY_AND_TYPE_ASC)

                                    SortBy.COUNTRY_AND_TYPE_DESC ->
                                        viewModel.setSortBy(SortBy.COUNTRY_AND_TYPE_DESC)

                                    SortBy.DATE_NEWEST -> viewModel.setSortBy(SortBy.DATE_NEWEST)
                                    SortBy.DATE_OLDEST -> viewModel.setSortBy(SortBy.DATE_OLDEST)
                                }
                            },
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
@OptIn(ExperimentalMaterial3Api::class)
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

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        modifier = Modifier.onGloballyPositioned {
            sheetHeight = it.size.height
        }
    ) {
        Header(
            label = stringResource(R.string.filter),
            onReset = { viewModel.resetFilter() }
        )
        Box(
            modifier = Modifier.weight(1f)
        ) {
            LazyColumn {
                item {
                    CheckboxList(
                        label = stringResource(R.string.country),
                        filterOptions = countries,
                        activeFilters = uiState.filters.country,
                        onToggleFilter = { viewModel.toggleCountryFilter(it) }
                    )
                    FilterHorizontalDivider()
                }
                item {
                    CheckboxList(
                        label = stringResource(R.string.type),
                        filterOptions = types,
                        activeFilters = uiState.filters.type,
                        onToggleFilter = { viewModel.toggleTypeFilter(it) }
                    )
                    FilterHorizontalDivider()
                }
                item {
                    CheckboxList(
                        label = stringResource(R.string.location),
                        filterOptions = locations,
                        activeFilters = uiState.filters.location,
                        onToggleFilter = { viewModel.toggleLocationFilter(it) }
                    )
                    FilterHorizontalDivider()
                }
                item {
                    Text(
                        "Boolean",
                        modifier = Modifier.padding(start = 32.dp, top = 12.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 32.dp, end = 32.dp, top = 4.dp, bottom = 12.dp)
                    ) {
                        FilterChip(
                            selected = uiState.filters.isKeeper,
                            onClick = { viewModel.toggleIsKeeperFilter() },
                            label = { Text("Keeper") },
                            leadingIcon = if (uiState.filters.isKeeper) {
                                {
                                    Icon(
                                        imageVector = Icons.Rounded.Done,
                                        contentDescription = null
                                    )
                                }
                            } else {
                                null
                            },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = uiState.filters.isForTrade,
                            onClick = { viewModel.toggleIsForTradeFilter() },
                            label = { Text("For trade") },
                            leadingIcon = if (uiState.filters.isForTrade) {
                                {
                                    Icon(
                                        imageVector = Icons.Rounded.Done,
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else {
                                null
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
        FilterFooter(
            sheetState = sheetState,
            sheetHeight = sheetHeight,
            filterCount = uiState.filters.country.size +
                    uiState.filters.type.size +
                    uiState.filters.location.size +
                    if (uiState.filters.isKeeper && uiState.filters.isForTrade) {
                        2
                    } else if (uiState.filters.isKeeper || uiState.filters.isForTrade) {
                        1
                    } else {
                        0
                    },
            onReset = { viewModel.resetFilter() },
            onApply = {
                viewModel.setFilter()
                onDismiss()
            }
        )
    }
}

@Composable
private fun Header(
    label: String,
    onReset: (() -> Unit)? = null // TODO get rid of completely?
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            label,
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(start = 32.dp, bottom = 16.dp)
        )
        /*if (onReset != null) {
            //Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = { onReset() },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .absoluteOffset(y = (-5).dp)
                    .padding(end = 8.dp)
            ) {
                /*Icon(
                    painter = painterResource(R.drawable.rounded_refresh),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .rotate(45f)
                        .scale(scaleX = -1f, scaleY = 1f)
                )*/
                Text(
                    stringResource(R.string.reset),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }*/
    }
    HorizontalDivider(modifier = Modifier.fillMaxWidth())
}

@Composable
private fun CheckboxList(
    label: String,
    filterOptions: Set<String>,
    activeFilters: Set<String>,
    onToggleFilter: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val onClickLabel = remember { Modifier.clickable { isExpanded = !isExpanded } }

    Column(
        modifier = Modifier.animateContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .then(onClickLabel)
        ) {
            BadgedBox(
                badge = {
                    if (activeFilters.isNotEmpty()) {
                        Badge(
                            modifier = Modifier.padding(start = 14.dp, top = 4.dp)
                        ) {
                            Text(activeFilters.size.toString())
                        }
                    }
                }
            ) {
                Text(
                    label,
                    modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (isExpanded) Icons.Rounded.KeyboardArrowUp
                    else Icons.Rounded.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.padding(end = 32.dp)
            )
        }
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
}

@Composable
private fun FilterHorizontalDivider() {
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, /*top = 8.dp*/)
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
