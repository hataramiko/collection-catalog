package com.mikohatara.collectioncatalog.ui.catalog

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Item
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.data.UserPreferences
import com.mikohatara.collectioncatalog.ui.components.EmptyList
import com.mikohatara.collectioncatalog.ui.components.EndOfList
import com.mikohatara.collectioncatalog.ui.components.ExportDialog
import com.mikohatara.collectioncatalog.ui.components.FilterBottomSheet
import com.mikohatara.collectioncatalog.ui.components.HomeTopAppBar
import com.mikohatara.collectioncatalog.ui.components.ImportDialog
import com.mikohatara.collectioncatalog.ui.components.ItemCard
import com.mikohatara.collectioncatalog.ui.components.Loading
import com.mikohatara.collectioncatalog.ui.components.SortByBottomSheet
import com.mikohatara.collectioncatalog.ui.components.TopRow
import com.mikohatara.collectioncatalog.ui.components.WishlistCard
import com.mikohatara.collectioncatalog.util.getFileNameForExport
import com.mikohatara.collectioncatalog.util.toItemDetails

@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel = hiltViewModel(),
    onAddItem: () -> Unit,
    onItemClick: (Item) -> Unit,
    onOpenDrawer: () -> Unit,
    onImportHelp: () -> Unit
) {
    val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    CatalogScreen(
        itemList = uiState.items,
        uiState = uiState,
        viewModel = viewModel,
        userPreferences = userPreferences,
        context = context,
        onAddItem = onAddItem,
        onItemClick = onItemClick,
        onOpenDrawer = onOpenDrawer,
        onImportHelp = onImportHelp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogScreen(
    itemList: List<Item>,
    uiState: CatalogUiState,
    viewModel: CatalogViewModel,
    userPreferences: UserPreferences,
    context: Context,
    onAddItem: () -> Unit,
    onItemClick: (Item) -> Unit,
    onOpenDrawer: () -> Unit,
    onImportHelp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults
        .enterAlwaysScrollBehavior(rememberTopAppBarState())
    val isFabHidden by viewModel.isTopRowHidden.collectAsStateWithLifecycle()
    val topBarTitle = viewModel.getTopBarTitle(context)
    val onBackBehavior = { if (uiState.isSearchActive) viewModel.toggleSearch() }

    val redirectMessage = stringResource(R.string.add_plate_redirect_msg)
    val onFabClick = {
        if (uiState.isCollection) {
            Toast.makeText(context, redirectMessage, Toast.LENGTH_SHORT).show()
        } else onAddItem()
    }
    val (fabContainerColor, fabContentColor) = if (uiState.isCollection) {
        FloatingActionButtonDefaults.containerColor.copy(alpha = 0.1f) to
                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.38f)
    } else {
        FloatingActionButtonDefaults.containerColor to
                MaterialTheme.colorScheme.onPrimaryContainer
    }

    var showImportDialog by rememberSaveable { mutableStateOf(false) }
    var showExportDialog by rememberSaveable { mutableStateOf(false) }
    val onImport = if (!uiState.isCollection) { { showImportDialog = true } } else null
    val onToggleSearch = if (uiState.itemType != ItemType.WANTED_PLATE) {
        { viewModel.toggleSearch() }
    } else null

    val pickCsvForImport = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let { viewModel.importItems(context, it) }
        }
    )
    val createCsvForExport = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
        onResult = { uri: Uri? ->
            uri?.let { viewModel.exportItems(context, it) }
        }
    )

    BackHandler { onBackBehavior() }

    LaunchedEffect(key1 = uiState.importResult) {
        uiState.importResult?.let { result ->
            when (result) {
                is ImportResult.Success -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
                is ImportResult.Failure -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
            viewModel.clearImportResult()
        }
    }
    LaunchedEffect(key1 = uiState.exportResult) {
        uiState.exportResult?.let { result ->
            when (result) {
                is ExportResult.Success -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
                is ExportResult.Failure -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
            viewModel.clearExportResult()
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeTopAppBar(
                title = topBarTitle,
                onOpenDrawer = onOpenDrawer,
                onToggleSearch = onToggleSearch,
                onImport = onImport,
                onExport = { showExportDialog = true },
                itemListSize = itemList.size,
                isSearchActive = uiState.isSearchActive,
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !isFabHidden,
                enter = slideInVertically(initialOffsetY = { it * 2 }),
                exit = slideOutVertically(targetOffsetY = { it * 3 })
            ) {
                FloatingActionButton(
                    onClick = onFabClick,
                    containerColor = fabContainerColor,
                    contentColor = fabContentColor
                ) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_add_24),
                        contentDescription = null
                    )
                }
            }
        },
        content = { innerPadding ->
            CatalogScreenContent(
                uiState = uiState,
                viewModel = viewModel,
                topBarState = scrollBehavior.state,
                itemList = itemList,
                onItemClick = onItemClick,
                onSortByClick = { viewModel.openSortByBottomSheet() },
                onFilterClick = { viewModel.openFilterBottomSheet() },
                modifier = modifier.padding(innerPadding),
                maxItemWidth = viewModel.getMaxItemWidth()
            )
            if (viewModel.showSortByBottomSheet.value) {
                SortByBottomSheet(
                    onDismiss = { viewModel.closeSortByBottomSheet() },
                    onClick = { sortBy ->
                        viewModel.setSortBy(sortBy)
                    },
                    sortByOptions = viewModel.getSortByOptions(),
                    selectedSortBy = uiState.sortBy
                )
            }
            if (viewModel.showFilterBottomSheet.value) {
                FilterBottomSheet(
                    itemType = uiState.itemType,
                    onDismiss = { viewModel.closeFilterBottomSheet() },
                    filters = uiState.filters,
                    filterCount = viewModel.getFilterCount(),
                    onApply = { viewModel.setFilter() },
                    onReset = { viewModel.resetFilter() },
                    localeCode = userPreferences.userCountry,
                    lengthUnit = userPreferences.lengthUnit,
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
                    hasVehicle = uiState.filters.hasVehicle,
                    toggleVehicleSwitch = { viewModel.toggleVehicleSwitch() },
                    dateSliderRange = viewModel.getDateSliderRange(),
                    dateSliderPosition = uiState.dateSliderPosition,
                    onDateSliderChange = { newPosition ->
                        viewModel.updateDateSliderPosition(newPosition)
                    },
                    costSliderRange = viewModel.getCostSliderRange(),
                    costSliderPosition = uiState.costSliderPosition,
                    onCostSliderChange = { newPosition ->
                        viewModel.updateCostSliderPosition(newPosition)
                    },
                    valueSliderRange = viewModel.getValueSliderRange(),
                    valueSliderPosition = uiState.valueSliderPosition,
                    onValueSliderChange = { newPosition ->
                        viewModel.updateValueSliderPosition(newPosition)
                    },
                    locations = viewModel.getLocations(),
                    toggleLocation = { viewModel.toggleLocationFilter(it) },
                    widthSliderRange = viewModel.getWidthSliderRange(),
                    widthSliderPosition = uiState.widthSliderPosition,
                    onWidthSliderChange = { newPosition ->
                        viewModel.updateWidthSliderPosition(newPosition)
                    },
                    colorsMain = viewModel.getColorsMain(),
                    toggleColorMain = { viewModel.toggleColorMainFilter(it) },
                    colorsSecondary = viewModel.getColorsSecondary(),
                    toggleColorSecondary = { viewModel.toggleColorSecondaryFilter(it) },
                    sourceTypes = viewModel.getSourceTypes(),
                    toggleSourceType = { viewModel.toggleSourceTypeFilter(it) },
                    sourceCountries = viewModel.getSourceCountries(),
                    toggleSourceCountry = { viewModel.toggleSourceCountryFilter(it) },
                    archivalDateSliderRange = viewModel.getArchivalDateSliderRange(),
                    archivalDateSliderPosition = uiState.archivalDateSliderPosition,
                    onArchivalDateSliderChange = { newPosition ->
                        viewModel.updateArchivalDateSliderPosition(newPosition)
                    },
                    archivalReasons = viewModel.getArchivalReasons(),
                    toggleArchivalReason = { viewModel.toggleArchivalReasonFilter(it) },
                    recipientCountries = viewModel.getRecipientCountries(),
                    toggleRecipientCountry = { viewModel.toggleRecipientCountryFilter(it) }
                )
            }
        }
    )
    if (showImportDialog) {
        ImportDialog(
            onConfirm = {
                showImportDialog = false
                pickCsvForImport.launch(arrayOf( //TODO "*/*" should be removed
                    "text/csv", "application/csv", "application/vnd.ms-excel", "*/*"
                ))
            },
            onCancel = { showImportDialog = false },
            onHelp = {
                showImportDialog = false
                onImportHelp()
            }
        )
    }
    if (showExportDialog) {
        ExportDialog(
            onConfirm = {
                showExportDialog = false
                val fileName = getFileNameForExport(topBarTitle)
                createCsvForExport.launch(fileName)
            },
            onCancel = { showExportDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogScreenContent(
    uiState: CatalogUiState,
    viewModel: CatalogViewModel,
    topBarState: TopAppBarState,
    itemList: List<Item>,
    onItemClick: (Item) -> Unit,
    onSortByClick: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
    maxItemWidth: Int = 1
) {
    // Use this if filtered items need to occupy all available width, instead of conforming to
    // the maximum available width from _allItems
    //val maxItemWidth = itemList.maxOfOrNull { it.toItemDetails().width ?: 1 } ?: 1
    val listState = rememberLazyListState()
    val isAtTop = remember { derivedStateOf {
        (listState.firstVisibleItemIndex == 0) && (listState.firstVisibleItemScrollOffset == 0)
    } }
    val itemIndex = remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val topBarCollapsedFraction = remember { derivedStateOf { topBarState.collapsedFraction } }
    val isTopRowHidden by viewModel.isTopRowHidden.collectAsStateWithLifecycle()
    val (contentPadding, verticalSpace) = if (uiState.itemType == ItemType.WANTED_PLATE) {
        PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 16.dp) to 12.dp
    } else PaddingValues(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 8.dp) to 8.dp

    // Use itemIndex and topBarCollapsedFraction to update TopRow visibility in viewModel
    LaunchedEffect(itemIndex.value, topBarCollapsedFraction.value) {
        viewModel.updateTopRowVisibility(itemIndex.value, topBarCollapsedFraction.value)
    }

    LazyColumn(
        state = listState,
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(verticalSpace),
        modifier = modifier.fillMaxWidth()
    ) {
        stickyHeader {
            TopRow(
                isHidden = isTopRowHidden,
                isAtTop = isAtTop.value,
                onSortByClick = onSortByClick,
                onFilterClick = onFilterClick,
                filterCount = uiState.activeFilterCount
            )
        }
        if (uiState.isLoading) {
            item {
                Loading(modifier = Modifier.padding(top = 256.dp))
            }
        } else if (itemList.isEmpty()) {
            item {
                if (uiState.searchQuery != "") {
                    EmptyList(
                        painter = painterResource(R.drawable.rounded_frame_inspect),
                        message = stringResource(R.string.empty_list_search_msg),
                        description = stringResource(
                            R.string.empty_list_search_desc, uiState.searchQuery
                        )
                    )
                } else if (uiState.filters != FilterData()) {
                    EmptyList(
                        message = stringResource(R.string.empty_list_filter_msg),
                        description = stringResource(R.string.empty_list_filter_desc)
                    )
                } else if (uiState.itemType == ItemType.WANTED_PLATE) {
                    EmptyList(
                        painter = painterResource(R.drawable.rounded_heart),
                        message = stringResource(R.string.empty_list_wishlist_msg),
                        description = stringResource(R.string.empty_list_wishlist_desc)
                    )
                } else if (uiState.itemType == ItemType.FORMER_PLATE) {
                    EmptyList(
                        painter = painterResource(R.drawable.rounded_archive),
                        message = stringResource(R.string.empty_list_archive_msg),
                        description = stringResource(R.string.empty_list_archive_desc)
                    )
                } else if (uiState.isCollection) {
                    EmptyList(
                        painter = painterResource(R.drawable.rounded_label),
                        message = stringResource(R.string.empty_list_collection_msg),
                        description = stringResource(R.string.empty_list_collection_desc),
                        iconModifier = Modifier.offset(x = 2.dp),
                        collectionEmoji = viewModel.getCollectionEmoji(),
                        collectionColor = viewModel.getCollectionColor()
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
            item { // an effectively empty item for improved TopRow manipulation
                Spacer(modifier = Modifier.height(0.dp))
            }
            items(items = itemList, key = { it.toItemDetails().id ?: 0 }) { item ->
                val details = item.toItemDetails()

                if (uiState.itemType == ItemType.WANTED_PLATE) {
                    WishlistCard(
                        country = details.country ?: "",
                        region1st = details.region1st,
                        region2nd = details.region2nd,
                        region3rd = details.region3rd,
                        type = details.type ?: "",
                        periodStart = details.periodStart,
                        periodEnd = details.periodEnd,
                        year = details.year,
                        regNo = details.regNo,
                        imagePath = details.imagePath,
                        notes = details.notes,
                    ) {
                        onItemClick(item)
                    }
                } else {
                    ItemCard(
                        title = details.regNo ?: "",
                        imagePath = details.imagePath,
                        itemWidth = details.width,
                        maxWidth = maxItemWidth
                    ) {
                        onItemClick(item)
                    }
                }
            }
            item {
                EndOfList(
                    hasCircle = uiState.itemType != ItemType.WANTED_PLATE,
                    text = if (uiState.itemType == ItemType.WANTED_PLATE) {
                        pluralStringResource(R.plurals.wishlist_list_size, itemList.size, itemList.size)
                    } else {
                        pluralStringResource(R.plurals.plates_list_size, itemList.size, itemList.size)
                    }
                )
            }
        }
    }
}
