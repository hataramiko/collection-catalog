package com.mikohatara.collectioncatalog.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.WantedPlate
import com.mikohatara.collectioncatalog.ui.components.HomeTopAppBar
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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeTopAppBar(
                title = stringResource(R.string.wishlist),
                onOpenDrawer = onOpenDrawer,
                onAddItem = onAddItem,
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
        contentPadding = PaddingValues(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        if (uiState.isLoading) {
            item {
                Text(
                    text = stringResource(R.string.loading),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 256.dp)
                )
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
                    region = item.commonDetails.region1st,
                    type = item.commonDetails.type,
                    period = item.commonDetails.periodStart.toString() + " – " +
                        item.commonDetails.periodEnd.toString(),
                    year = item.commonDetails.year.toString(),
                    imagePath = item.imagePath
                ) {
                    onItemClick(item)
                }
            }
        }
    }
}
