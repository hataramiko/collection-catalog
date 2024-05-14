package com.mikohatara.collectioncatalog.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.samplePlates
import com.mikohatara.collectioncatalog.ui.components.HomeScreenTopAppBar
import com.mikohatara.collectioncatalog.ui.components.ItemCard
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onAddItem: () -> Unit,
    onItemClick: (Plate) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        itemList = uiState.items,
        onAddItem = onAddItem,
        onItemClick = onItemClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    itemList: List<Plate>,
    onAddItem: () -> Unit,
    onItemClick: (Plate) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { HomeScreenTopAppBar(
            onAddItem = onAddItem,
            scrollBehavior = scrollBehavior
        ) },
            /*androidx.compose.material3.TopAppBar(
                title = {
                    Text(
                        "Plates", // database tableName?
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        //modifier = Modifier.padding(16.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add"
                        )
                    }
                    IconButton(onClick = {
                        Log.d("NavTest", "MoreVert Clicked")
                        //onNavTestClicked()
                    }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More actions")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },*/
        content = { innerPadding ->
            HomeBody(
                itemList = itemList,
                modifier = Modifier
                    .padding(innerPadding),
                    //.verticalScroll(rememberScrollState())
                onItemClick = onItemClick
            )
        }
    )
}

@Composable
fun HomeBody(
    itemList: List<Plate>,
    modifier: Modifier = Modifier,
    onItemClick: (Plate) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(itemList) { item ->
            ItemCard(
                item = item
            ) {
                onItemClick(item)
                Log.d("ItemCard", item.toString())
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    CollectionCatalogTheme {
        HomeScreenContent(samplePlates, onAddItem = {}, onItemClick = {})
    }
}
