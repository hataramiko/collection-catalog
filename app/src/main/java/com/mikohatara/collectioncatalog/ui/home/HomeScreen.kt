package com.mikohatara.collectioncatalog.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.ui.components.FilterBottomSheet
import com.mikohatara.collectioncatalog.ui.components.HomeTopAppBar
import com.mikohatara.collectioncatalog.ui.components.ItemCard
import com.mikohatara.collectioncatalog.ui.components.SortByBottomSheet

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
                uiState = uiState,
                viewModel = viewModel,
                topBarState = scrollBehavior.state,
                itemList = itemList,
                modifier = modifier.padding(innerPadding),
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
            /*  TODO add different messages and visualizations for different types of empty
            *
            *   Is the entire database empty? Just Wishlist? Archive? A collection?
            *   Or due to filters?
            *   Also a proper empty state for Stats.
            *
            * */
            item {
                Text(
                    text = stringResource(R.string.is_empty_message),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 256.dp)
                )
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopRow(
    isHidden: Boolean,
    isAtTop: Boolean,
    onSortByClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val offset by animateFloatAsState(if (isHidden) -64f else 0f)
    val backgroundColor by animateColorAsState(
        if (isAtTop) TopAppBarDefaults.topAppBarColors().containerColor else
            TopAppBarDefaults.topAppBarColors().scrolledContainerColor
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 24.dp,
            bottomEnd = 24.dp
        ),
        modifier = Modifier
            .requiredWidth(screenWidth.dp)
            .offset(0.dp, offset.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            OutlinedButton(
                onClick = { onSortByClick() },
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
    }
}

@Composable
private fun Loading() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()
            .width(IntrinsicSize.Max)
            .padding(top = 256.dp)
    ) {
        var textWidth by remember { mutableIntStateOf(0) }

        Text(
            text = stringResource(R.string.loading),
            modifier = Modifier
                .offset(x = 4.dp)
                .onGloballyPositioned { textWidth = it.size.width }
        )
        /*LinearProgressIndicator(
            modifier = Modifier
                .padding(top = 4.dp)
                .width((textWidth / 2).dp)
        )*/
    }
}
