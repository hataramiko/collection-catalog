package com.mikohatara.collectioncatalog.ui.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.ui.components.StatsTopAppBar
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme
import java.util.Locale

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StatsScreen(
        uiState = uiState,
        viewModel = viewModel,
        onOpenDrawer = onOpenDrawer
    )
}

@Composable
private fun StatsScreen(
    uiState: StatsUiState,
    viewModel: StatsViewModel,
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
                uiState = uiState,
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
}

@Composable
private fun StatsScreenContent(
    uiState: StatsUiState,
    viewModel: StatsViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            StatsCard {
                Header(uiState.items.size)
            }
        }
        item {
            StatsCard {
                Table(
                    label = stringResource(R.string.country),
                    columns = viewModel.getCountries(),
                    items = uiState.items
                )
            }
        }
    }
}

@Composable
private fun StatsCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        content()
    }
}

@Composable
private fun Header(
    plateCount: Int
) {
    Text(
        pluralStringResource(R.plurals.statistics_intro, plateCount, plateCount),
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun Table(
    label: String,
    columns: Set<String>,
    items: List<Plate>
) {
    val allItems = items.size

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            label,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )
        HorizontalDivider(
            color = Color(0, 0, 0, 128),
            modifier = Modifier.padding(vertical = 4.dp)
        )
        columns.forEach { column ->
            val filteredItems = items.filter { it.commonDetails.country == column }
            val quantity = filteredItems.size
            val percentage = String.format(
                Locale.getDefault(),
                "%.2f",
                quantity.toFloat() / allItems.toFloat() * 100.0
            )

            Row {
                Text(
                    column,
                    modifier = Modifier.weight(1f)
                )
                VerticalDivider()
                Text(
                    quantity.toString(),
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                        .weight(0.3f)
                )
                VerticalDivider()
                Text(
                    "$percentage%",
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                        .weight(0.5f)
                )
            }
            HorizontalDivider(
                color = Color(0, 0, 0, 128),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Preview
@Composable
private fun StatsScreenPreview() {
    CollectionCatalogTheme {
        //StatsScreenContent()
    }
}
