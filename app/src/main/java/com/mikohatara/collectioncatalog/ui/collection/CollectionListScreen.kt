package com.mikohatara.collectioncatalog.ui.collection

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Collection
import com.mikohatara.collectioncatalog.ui.components.CollectionListTopAppBar
import com.mikohatara.collectioncatalog.ui.components.EndOfList

@Composable
fun CollectionListScreen(
    viewModel: CollectionListViewModel = hiltViewModel(),
    onAddNew: () -> Unit,
    onCollectionClick: (Collection) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectionListScreen(
        collectionList = uiState.collectionList,
        uiState = uiState,
        viewModel = viewModel,
        onAddNew = onAddNew,
        onCollectionClick = onCollectionClick,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectionListScreen(
    collectionList: List<Collection>,
    uiState: CollectionListUiState,
    viewModel: CollectionListViewModel,
    onAddNew: () -> Unit,
    onCollectionClick: (Collection) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CollectionListTopAppBar(
                title = stringResource(R.string.edit_collections),
                onBack = onBack,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        },
        content = { innerPadding ->
            CollectionListScreenContent(
                uiState = uiState,
                viewModel = viewModel,
                collectionList = collectionList,
                onAddNew = onAddNew,
                onCollectionClick = onCollectionClick,
                modifier = modifier.padding(innerPadding)
            )
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CollectionListScreenContent(
    uiState: CollectionListUiState,
    viewModel: CollectionListViewModel,
    collectionList: List<Collection>,
    onAddNew: () -> Unit,
    onCollectionClick: (Collection) -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d("collectionList", collectionList.toString())
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        stickyHeader {
            Card(
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                )
            ) {
                CollectionListItem(
                    label = stringResource(R.string.create_collection),
                    onClick = { onAddNew() },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null
                        )
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(items = collectionList, key = { it.id }) { collection ->
            CollectionListItem(
                label = collection.name,
                emoji = collection.emoji
            ) {
                onCollectionClick(collection)
            }
        }
        item {
            EndOfList()
        }
    }
}

@Composable
private fun CollectionListItem(
    label: String,
    modifier: Modifier = Modifier,
    emoji: String? = null,
    icon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    val onClickItem = remember { Modifier.clickable { onClick() } }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .then(onClickItem)
    ) {
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
        ) {
            if (icon != null) {
                icon()
            } else if (emoji != null) {
                Text(emoji)
            } else {
                Icon(
                    painter = painterResource(R.drawable.rounded_bookmark),
                    contentDescription = null
                )
            }
        }
        Text(
            text = label,
            modifier = Modifier
        )
    }
}
