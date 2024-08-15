package com.mikohatara.collectioncatalog.ui.home

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.ui.components.FilterBottomSheet
import com.mikohatara.collectioncatalog.ui.components.HomeTopAppBar
import com.mikohatara.collectioncatalog.ui.components.ItemCard
import com.mikohatara.collectioncatalog.ui.components.SortByBottomSheet
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme

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
    val scope = rememberCoroutineScope()
    val topBarState = rememberTopAppBarState()
    val scrollState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeTopAppBar(
                title = stringResource(R.string.plates) + " (${itemList.size})",
                onOpenDrawer = onOpenDrawer,
                onAddItem = onAddItem,
                scrollBehavior = scrollBehavior
            )
        },
        content = { innerPadding ->
            HomeScreenContent(
                viewModel = viewModel,
                itemList = itemList,
                modifier = modifier
                    .padding(innerPadding),
                    //.verticalScroll(rememberScrollState())
                onItemClick = onItemClick,
                onSortByClick = { viewModel.showSortByBottomSheet.value = true },
                onFilterClick = { viewModel.showFilterBottomSheet.value = true }
            )
            if(viewModel.showSortByBottomSheet.value) {
                SortByBottomSheet(
                    onDismiss = { viewModel.showSortByBottomSheet.value = false },
                    uiState = uiState,
                    viewModel = viewModel
                )
            }
            if (viewModel.showFilterBottomSheet.value) {
                FilterBottomSheet(
                    onDismiss = { viewModel.showFilterBottomSheet.value = false },
                    uiState = uiState,
                    viewModel = viewModel
                )
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeScreenContent(
    viewModel: HomeViewModel,
    itemList: List<Plate>,
    modifier: Modifier = Modifier,
    onItemClick: (Plate) -> Unit,
    onSortByClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    val maxWidth = itemList.maxOfOrNull { it.size.width ?: 1 } ?: 1

    // For testing TopRow scroll handling
    val listState = rememberLazyListState()
    val itemIndex = remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val scrollState = viewModel.isScrollingUp.collectAsState()

    viewModel.updateScrollPosition(itemIndex.value)
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        stickyHeader {
            TopRow(scrollState, onSortByClick, onFilterClick)
        }
        if (itemList.isEmpty()) {
            item {
                Text(
                    text = "Collection is empty.\nPress + to add an item",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 200.dp)
                )
            }
            Log.d("itemList is empty", itemList.toString())
        } else {
            items(items = itemList, key = { it.id }) { item ->
                ItemCard(
                    title = item.uniqueDetails.regNo,
                    imagePath = item.uniqueDetails.imagePath,
                    itemWidth = item.size.width,
                    maxWidth = maxWidth
                ) {
                    onItemClick(item)
                }
            }
        }
    }
}

@Composable
private fun TopRow(
    scrollState: State<Boolean?>,
    onSortByClick: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // For testing scroll handling
    val position by animateFloatAsState(if (scrollState.value == true) -256f else 0f)

    /*
    *   Did some changes to the structure, paddings, and background colors
    *   for testing the scroll behavior stuff commented out here and in the ViewModel.
    *
    *   TODO
    *   - test with firstVisibleItemOffset
    *   - try FAB approach, utilizing -''-Index?
    *
    * */

    Column(
        modifier = Modifier.graphicsLayer { translationY = (position) }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                //.background(MaterialTheme.colorScheme.background)
        ) {
            OutlinedButton(
                onClick = { onSortByClick() },
                colors = ButtonDefaults
                    .outlinedButtonColors(MaterialTheme.colorScheme.background),
                modifier = Modifier
                    .weight(1f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.rounded_swap_vert),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    stringResource(R.string.sort_by),
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = { onFilterClick() },
                colors = ButtonDefaults
                    .outlinedButtonColors(MaterialTheme.colorScheme.background),
                modifier = Modifier
                    .weight(1f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.rounded_filter),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 6.dp)
                )
                Text(
                    stringResource(R.string.filter),
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }
        /*Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(MaterialTheme.colorScheme.background)
        )
        HorizontalDivider(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                //.padding(bottom = 4.dp)
                .requiredWidth(1024.dp)
        )*/
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    CollectionCatalogTheme {
        //HomeScreenContent(samplePlates, onAddItem = {}, onItemClick = {}, onOpenDrawer = {})
    }
}
