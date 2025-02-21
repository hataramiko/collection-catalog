package com.mikohatara.collectioncatalog.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.ui.components.EmptyList
import com.mikohatara.collectioncatalog.ui.components.EndOfList
import com.mikohatara.collectioncatalog.ui.components.FilterBottomSheet
import com.mikohatara.collectioncatalog.ui.components.HomeTopAppBar
import com.mikohatara.collectioncatalog.ui.components.ItemCard
import com.mikohatara.collectioncatalog.ui.components.Loading
import com.mikohatara.collectioncatalog.ui.components.SortByBottomSheet
import com.mikohatara.collectioncatalog.ui.components.TopRow

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onAddItem: () -> Unit,
    onItemClick: (Plate) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        itemList = uiState.items,
        uiState = uiState,
        viewModel = viewModel,
        onAddItem = onAddItem,
        onItemClick = onItemClick,
        onOpenDrawer = onOpenDrawer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    itemList: List<Plate>,
    uiState: HomeUiState,
    viewModel: HomeViewModel,
    onAddItem: () -> Unit,
    onItemClick: (Plate) -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val isFabHidden by viewModel.isTopRowHidden.collectAsStateWithLifecycle()
    val topBarTitle = viewModel.getCollectionName() ?: stringResource(R.string.all_plates)
    //" (${itemList.size})"

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeTopAppBar(
                title = topBarTitle,
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
            HomeScreenContent(
                uiState = uiState,
                viewModel = viewModel,
                topBarState = scrollBehavior.state,
                itemList = itemList,
                modifier = modifier.padding(innerPadding),
                onItemClick = onItemClick,
                onSortByClick = { viewModel.showSortByBottomSheet.value = true },
                onFilterClick = { viewModel.showFilterBottomSheet.value = true }
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
                    locations = viewModel.getLocations(),
                    toggleLocation = { viewModel.toggleLocationFilter(it) }
                )
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    viewModel: HomeViewModel,
    topBarState: TopAppBarState,
    itemList: List<Plate>,
    modifier: Modifier = Modifier,
    onItemClick: (Plate) -> Unit,
    onSortByClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    val maxItemWidth = itemList.maxOfOrNull { it.size.width ?: 1 } ?: 1
    val listState = rememberLazyListState()
    val isAtTop = remember {
        derivedStateOf { (listState.firstVisibleItemIndex == 0) &&
            (listState.firstVisibleItemScrollOffset == 0) }
    }
    val itemIndex = remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val topBarCollapsedFraction = remember { derivedStateOf { topBarState.collapsedFraction } }
    // Use itemIndex and topBarCollapsedFraction to update TopRow visibility in viewModel
    viewModel.updateTopRowVisibility(itemIndex.value, topBarCollapsedFraction.value)

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
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
                    EmptyList()
                } else if (viewModel.collectionId != null) {
                    EmptyList(
                        painter = painterResource(R.drawable.rounded_label),
                        message = stringResource(R.string.empty_list_collection_msg),
                        description = stringResource(R.string.empty_list_collection_desc),
                        iconModifier = Modifier.offset(x = 2.dp)
                    )
                } else {
                    EmptyList(
                        painter = painterResource(R.drawable.rounded_newsstand),
                        message = stringResource(R.string.empty_list_plates_msg),
                        description = stringResource(R.string.empty_list_plates_desc),
                    )
                }
            }
        } else {
            items(items = itemList, key = { it.id }) { item ->
                ItemCard(
                    title = item.uniqueDetails.regNo,
                    imagePath = item.uniqueDetails.imagePath,
                    itemWidth = item.size.width,
                    maxWidth = maxItemWidth
                ) {
                    onItemClick(item)
                }
            }
            item {
                EndOfList(
                    hasCircle = true,
                    text = pluralStringResource(
                        R.plurals.plates_list_size, itemList.size, itemList.size
                    )
                )
            }
        }
    }
}
