package com.mikohatara.collectioncatalog.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.WantedPlate
import com.mikohatara.collectioncatalog.ui.components.EmptyList
import com.mikohatara.collectioncatalog.ui.components.EndOfList
import com.mikohatara.collectioncatalog.ui.components.FilterBottomSheet
import com.mikohatara.collectioncatalog.ui.components.HomeTopAppBar
import com.mikohatara.collectioncatalog.ui.components.Loading
import com.mikohatara.collectioncatalog.ui.components.SortByBottomSheet
import com.mikohatara.collectioncatalog.ui.components.TopRow
import com.mikohatara.collectioncatalog.ui.components.WishlistCard

@Composable
fun WishlistScreen(
    viewModel: WishlistViewModel = hiltViewModel(),
    onAddItem: () -> Unit,
    onItemClick: (WantedPlate) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WishlistScreen(
        itemList = uiState.items,
        uiState = uiState,
        viewModel = viewModel,
        onAddItem = onAddItem,
        onItemClick = onItemClick,
        onOpenDrawer = onOpenDrawer,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WishlistScreen(
    itemList: List<WantedPlate>,
    uiState: WishlistUiState,
    viewModel: WishlistViewModel,
    onAddItem: () -> Unit,
    onItemClick: (WantedPlate) -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val isFabHidden by viewModel.isTopRowHidden.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeTopAppBar(
                title = stringResource(R.string.wishlist),
                onOpenDrawer = onOpenDrawer,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !isFabHidden,
                enter = slideInVertically(initialOffsetY = { it * 2 }),
                exit = slideOutVertically(targetOffsetY = { it * 3 })
            ) {
                FloatingActionButton(onClick = onAddItem) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null
                    )
                }
            }
        },
        content = { innerPadding ->
            WishlistScreenContent(
                uiState = uiState,
                viewModel = viewModel,
                topBarState = scrollBehavior.state,
                itemList = itemList,
                onItemClick = onItemClick,
                onSortByClick = { viewModel.showSortByBottomSheet.value = true },
                onFilterClick = { viewModel.openFilterBottomSheet() },
                modifier = Modifier.padding(innerPadding)
            )
            if (viewModel.showSortByBottomSheet.value) {
                SortByBottomSheet(
                    onDismiss = { viewModel.showSortByBottomSheet.value = false },
                    onClick = { sortBy ->
                        viewModel.setSortBy(sortBy)
                        //viewModel.showSortByBottomSheet.value = false
                    },
                    sortByOptions = viewModel.getSortByOptions(),
                    selectedSortBy = uiState.sortBy
                )
            }
            if (viewModel.showFilterBottomSheet.value) {
                FilterBottomSheet(
                    onDismiss = { viewModel.showFilterBottomSheet.value = false },
                    filters = uiState.filters,
                    onApply = { viewModel.setFilter() },
                    onReset = { viewModel.resetFilter() },
                    countries = viewModel.getCountries(),
                    toggleCountry = { viewModel.toggleCountryFilter(it) },
                    types = viewModel.getTypes(),
                    toggleType = { viewModel.toggleTypeFilter(it) },
                    periodSliderPosition = uiState.periodSliderPosition,
                    onPeriodSliderChange = { newPosition ->
                        viewModel.updatePeriodSliderPosition(newPosition)
                    },
                    yearSliderRange = viewModel.getYearSliderRange(),
                    yearSliderPosition = uiState.yearSliderPosition,
                    onYearSliderChange = { newPosition ->
                        viewModel.updateYearSliderPosition(newPosition)
                    },
                )
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun WishlistScreenContent(
    uiState: WishlistUiState,
    viewModel: WishlistViewModel,
    topBarState: TopAppBarState,
    itemList: List<WantedPlate>,
    onItemClick: (WantedPlate) -> Unit,
    onSortByClick: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val itemIndex = remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val isAtTop = remember {
        derivedStateOf { (listState.firstVisibleItemIndex == 0) &&
            (listState.firstVisibleItemScrollOffset == 0) }
    }
    val topBarCollapsedFraction = remember { derivedStateOf { topBarState.collapsedFraction } }
    viewModel.updateTopRowVisibility(itemIndex.value, topBarCollapsedFraction.value)

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        stickyHeader {
            TopRow(viewModel.isTopRowHidden.value, isAtTop.value, onSortByClick, onFilterClick)
        }
        if (uiState.isLoading) {
            item {
                Loading()
            }
        } else if (itemList.isEmpty()) {
            item {
                if (uiState.filters != FilterData()) {
                    EmptyList(
                        message = stringResource(R.string.empty_list_filter_msg),
                        description = stringResource(R.string.empty_list_filter_desc)
                    )
                } else {
                    EmptyList(
                        painter = painterResource(R.drawable.rounded_heart),
                        message = stringResource(R.string.empty_list_wishlist_msg),
                        description = stringResource(R.string.empty_list_wishlist_desc)
                    )
                }
            }
        } else {
            items(items = itemList, key = { it.id }) { item ->
                WishlistCard(
                    country = item.commonDetails.country,
                    region1st = item.commonDetails.region1st,
                    region2nd = item.commonDetails.region2nd,
                    region3rd = item.commonDetails.region3rd,
                    type = item.commonDetails.type,
                    periodStart = item.commonDetails.periodStart,
                    periodEnd = item.commonDetails.periodEnd,
                    year = item.commonDetails.year,
                    regNo = item.regNo,
                    imagePath = item.imagePath,
                    notes = item.notes,
                ) {
                    onItemClick(item)
                }
            }
            item {
                EndOfList(
                    //hasCircle = true,
                    text = pluralStringResource(
                        R.plurals.wishlist_list_size, itemList.size, itemList.size
                    )
                    //modifier = Modifier.height(8.dp)
                )
            }
        }
    }
}
