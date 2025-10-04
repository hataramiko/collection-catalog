package com.mikohatara.collectioncatalog.ui.home

import android.content.Context
import android.icu.util.MeasureUnit
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.mikohatara.collectioncatalog.data.FormerPlate
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.data.UserPreferences
import com.mikohatara.collectioncatalog.ui.catalog.ExportResult
import com.mikohatara.collectioncatalog.ui.catalog.FilterData
import com.mikohatara.collectioncatalog.ui.catalog.ImportResult
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
import com.mikohatara.collectioncatalog.util.getFileNameForExport

@Composable
fun ArchiveScreen(
    viewModel: ArchiveViewModel = hiltViewModel(),
    onAddItem: () -> Unit,
    onItemClick: (FormerPlate) -> Unit,
    onOpenDrawer: () -> Unit,
    onImportHelp: () -> Unit
) {
    val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ArchiveScreen(
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
private fun ArchiveScreen(
    itemList: List<FormerPlate>,
    uiState: ArchiveUiState,
    viewModel: ArchiveViewModel,
    userPreferences: UserPreferences,
    context: Context,
    onAddItem: () -> Unit,
    onItemClick: (FormerPlate) -> Unit,
    onOpenDrawer: () -> Unit,
    onImportHelp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val isFabHidden by viewModel.isTopRowHidden.collectAsStateWithLifecycle()
    val exportTitle = stringResource(R.string.archive)
    val onBackBehavior = { if (uiState.isSearchActive) viewModel.toggleSearch() }
    val pickCsvForImport = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                viewModel.importItems(context, it)
            }
        }
    )
    val createCsvForExport = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
        onResult = { uri: Uri? ->
            uri?.let {
                viewModel.exportItems(context, it)
            }
        }
    )
    var showImportDialog by rememberSaveable { mutableStateOf(false) }
    var showExportDialog by rememberSaveable { mutableStateOf(false) }

    BackHandler {
        onBackBehavior()
    }

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

    val (fabContainerColor, fabContentColor) = FloatingActionButtonDefaults.containerColor to
            MaterialTheme.colorScheme.onPrimaryContainer

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeTopAppBar(
                title = stringResource(R.string.archive),
                onOpenDrawer = onOpenDrawer,
                onToggleSearch = { viewModel.toggleSearch() },
                onImport = { showImportDialog = true },
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
                    onClick = onAddItem,
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
            ArchiveScreenContent(
                uiState = uiState,
                viewModel = viewModel,
                topBarState = scrollBehavior.state,
                itemList = itemList,
                onItemClick = onItemClick,
                onSortByClick = { viewModel.showSortByBottomSheet.value = true },
                onFilterClick = { viewModel.openFilterBottomSheet() },
                modifier = modifier.padding(innerPadding)
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
                    itemType = ItemType.FORMER_PLATE,
                    onDismiss = { viewModel.showFilterBottomSheet.value = false },
                    filters = uiState.filters,
                    filterCount = viewModel.getFilterCount(),
                    onApply = { viewModel.setFilter() },
                    onReset = { viewModel.resetFilter() },
                    localeCode = userPreferences.userCountry,
                    lengthUnit = MeasureUnit.MILLIMETER,
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
                pickCsvForImport.launch( //TODO improve
                    arrayOf("text/csv", "application/csv", "application/vnd.ms-excel", "*/*"))
            },
            onCancel = { showImportDialog = false },
            onHelp = {
                onImportHelp()
                showImportDialog = false
            }
        )
    }
    if (showExportDialog) {
        ExportDialog(
            onConfirm = {
                showExportDialog = false
                val fileName = getFileNameForExport(exportTitle)
                createCsvForExport.launch(fileName)
            },
            onCancel = { showExportDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun ArchiveScreenContent(
    uiState: ArchiveUiState,
    viewModel: ArchiveViewModel,
    topBarState: TopAppBarState,
    itemList: List<FormerPlate>,
    onItemClick: (FormerPlate) -> Unit,
    onSortByClick: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val maxItemWidth = itemList.maxOfOrNull { it.size.width ?: 1 } ?: 1
    val listState = rememberLazyListState()
    val isAtTop = remember {
        derivedStateOf { (listState.firstVisibleItemIndex == 0) &&
            (listState.firstVisibleItemScrollOffset == 0)}
    }
    val itemIndex = remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val topBarCollapsedFraction = remember { derivedStateOf { topBarState.collapsedFraction } }
    val isTopRowHidden by viewModel.isTopRowHidden.collectAsStateWithLifecycle()
    // Use itemIndex and topBarCollapsedFraction to update TopRow visibility in viewModel
    LaunchedEffect(itemIndex.value, topBarCollapsedFraction.value) {
        viewModel.updateTopRowVisibility(itemIndex.value, topBarCollapsedFraction.value)
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        stickyHeader {
            TopRow(
                isTopRowHidden,
                isAtTop.value,
                onSortByClick,
                onFilterClick,
                uiState.activeFilterCount
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
                            R.string.empty_list_search_desc, uiState.searchQuery)
                    )
                } else if (uiState.filters != FilterData()) {
                    EmptyList(
                        message = stringResource(R.string.empty_list_filter_msg),
                        description = stringResource(R.string.empty_list_filter_desc)
                    )
                } else {
                    EmptyList(
                        painter = painterResource(R.drawable.rounded_archive),
                        message = stringResource(R.string.empty_list_archive_msg),
                        description = stringResource(R.string.empty_list_archive_desc)
                    )
                }
            }
        } else {
            item { // an effectively empty item for improved TopRow manipulation
                Spacer(modifier = Modifier.height(0.dp))
            }
            items(items = itemList, key = { it.id }) { item ->
                ItemCard(
                    title = item.uniqueDetails.regNo,
                    imagePath = item.uniqueDetails.imagePath,
                    itemWidth = item.size.width,
                    maxWidth = maxItemWidth,
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
