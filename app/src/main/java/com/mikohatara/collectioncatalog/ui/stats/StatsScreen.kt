package com.mikohatara.collectioncatalog.ui.stats

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Collection
import com.mikohatara.collectioncatalog.data.Item
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.data.UserPreferences
import com.mikohatara.collectioncatalog.ui.components.EndOfList
import com.mikohatara.collectioncatalog.ui.components.ExpandableCard
import com.mikohatara.collectioncatalog.ui.components.IconCollectionLabel
import com.mikohatara.collectioncatalog.ui.components.SelectCollectionBottomSheet
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
    var showCollectionBottomSheet by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            StatsTopAppBar(
                onOpenDrawer = onOpenDrawer,
                scrollBehavior = scrollBehavior
            )
        },
        content = { innerPadding ->
            StatsScreenContent(
                viewModel = viewModel,
                userPreferences = userPreferences,
                uiState = uiState,
                onShowCollectionBottomSheet = { showCollectionBottomSheet = true },
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
    if (showCollectionBottomSheet) {
        SelectCollectionBottomSheet(
            collections = viewModel.getCollections(),
            selectedCollection = uiState.collection?.name ?: "",
            onSelect = {
                viewModel.setCollection(it)
                showCollectionBottomSheet = false
            },
            onDismiss = { showCollectionBottomSheet = false }
        )
    }
}

@Composable
private fun StatsScreenContent(
    viewModel: StatsViewModel,
    userPreferences: UserPreferences,
    uiState: StatsUiState,
    onShowCollectionBottomSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            StatsHeaderCard(
                isSelected = uiState.activeItemType == ItemType.PLATE && uiState.collection == null,
                message = stringResource(R.string.all_plates),
                amount = uiState.allPlates.size.toFormattedString(userPreferences.userCountry),
                painter = painterResource(R.drawable.rounded_newsstand),
                fontSize = 18.sp,
                onClick = {
                    viewModel.setActiveItemType(ItemType.PLATE)
                    viewModel.clearCollection()
                }
            )
            CollectionCard(
                collection = uiState.collection,
                collectionSize = uiState.collectionPlates.size,
                allPlatesSize = uiState.allPlates.size,
                userCountry = userPreferences.userCountry,
                /*amount = uiState.collectionPlates.size
                    .toFormattedString(userPreferences.userCountry),*/
                onClick = { onShowCollectionBottomSheet() }
            )
            Row(modifier = Modifier.padding(bottom = 32.dp)) {
                StatsHeaderCard(
                    isSelected = uiState.activeItemType == ItemType.WANTED_PLATE,
                    message = stringResource(R.string.wishlist),
                    amount = uiState.wishlist.size.toFormattedString(userPreferences.userCountry),
                    painter = painterResource(R.drawable.rounded_heart),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.setActiveItemType(ItemType.WANTED_PLATE)
                        viewModel.clearCollection()
                    }
                )
                Spacer(modifier = Modifier.width(12.dp))
                StatsHeaderCard(
                    isSelected = uiState.activeItemType == ItemType.FORMER_PLATE,
                    message = stringResource(R.string.archive),
                    amount = uiState.archive.size.toFormattedString(userPreferences.userCountry),
                    painter = painterResource(R.drawable.rounded_archive),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.setActiveItemType(ItemType.FORMER_PLATE)
                        viewModel.clearCollection()
                    }
                )
            }
        }
        item {
            Table(
                userPreferences = userPreferences,
                label = stringResource(R.string.countries),
                rows = viewModel.getCountries(),
                items = viewModel.getActiveItems(),
                propertyExtractor = viewModel.getPropertyExtractor("country"),
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }
        item {
            Table(
                userPreferences = userPreferences,
                label = stringResource(R.string.types),
                rows = viewModel.getTypes(),
                items = viewModel.getActiveItems(),
                propertyExtractor = viewModel.getPropertyExtractor("type")
            )
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
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    onClick: () -> Unit
) {
    val onClick = remember { Modifier.clickable { onClick() } }
    val cardColor = if (isSelected) {
        MaterialTheme.colorScheme.surfaceContainerHighest
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }
    val iconColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }
    val textColor = if (isSelected) {
        LocalContentColor.current
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        // A Box to hold the onClick instead of the Card for better visualization
        Box(modifier = Modifier.fillMaxSize().then(onClick)) {
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
                        tint = iconColor,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = message,
                        color = textColor,
                        fontSize = fontSize,
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = amount,
                    color = textColor,
                    fontSize = fontSize * 1.5,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }

    }
}

@Composable
private fun CollectionCard(
    collection: Collection? = null,
    collectionSize: Int,
    allPlatesSize: Int,
    userCountry: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    onClick: () -> Unit
) {
    val onClick = remember { Modifier.clickable { onClick() } }
    val cardColor = if (collection != null) {
        MaterialTheme.colorScheme.surfaceContainerHighest
    } else {
        MaterialTheme.colorScheme.surfaceContainerLowest
    }
    val iconColor = if (collection != null) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }
    val textColor = if (collection != null) {
        LocalContentColor.current
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = modifier
            .animateContentSize()
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        // A Box to hold the onClick instead of the Card for better visualization
        Box(modifier = Modifier.fillMaxSize().then(onClick)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.offset(x = (-2).dp)
                ) {
                    if (collection != null) {
                        if (collection.emoji != null) {
                            Text(
                                text = collection.emoji,
                                fontSize = fontSize,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        } else {
                            IconCollectionLabel(
                                color = collection.color.color,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.rounded_new_label),
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Text(
                        text = collection?.name ?: stringResource(R.string.select_collection),
                        color = textColor,
                        fontSize = fontSize,
                        textAlign = TextAlign.Center
                    )
                }
                if (collection != null) {
                    Text(
                        text = collectionSize.toFormattedString(userCountry),
                        color = textColor,
                        fontSize = fontSize * 1.5,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    /*if (collectionSize > 0) {
                        val percentage = (collectionSize.toFloat() / allPlatesSize.toFloat())
                        val percentageString = percentage.toPercentage(userCountry)

                        Text(
                            text = stringResource(R.string.percentage_of_plates, percentageString),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = fontSize * 0.75,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }*/
                }
            }
        }

    }
}

@Composable
private fun Table(
    userPreferences: UserPreferences,
    label: String,
    rows: Set<String>,
    items: List<Item>,
    propertyExtractor: (Item) -> String,
    modifier: Modifier = Modifier
) {
    val rowCount = rows.size
    val allItems = items.size
    val dividerColor = MaterialTheme.colorScheme.surface

    ExpandableCard(
        label = label,
        modifier = modifier
    ) {
        Column {
            Row(modifier = Modifier.padding(start = 16.dp, bottom = 8.dp, end = 16.dp)) {
                Text(
                    text = stringResource(R.string.total_with_punctuation),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$rowCount",
                    //fontWeight = FontWeight.Bold
                )
            }
            if (rows.isNotEmpty()) {
                HorizontalDivider(
                    color = dividerColor,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else Spacer(modifier = Modifier.height(8.dp))
        }
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(0.dp))
            rows.forEach { row ->
                val filteredItems = items.filter { propertyExtractor(it) == row }
                val quantity = filteredItems.size.toFormattedString(userPreferences.userCountry)
                val percentage = (quantity.toFloat() / allItems.toFloat())
                    .toPercentage(userPreferences.userCountry)

                Row(modifier = Modifier.padding(/*horizontal = */4.dp)) {
                    Text(
                        row,
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
                if (row != rows.last()) {
                    HorizontalDivider(
                        color = dividerColor,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
