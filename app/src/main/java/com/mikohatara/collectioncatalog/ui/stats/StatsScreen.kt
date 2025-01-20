package com.mikohatara.collectioncatalog.ui.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            StatsHeaderCard(
                message = stringResource(R.string.all_plates),
                amount = uiState.plates.size.toFormattedString(userPreferences.userCountry),
                painter = painterResource(R.drawable.rounded_newsstand),
                fontSize = 18.sp
            )
            Row(modifier = Modifier.padding(bottom = 32.dp)) {
                StatsHeaderCard(
                    message = stringResource(R.string.wishlist),
                    amount = uiState.wishlist.size.toFormattedString(userPreferences.userCountry),
                    painter = painterResource(R.drawable.rounded_heart),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                StatsHeaderCard(
                    message = stringResource(R.string.archive),
                    amount = uiState.archive.size.toFormattedString(userPreferences.userCountry),
                    painter = painterResource(R.drawable.rounded_archive),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            ExpandableCard(
                label = stringResource(R.string.country),
            ) {
                Table(
                    userPreferences = userPreferences,
                    columns = viewModel.getCountries(),
                    items = uiState.plates
                )
            }
        }
        item {
            EndOfList()
        }
    }
}

@Composable
private fun StatsHeaderCard(
    message: String,
    amount: String,
    painter: Painter,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.offset(x = (-2).dp)
            ) {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = message,
                    fontSize = fontSize,
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = amount,
                fontSize = fontSize * 1.5,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
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

            Row(modifier = Modifier.padding(/*horizontal = */4.dp)) {
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
