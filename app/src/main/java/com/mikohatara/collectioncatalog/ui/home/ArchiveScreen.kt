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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.FormerPlate
import com.mikohatara.collectioncatalog.ui.components.EmptyList
import com.mikohatara.collectioncatalog.ui.components.EndOfList
import com.mikohatara.collectioncatalog.ui.components.HomeTopAppBar
import com.mikohatara.collectioncatalog.ui.components.ItemCard
import com.mikohatara.collectioncatalog.ui.components.Loading
import com.mikohatara.collectioncatalog.ui.components.TopRow

@Composable
fun ArchiveScreen(
    viewModel: ArchiveViewModel = hiltViewModel(),
    onAddItem: () -> Unit,
    onItemClick: (FormerPlate) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WishlistScreen(
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
private fun WishlistScreen(
    itemList: List<FormerPlate>,
    uiState: ArchiveUiState,
    viewModel: ArchiveViewModel,
    onAddItem: () -> Unit,
    onItemClick: (FormerPlate) -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val isFabHidden by viewModel.isTopRowHidden.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeTopAppBar(
                title = stringResource(R.string.archive),
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
            ArchiveScreenContent(
                uiState = uiState,
                viewModel = viewModel,
                topBarState = scrollBehavior.state,
                itemList = itemList,
                onItemClick = onItemClick,
                modifier = modifier.padding(innerPadding)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun ArchiveScreenContent(
    uiState: ArchiveUiState,
    viewModel: ArchiveViewModel,
    topBarState: TopAppBarState,
    itemList: List<FormerPlate>,
    onItemClick: (FormerPlate) -> Unit,
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
    viewModel.updateTopRowVisibility(itemIndex.value, topBarCollapsedFraction.value)

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        stickyHeader {
            TopRow(viewModel.isTopRowHidden.value, isAtTop.value, {}, {})
        }
        if (uiState.isLoading) {
            item {
                Loading()
            }
        } else if (itemList.isEmpty()) {
            item {
                EmptyList(
                    painter = painterResource(R.drawable.rounded_history),
                    message = stringResource(R.string.empty_list_archive_msg)
                )
            }
        } else {
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
                EndOfList(hasCircle = true)
            }
        }
    }
}
