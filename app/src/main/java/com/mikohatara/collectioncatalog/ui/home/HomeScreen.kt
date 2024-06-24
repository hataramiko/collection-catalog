package com.mikohatara.collectioncatalog.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.samplePlates
import com.mikohatara.collectioncatalog.ui.components.HomeScreenTopAppBar
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

    HomeScreenContent(
        itemList = uiState.items,
        onAddItem = onAddItem,
        onItemClick = onItemClick,
        onOpenDrawer = onOpenDrawer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    itemList: List<Plate>,
    onAddItem: () -> Unit,
    onItemClick: (Plate) -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { HomeScreenTopAppBar(
            title = stringResource(R.string.plates) + " (${itemList.size})",
            onOpenDrawer = onOpenDrawer,
            onAddItem = onAddItem,
            scrollBehavior = scrollBehavior
        ) },
        content = { innerPadding ->
            HomeBody(
                itemList = itemList,
                modifier = modifier
                    .padding(innerPadding),
                    //.verticalScroll(rememberScrollState())
                onItemClick = onItemClick
            )

            if(showBottomSheet) {
                SortByBottomSheet(onDismiss = { showBottomSheet = false })
            }
        }
    )
}

@Composable
fun HomeBody(
    itemList: List<Plate>,
    modifier: Modifier = Modifier,
    onItemClick: (Plate) -> Unit
) {
    val maxWidth = itemList.maxOfOrNull { it.measurements.width ?: 0.0 } ?: 0.0
    //Log.d("maxWidth", maxWidth.toString())

    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        item {
            TopRow()
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
            items(itemList) { item ->
                ItemCard(
                    item = item,
                    maxWidth = maxWidth
                ) {
                    onItemClick(item)
                }
            }
        }
    }
}

@Composable
private fun TopRow() {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        OutlinedButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(R.drawable.rounded_swap_vert),
                contentDescription = null
            )
            Text(stringResource(R.string.sort_by))
        }
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(R.drawable.rounded_filter),
                contentDescription = null
            )
            Text(stringResource(R.string.filter))
        }
    }
    HorizontalDivider(
        modifier = Modifier
            .padding(bottom = 4.dp)
            .requiredWidth(1024.dp)
    )
}

@Preview
@Composable
fun HomeScreenPreview() {
    CollectionCatalogTheme {
        HomeScreenContent(samplePlates, onAddItem = {}, onItemClick = {}, onOpenDrawer = {})
    }
}
