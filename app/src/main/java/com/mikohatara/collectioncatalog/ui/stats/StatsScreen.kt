package com.mikohatara.collectioncatalog.ui.stats

import android.icu.text.NumberFormat
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.UserPreferences
import com.mikohatara.collectioncatalog.ui.components.EndOfList
import com.mikohatara.collectioncatalog.ui.components.ExpandableCard
import com.mikohatara.collectioncatalog.ui.components.StatsTopAppBar
import com.mikohatara.collectioncatalog.util.toFormattedString
import com.mikohatara.collectioncatalog.util.toPercentage
import java.util.Locale

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit
) {
    val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StatsScreen(
        viewModel = viewModel,
        userPreferences = userPreferences,
        uiState = uiState,
        onOpenDrawer = onOpenDrawer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatsScreen(
    viewModel: StatsViewModel,
    userPreferences: UserPreferences,
    uiState: StatsUiState,
    onOpenDrawer: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            StatsTopAppBar(
                onOpenDrawer = onOpenDrawer,
                scrollBehavior
            )
        },
        content = { innerPadding ->
            StatsScreenContent(
                viewModel = viewModel,
                userPreferences = userPreferences,
                uiState = uiState,
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
}

@Composable
private fun StatsScreenContent(
    viewModel: StatsViewModel,
    userPreferences: UserPreferences,
    uiState: StatsUiState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        item {
            StatsCard {
                Header(uiState.items.size)
            }
        }
        item {
            /*StatsCard {
                Table(
                    userPreferences = userPreferences,
                    label = stringResource(R.string.country),
                    columns = viewModel.getCountries(),
                    items = uiState.items
                )
            }*/
            ExpandableCard(
                label = stringResource(R.string.country),
            ) {
                Table(
                    userPreferences = userPreferences,
                    columns = viewModel.getCountries(),
                    items = uiState.items
                )
            }
        }
        item {
            EndOfList()
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
    userPreferences: UserPreferences,
    columns: Set<String>,
    items: List<Plate>
) {
    val allItems = items.size

    Column(
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        columns.forEach { column ->
            val filteredItems = items.filter { it.commonDetails.country == column }
            val quantity = filteredItems.size.toFormattedString(userPreferences.userCountry)
            val percentage = (quantity.toFloat() / allItems.toFloat())
                .toPercentage(userPreferences.userCountry)

            Row(modifier = Modifier.padding(horizontal = 4.dp)) {
                Text(
                    column,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    quantity,
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                        .weight(0.3f)
                )
                Text(
                    percentage,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                        .weight(0.4f)
                )
            }
            if (column != columns.last()) {
                HorizontalDivider(
                    color = Color(0, 0, 0, 128),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
