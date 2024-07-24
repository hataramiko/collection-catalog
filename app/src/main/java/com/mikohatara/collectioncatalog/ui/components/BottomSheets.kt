package com.mikohatara.collectioncatalog.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
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
    val sortByOptions = viewModel.sortByOptions

    ModalBottomSheet(
        onDismissRequest = { onDismiss() }
    ) {
        Header(
            stringResource(R.string.sort_by)
        )
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
    val countries = viewModel.getCountries()
    val types = viewModel.getTypes()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() }
    ) {
        Header(
            stringResource(R.string.filter),
            onReset = {
                viewModel.resetFilter()
                onDismiss()
            }
        )
        Box(
            modifier = Modifier
                .weight(1f)
                //.verticalScroll(rememberScrollState()) // if Column instead of LazyColumn
        ) {
            LazyColumn {
                item {
                    CheckboxList(
                        label = stringResource(R.string.country),
                        filterOptions = countries,
                        activeFilters = uiState.countryFilter,
                        onToggleFilter = { viewModel.toggleCountryFilter(it) }
                    )
                    FilterHorizontalDivider()
                }
                item {
                    CheckboxList(
                        label = stringResource(R.string.type),
                        filterOptions = types,
                        activeFilters = uiState.typeFilter,
                        onToggleFilter = { viewModel.toggleTypeFilter(it) }
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
                            selected = uiState.isKeeperFilter,
                            onClick = { viewModel.toggleIsKeeperFilter() },
                            label = { Text("Keeper") },
                            leadingIcon = if (uiState.isKeeperFilter) {
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
                            selected = uiState.isForTradeFilter,
                            onClick = { viewModel.toggleIsForTradeFilter() },
                            label = { Text("For trade") },
                            leadingIcon = if (uiState.isForTradeFilter) {
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
        /*
        Column(
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            filterOptions.forEach { option ->
                Column(
                    modifier = Modifier
                        .clickable {  }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 16.dp)
                    ) {
                        Text(option)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            painter = painterResource(R.drawable.rounded_chevron_forward),
                            contentDescription = null
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                }
            }
        }*/
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(8.dp)
        ) {
            /* TODO potentially replace the Reset button in the Header with this
            TextButton(
                onClick = { viewModel.resetFilter() },
                modifier = Modifier
                    .weight(0.9f)
            ) {
                Text(stringResource(R.string.reset))
            }
            Spacer(modifier = Modifier.width(8.dp))*/
            Button(
                onClick = {
                    viewModel.setFilter()
                    onDismiss()
                },
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(stringResource(R.string.filter_apply))
            }
        }
    }
}

@Composable
private fun Header(
    label: String,
    hasBackNavigation: Boolean = false,
    onReset: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (hasBackNavigation) {
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null
                )
            }
        } else {
            Spacer(modifier = Modifier.width(18.dp))
        }

        Text(
            label,
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(start = 16.dp, bottom = 16.dp)
        )

        if (onReset != null) {
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
        }
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
    Text(
        label,
        modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 4.dp)
    )
    filterOptions.forEach { option ->
        val onClick = remember { Modifier.clickable { onToggleFilter(option) } }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .then(onClick)
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
}

@Composable
private fun FilterHorizontalDivider() {
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp)
    )
}

/*
@Composable
private fun CheckboxSubmenu(options: Set<String>) {
    Column {
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleCountryFilter(option) }
            ) {
                Checkbox(
                    checked = viewModel.uiState.value.countryFilter.any { it == option },
                    onCheckedChange = null,
                    modifier = Modifier
                        .padding(start = 32.dp, top = 12.dp, bottom = 12.dp, end = 10.dp)
                )
                Text(option)
            }
        }
    }
}*/
