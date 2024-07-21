package com.mikohatara.collectioncatalog.ui.stats

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.components.StatsTopAppBar
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StatsScreen(
        uiState = uiState,
        onOpenDrawer = onOpenDrawer
    )
}

@Composable
private fun StatsScreen(
    uiState: StatsUiState,
    onOpenDrawer: () -> Unit
) {
    Scaffold(
        modifier = Modifier,//.nestedScroll(),
        topBar = {
            StatsTopAppBar(
                onOpenDrawer = onOpenDrawer
            )
        },
        content = { innerPadding ->
            StatsScreenContent(
                plateCount = uiState.items.size,
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
}

@Composable
private fun StatsScreenContent(
    plateCount: Int,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            Header(plateCount)
        }
    }
}

@Composable
private fun Header(
    plateCount: Int
) {
    Text(pluralStringResource(R.plurals.statistics_intro, plateCount, plateCount))
}

@Preview
@Composable
private fun StatsScreenPreview() {
    CollectionCatalogTheme {
        StatsScreenContent(23)
    }
}
