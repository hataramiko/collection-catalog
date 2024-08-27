package com.mikohatara.collectioncatalog.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.WantedPlate
import com.mikohatara.collectioncatalog.ui.components.WishlistCard
import com.mikohatara.collectioncatalog.ui.components.WishlistTopAppBar

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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            WishlistTopAppBar(
                title = stringResource(R.string.wishlist),
                onOpenDrawer = onOpenDrawer,
                onAddItem = onAddItem,
                onSortBy = { /*TODO*/ },
                onFilter = { /*TODO*/ },
                scrollBehavior = scrollBehavior
            )
        },
        content = { innerPadding ->
            WishlistScreenContent(
                uiState = uiState,
                viewModel = viewModel,
                itemList = itemList,
                modifier = Modifier.padding(innerPadding),
                onItemClick = onItemClick
            )
        }
    )
}

@Composable
private fun WishlistScreenContent(
    uiState: WishlistUiState,
    viewModel: WishlistViewModel,
    itemList: List<WantedPlate>,
    modifier: Modifier = Modifier,
    onItemClick: (WantedPlate) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        if (uiState.isLoading) {
            item {
                Loading()
            }
        } else if (itemList.isEmpty()) {
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
        LinearProgressIndicator(
            modifier = Modifier
                .padding(top = 4.dp)
                .width((textWidth / 2).dp)
        )
    }
}
