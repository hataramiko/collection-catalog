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
import androidx.compose.foundation.combinedClickable
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
import com.mikohatara.collectioncatalog.data.CollectionColor
import com.mikohatara.collectioncatalog.data.Item
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.data.UserPreferences
import com.mikohatara.collectioncatalog.ui.components.CatalogTopAppBar
import com.mikohatara.collectioncatalog.ui.components.EmptyList
import com.mikohatara.collectioncatalog.ui.components.EndOfList
import com.mikohatara.collectioncatalog.ui.components.ExportDialog
import com.mikohatara.collectioncatalog.ui.components.FilterBottomSheet
import com.mikohatara.collectioncatalog.ui.components.FilterListData
import com.mikohatara.collectioncatalog.ui.components.FilterSliderData
import com.mikohatara.collectioncatalog.ui.components.FilterSwitchData
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
    onBack: () -> Unit,
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
        onBack = onBack,
        onAddItem = onAddItem,
        onItemClick = onItemClick,
        onOpenDrawer = onOpenDrawer,
        onImportHelp = onImportHelp,
        updateTopRowVisibility = viewModel::updateTopRowVisibility,
        openSortByBottomSheet = viewModel::openSortByBottomSheet,
        openFilterBottomSheet = viewModel::openFilterBottomSheet,
        closeSortByBottomSheet = viewModel::closeSortByBottomSheet,
        closeFilterBottomSheet = viewModel::closeFilterBottomSheet,
        setSortBy = viewModel::setSortBy,
        setFilter = viewModel::setFilter,
        resetFilter = viewModel::resetFilter,
        importItems = viewModel::importItems,
        exportItems = viewModel::exportItems,
        clearImportResult = viewModel::clearImportResult,
        clearExportResult = viewModel::clearExportResult,
        toggleSelection = viewModel::toggleSelection,
        clearSelection = viewModel::clearSelection,
        toggleSearch = viewModel::toggleSearch,
        updateSearchQuery = viewModel::updateSearchQuery,
        hideSelectedItems = viewModel::hideSelectedItems,
        clearHiddenItems = viewModel::clearHiddenItems,
        showToast = viewModel::showToast
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
    onBack: () -> Unit,
    onAddItem: () -> Unit,
    onItemClick: (Item) -> Unit,
    onOpenDrawer: () -> Unit,
    onImportHelp: () -> Unit,
    updateTopRowVisibility: (Int, Float) -> Unit,
    openSortByBottomSheet: () -> Unit,
    openFilterBottomSheet: () -> Unit,
    closeSortByBottomSheet: () -> Unit,
    closeFilterBottomSheet: () -> Unit,
    setSortBy: (SortBy) -> Unit,
    setFilter: () -> Unit,
    resetFilter: () -> Unit,
    importItems: (Context, Uri) -> Unit,
    exportItems: (Context, Uri) -> Unit,
    clearImportResult: () -> Unit,
    clearExportResult: () -> Unit,
    toggleSelection: (Int) -> Unit,
    clearSelection: () -> Unit,
    toggleSearch: () -> Unit,
    updateSearchQuery: (String) -> Unit,
    hideSelectedItems: () -> Unit,
    clearHiddenItems: () -> Unit,
    showToast: (Context, String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults
        .enterAlwaysScrollBehavior(rememberTopAppBarState())

    val (hideToast, unhideToast) = if (uiState.itemType == ItemType.WANTED_PLATE) {
        pluralStringResource(
            R.plurals.hide_wishlist_size,
            uiState.selectedItemIds.size, uiState.selectedItemIds.size
        ) to pluralStringResource(
            R.plurals.unhide_wishlist_size,
            uiState.hiddenItemIds.size, uiState.hiddenItemIds.size
        )
    } else {
        pluralStringResource(
            R.plurals.hide_plates_size,
            uiState.selectedItemIds.size, uiState.selectedItemIds.size
        ) to pluralStringResource(
            R.plurals.unhide_plates_size,
            uiState.hiddenItemIds.size, uiState.hiddenItemIds.size
        )
    }
    val redirectToast = stringResource(R.string.add_plate_redirect_msg)
    val onFabClick = {
        if (uiState.isCollection) {
            showToast(context, redirectToast, Toast.LENGTH_SHORT)
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
    val onDismissImportDialog = { showImportDialog = false }
    val onDismissExportDialog = { showExportDialog = false }
    val onImport = if (!uiState.isCollection) { { showImportDialog = true } } else null
    val onToggleSearch = if (uiState.itemType != ItemType.WANTED_PLATE) {
        { toggleSearch() }
    } else null

    val pickCsvForImport = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? -> uri?.let { importItems(context, it) } }
    )
    val createCsvForExport = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
        onResult = { uri: Uri? -> uri?.let { exportItems(context, it) } }
    )

    val onBackBehavior = {
        if (uiState.isSelectionMode) {
            clearSelection()
        } else if (uiState.isSearchActive) {
            toggleSearch()
        } else if (uiState.itemType != ItemType.PLATE || uiState.isCollection) {
            onBack()
        }
    }
    BackHandler { onBackBehavior() }

    LaunchedEffect(key1 = uiState.importResult) {
        uiState.importResult?.let { result ->
            when (result) {
                is ImportResult.Success -> {
                    showToast(context, result.message, Toast.LENGTH_LONG)
                }
                is ImportResult.Failure -> {
                    showToast(context, result.message, Toast.LENGTH_LONG)
                }
            }
            clearImportResult()
        }
    }
    LaunchedEffect(key1 = uiState.exportResult) {
        uiState.exportResult?.let { result ->
            when (result) {
                is ExportResult.Success -> {
                    showToast(context, result.message, Toast.LENGTH_LONG)
                }
                is ExportResult.Failure -> {
                    showToast(context, result.message, Toast.LENGTH_LONG)
                }
            }
            clearExportResult()
        }
    }

    val countries by remember(uiState.items) { mutableStateOf(viewModel
        .getCountries()) }
    val types by remember(uiState.items) { mutableStateOf(viewModel
        .getTypes()) }
    val locations by remember(uiState.items) { mutableStateOf(viewModel
        .getLocations()) }
    val colorsMain by remember(uiState.items) { mutableStateOf(viewModel
        .getColorsMain()) }
    val colorsSecondary by remember(uiState.items) { mutableStateOf(viewModel
        .getColorsSecondary()) }
    val sourceTypes by remember(uiState.items) { mutableStateOf(viewModel
        .getSourceTypes()) }
    val sourceCountries by remember(uiState.items) { mutableStateOf(viewModel
        .getSourceCountries()) }
    val archivalReasons by remember(uiState.items) { mutableStateOf(viewModel
        .getArchivalReasons()) }
    val recipientCountries by remember(uiState.items) { mutableStateOf(viewModel
        .getRecipientCountries()) }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CatalogTopAppBar(
                title = uiState.topBarTitle,
                onOpenDrawer = onOpenDrawer,
                onClearSelection = clearSelection,
                onToggleSearch = onToggleSearch,
                onHide = {
                    hideSelectedItems()
                    showToast(context, hideToast, Toast.LENGTH_SHORT)
                },
                onUnhide = {
                    clearHiddenItems()
                    showToast(context, unhideToast, Toast.LENGTH_SHORT)
                },
                onImport = onImport,
                onExport = { showExportDialog = true },
                itemListSize = itemList.size,
                hiddenItemsSize = uiState.hiddenItemIds.size,
                isSelectionMode = uiState.isSelectionMode,
                selectionSize = uiState.selectedItemIds.size,
                isSearchActive = uiState.isSearchActive,
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { updateSearchQuery(it) },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !uiState.isTopRowHidden,
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
                topBarState = scrollBehavior.state,
                itemList = itemList,
                onItemClick = onItemClick,
                onToggleSelection = toggleSelection,
                updateTopRowVisibility = updateTopRowVisibility,
                isTopRowHidden = uiState.isTopRowHidden,
                onSortByClick = openSortByBottomSheet,
                onFilterClick = openFilterBottomSheet,
                maxItemWidth = uiState.maxItemWidth,
                collectionEmoji = uiState.collectionEmoji,
                collectionColor = uiState.collectionColor,
                modifier = modifier.padding(innerPadding)
            )
            if (uiState.showSortByBottomSheet) {
                SortByBottomSheet(
                    onDismiss = closeSortByBottomSheet,
                    onClick = { setSortBy(it) },
                    sortByOptions = uiState.sortByOptions,
                    selectedSortBy = uiState.sortBy
                )
            }
            if (uiState.showFilterBottomSheet) {
                FilterBottomSheet(
                    itemType = uiState.itemType,
                    onDismiss = closeFilterBottomSheet,
                    filters = uiState.filters,
                    filterCount = uiState.activeFilterCount,
                    onApply = setFilter,
                    onReset = resetFilter,
                    localeCode = userPreferences.userCountry,
                    lengthUnit = userPreferences.lengthUnit,
                    countryList = FilterListData(
                        values = countries,
                        onToggleValue = { viewModel.toggleCountryFilter(it) }
                    ),
                    typeList = FilterListData(
                        values = types,
                        onToggleValue = { viewModel.toggleTypeFilter(it) }
                    ),
                    periodSlider = FilterSliderData(
                        range = viewModel.getYearSliderRange(),
                        position = uiState.periodSliderPosition ?: viewModel.getYearSliderRange(),
                        onValueChange = { newPosition ->
                            viewModel.updatePeriodSliderPosition(newPosition)
                        }
                    ),
                    yearSlider = FilterSliderData(
                        range = viewModel.getYearSliderRange(),
                        position = uiState.yearSliderPosition ?: viewModel.getYearSliderRange(),
                        onValueChange = { newPosition ->
                            viewModel.updateYearSliderPosition(newPosition)
                        }
                    ),
                    vehicleSwitch = FilterSwitchData(
                        isTrue = uiState.filters.hasVehicle,
                        onToggleSwitch = { viewModel.toggleVehicleSwitch() }
                    ),
                    dateSlider = FilterSliderData(
                        range = viewModel.getDateSliderRange(),
                        position = uiState.dateSliderPosition ?: viewModel.getDateSliderRange(),
                        onValueChange = { newPosition ->
                            viewModel.updateDateSliderPosition(newPosition)
                        }
                    ),
                    costSlider = FilterSliderData(
                        range = viewModel.getCostSliderRange(),
                        position = uiState.costSliderPosition ?: viewModel.getCostSliderRange(),
                        onValueChange = { newPosition ->
                            viewModel.updateCostSliderPosition(newPosition)
                        }
                    ),
                    valueSlider = FilterSliderData(
                        range = viewModel.getValueSliderRange(),
                        position = uiState.valueSliderPosition ?: viewModel.getValueSliderRange(),
                        onValueChange = { newPosition ->
                            viewModel.updateValueSliderPosition(newPosition)
                        }
                    ),
                    locationList = FilterListData(
                        values = locations,
                        onToggleValue = { viewModel.toggleLocationFilter(it) }
                    ),
                    widthSlider = FilterSliderData(
                        range = viewModel.getWidthSliderRange(),
                        position = uiState.widthSliderPosition ?: viewModel.getWidthSliderRange(),
                        onValueChange = { newPosition ->
                            viewModel.updateWidthSliderPosition(newPosition)
                        }
                    ),
                    colorsMainList = FilterListData(
                        values = colorsMain,
                        onToggleValue = { viewModel.toggleColorMainFilter(it) }
                    ),
                    colorsSecondaryList = FilterListData(
                        values = colorsSecondary,
                        onToggleValue = { viewModel.toggleColorSecondaryFilter(it) }
                    ),
                    sourceTypeList = FilterListData(
                        values = sourceTypes,
                        onToggleValue = { viewModel.toggleSourceTypeFilter(it) }
                    ),
                    sourceCountryList = FilterListData(
                        values = sourceCountries,
                        onToggleValue = { viewModel.toggleSourceCountryFilter(it) }
                    ),
                    archivalDateSlider = FilterSliderData(
                        range = viewModel.getArchivalDateSliderRange(),
                        position = uiState
                            .archivalDateSliderPosition ?: viewModel.getArchivalDateSliderRange(),
                        onValueChange = { newPosition ->
                            viewModel.updateArchivalDateSliderPosition(newPosition)
                        }
                    ),
                    archivalReasonList = FilterListData(
                        values = archivalReasons,
                        onToggleValue = { viewModel.toggleArchivalReasonFilter(it) }
                    ),
                    recipientCountryList = FilterListData(
                        values = recipientCountries,
                        onToggleValue = { viewModel.toggleRecipientCountryFilter(it) }
                    )
                )
            }
        }
    )
    if (showImportDialog) {
        ImportDialog(
            onConfirm = {
                onDismissImportDialog()
                pickCsvForImport.launch(arrayOf( //TODO "*/*" should be removed
                    "text/csv", "application/csv", "application/vnd.ms-excel", "*/*"
                ))
            },
            onCancel = onDismissImportDialog,
            onHelp = {
                onDismissImportDialog()
                onImportHelp()
            }
        )
    }
    if (showExportDialog) {
        ExportDialog(
            onConfirm = {
                onDismissExportDialog()
                val fileName = getFileNameForExport(uiState.topBarTitle)
                createCsvForExport.launch(fileName)
            },
            onCancel = onDismissExportDialog
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogScreenContent(
    uiState: CatalogUiState,
    topBarState: TopAppBarState,
    itemList: List<Item>,
    onItemClick: (Item) -> Unit,
    onToggleSelection: (Int) -> Unit,
    updateTopRowVisibility: (Int, Float) -> Unit,
    isTopRowHidden: Boolean,
    onSortByClick: () -> Unit,
    onFilterClick: () -> Unit,
    maxItemWidth: Int,
    collectionEmoji: String?,
    collectionColor: CollectionColor,
    modifier: Modifier = Modifier
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
    val contentPadding = PaddingValues(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 8.dp)

    // Use itemIndex and topBarCollapsedFraction to update TopRow visibility
    LaunchedEffect(itemIndex.value, topBarCollapsedFraction.value) {
        updateTopRowVisibility(itemIndex.value, topBarCollapsedFraction.value)
    }

    LazyColumn(
        state = listState,
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        stickyHeader {
            TopRow(
                isHidden = isTopRowHidden,
                isAtTop = isAtTop.value,
                isSelectionMode = uiState.isSelectionMode,
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
                        collectionEmoji = collectionEmoji,
                        collectionColor = collectionColor
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
            item { // An effectively empty item for improved TopRow manipulation
                Spacer(modifier = Modifier.height(0.dp))
            }
            items(items = itemList, key = { it.toItemDetails().id ?: 0 }) { item ->
                val details = item.toItemDetails()
                val itemId = details.id
                // Null check for safety, the itemId should never be null.
                val onClickModifier = if (itemId != null) {
                    Modifier.combinedClickable(
                        onClick = {
                            if (uiState.isSelectionMode) {
                                onToggleSelection(itemId)
                            } else {
                                onItemClick(item)
                            }
                        },
                        onLongClick = {
                            onToggleSelection(itemId)
                        }
                    )
                } else { // This block should never be reached
                    Modifier
                }

                if (uiState.itemType == ItemType.WANTED_PLATE) {
                    WishlistCard(
                        imagePath = details.imagePath,
                        country = details.country,
                        region1st = details.region1st,
                        region2nd = details.region2nd,
                        region3rd = details.region3rd,
                        type = details.type,
                        periodStart = details.periodStart,
                        periodEnd = details.periodEnd,
                        year = details.year,
                        regNo = details.regNo,
                        notes = details.notes,
                        isSelected = itemId != null && itemId in uiState.selectedItemIds,
                        modifier = onClickModifier
                    )
                } else {
                    ItemCard(
                        title = details.regNo ?: "",
                        imagePath = details.imagePath,
                        itemWidth = details.width,
                        maxWidth = maxItemWidth,
                        isSelected = itemId != null && itemId in uiState.selectedItemIds,
                        modifier = onClickModifier
                    )
                }
            }
            item {
                EndOfList(
                    hasCircle = true,
                    text = if (uiState.itemType == ItemType.WANTED_PLATE) {
                        pluralStringResource(
                            R.plurals.wishlist_list_size,
                            itemList.size, itemList.size)
                    } else {
                        pluralStringResource(
                            R.plurals.plates_list_size,
                            itemList.size, itemList.size)
                    }
                )
            }
        }
    }
}
