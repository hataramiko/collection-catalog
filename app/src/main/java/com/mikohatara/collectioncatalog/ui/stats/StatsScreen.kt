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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
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
import com.mikohatara.collectioncatalog.data.CollectionColor
import com.mikohatara.collectioncatalog.data.Item
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.data.UserPreferences
import com.mikohatara.collectioncatalog.ui.components.EndOfList
import com.mikohatara.collectioncatalog.ui.components.ExpandableStatsCard
import com.mikohatara.collectioncatalog.ui.components.IconCollectionLabel
import com.mikohatara.collectioncatalog.ui.components.InfoDialog
import com.mikohatara.collectioncatalog.ui.components.Loading
import com.mikohatara.collectioncatalog.ui.components.SelectCollectionBottomSheet
import com.mikohatara.collectioncatalog.ui.components.StatsTopAppBar
import com.mikohatara.collectioncatalog.util.toFormattedString
import com.mikohatara.collectioncatalog.util.toPercentage

// Temporary solution to manage card shapes within StatsScreen
// TODO implement unified app-wide approach in theme etc.
private object RoundedCorners {
    val AllRound = RoundedCornerShape(20.dp)

    val AllSharp = RoundedCornerShape(8.dp)

    val BottomSharp = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = 8.dp,
        bottomEnd = 8.dp
    )

    val TopSharp = RoundedCornerShape(
        topStart = 8.dp,
        topEnd = 8.dp,
        bottomStart = 20.dp,
        bottomEnd = 20.dp
    )
}

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
    val propertyExtractorCountry = remember(uiState.activeItemType) { viewModel
        .getPropertyExtractor("country") }
    val propertyExtractorType = remember(uiState.activeItemType) { viewModel
        .getPropertyExtractor("type") }
    val propertyExtractorStartDateYear = remember(uiState.activeItemType) { viewModel
        .getPropertyExtractor("startDateYear") }
    val propertyExtractorLocation = remember(uiState.activeItemType) { viewModel
        .getPropertyExtractor("location") }
    val propertyExtractorSourceType = remember(uiState.activeItemType) { viewModel
        .getPropertyExtractor("sourceType") }
    val propertyExtractorSourceCountry = remember(uiState.activeItemType) { viewModel
        .getPropertyExtractor("sourceCountry") }
    val propertyExtractorEndDateYear = remember(uiState.activeItemType) { viewModel
        .getPropertyExtractor("endDateYear") }
    val propertyExtractorArchivalReason = remember(uiState.activeItemType) { viewModel
        .getPropertyExtractor("archivalReason") }
    val propertyExtractorRecipientCountry = remember(uiState.activeItemType) { viewModel
        .getPropertyExtractor("recipientCountry") }

    if (uiState.isLoading) {
        Loading()
    } else {
        LazyColumn(modifier = modifier.padding(horizontal = 16.dp)) {
            item {
                StatsHeaderCard(
                    isSelected = uiState.activeItemType ==
                        ItemType.PLATE && uiState.collection == null,
                    message = stringResource(R.string.all_plates),
                    amount = uiState.allPlates.size
                        .toFormattedString(userPreferences.userCountry),
                    painter = painterResource(R.drawable.rounded_newsstand),
                    modifier = Modifier.padding(top = 20.dp),
                    fontSize = 18.sp,
                    onClick = {
                        viewModel.setActiveItemType(ItemType.PLATE)
                        viewModel.clearCollection()
                    }
                )
                CollectionCard(
                    collection = uiState.collection,
                    collectionSize = uiState.collectionPlates.size
                        .toFormattedString(userPreferences.userCountry),
                    percentageOfAllPlates = uiState.collectionPercentage
                        .toPercentage(userPreferences.userCountry),
                    onClick = { onShowCollectionBottomSheet() }
                )
                Row(modifier = Modifier.padding(bottom = 40.dp)) {
                    StatsHeaderCard(
                        isSelected = uiState.activeItemType == ItemType.WANTED_PLATE,
                        message = stringResource(R.string.wishlist),
                        amount = uiState.wishlist.size
                            .toFormattedString(userPreferences.userCountry),
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
                        amount = uiState.archive.size
                            .toFormattedString(userPreferences.userCountry),
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
                ExpandableStatsCard(
                    label = stringResource(R.string.countries),
                    shape = RoundedCorners.BottomSharp,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    SumRow(uiState.countries.size)
                    Table(
                        userPreferences = userPreferences,
                        rows = uiState.countries,
                        items = uiState.activeItems,
                        propertyExtractor = propertyExtractorCountry
                    )
                }
            }
            item {
                ExpandableStatsCard(
                    label = stringResource(R.string.types),
                    shape = RoundedCorners.AllSharp,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    SumRow(uiState.types.size)
                    Table(
                        userPreferences = userPreferences,
                        rows = uiState.types,
                        items = uiState.activeItems,
                        propertyExtractor = propertyExtractorType
                    )
                }
            }
            item {
                ExpandableStatsCard(
                    label = stringResource(R.string.period),
                    shape = RoundedCorners.AllSharp,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) { /*TODO*/ }
            }
            item {
                ExpandableStatsCard(
                    label = stringResource(R.string.year),
                    shape = RoundedCorners.TopSharp,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) { /*TODO*/ }
            }
            if (uiState.activeItemType != ItemType.WANTED_PLATE) {
                val (costShape, costPadding) = if (uiState.activeItemType == ItemType.PLATE) {
                    RoundedCorners.AllSharp to 4.dp
                } else RoundedCorners.TopSharp to 16.dp

                item {
                    ExpandableStatsCard(
                        label = stringResource(R.string.date),
                        shape = RoundedCorners.BottomSharp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Table(
                            userPreferences = userPreferences,
                            rows = uiState.startDateYears,
                            items = uiState.activeItems,
                            propertyExtractor = propertyExtractorStartDateYear
                        )
                    }
                }
                item {
                    ExpandableStatsCard(
                        label = stringResource(R.string.cost),
                        shape = costShape,
                        modifier = Modifier.padding(bottom = costPadding)
                    ) {
                        CostCardContent(uiState)
                    }
                }
                if (uiState.activeItemType == ItemType.PLATE) {
                    item {
                        ExpandableStatsCard(
                            label = stringResource(R.string.value),
                            shape = RoundedCorners.AllSharp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            ValueCardContent(uiState)
                        }
                    }
                    item {
                        ExpandableStatsCard(
                            label = stringResource(R.string.location),
                            shape = RoundedCorners.TopSharp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Table(
                                userPreferences = userPreferences,
                                rows = uiState.locations,
                                items = uiState.activeItems,
                                propertyExtractor = propertyExtractorLocation
                            )
                        }
                    }
                }
                item { Subheader(stringResource(R.string.source)) }
                item {
                    ExpandableStatsCard(
                        label = stringResource(R.string.source_type),
                        shape = RoundedCorners.BottomSharp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        SourceTypeContent(
                            uiState,
                            userPreferences,
                            propertyExtractorSourceType
                        )
                    }
                }
                item {
                    ExpandableStatsCard(
                        label = stringResource(R.string.source_country),
                        shape = RoundedCorners.TopSharp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        SourceCountryContent(
                            uiState,
                            userPreferences,
                            propertyExtractorSourceCountry
                        )
                    }
                }
            }
            if (uiState.activeItemType == ItemType.FORMER_PLATE) {
                item { Subheader(stringResource(R.string.archival)) }
                item {
                    ExpandableStatsCard(
                        label = stringResource(R.string.archival_date),
                        shape = RoundedCorners.BottomSharp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Table(
                            userPreferences = userPreferences,
                            rows = uiState.endDateYears,
                            items = uiState.activeItems,
                            propertyExtractor = propertyExtractorEndDateYear
                        )
                    }
                }
                item {
                    ExpandableStatsCard(
                        label = stringResource(R.string.archival_reason),
                        shape = RoundedCorners.AllSharp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        ArchivalReasonContent(
                            uiState,
                            userPreferences,
                            propertyExtractorArchivalReason
                        )
                    }
                }
                item {
                    ExpandableStatsCard(
                        label = stringResource(R.string.sold_price),
                        shape = RoundedCorners.AllSharp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        ArchivalPriceContent(uiState)
                    }
                }
                item {
                    ExpandableStatsCard(
                        label = stringResource(R.string.recipient_country),
                        shape = RoundedCorners.TopSharp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        RecipientCountryContent(
                            uiState,
                            userPreferences,
                            propertyExtractorRecipientCountry
                        )
                    }
                }
            }
            item { EndOfList() }
        }
    }
}

@Composable
private fun StatsHeaderCard(
    message: String,
    amount: String,
    painter: Painter,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    fontSize: TextUnit = 16.sp,
    onClick: () -> Unit
) {
    val onClick = remember { Modifier.clickable { onClick() } }
    val cardColor = if (isSelected) {
        colorScheme.surfaceContainerHighest
    } else {
        colorScheme.surfaceContainer
    }
    val iconColor = if (isSelected) {
        colorScheme.primary
    } else {
        colorScheme.outline
    }
    val textColor = if (isSelected) {
        LocalContentColor.current
    } else {
        colorScheme.onSurfaceVariant
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
                    modifier = Modifier.offset(x = (-2).dp)//.padding(horizontal = 8.dp)
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
    modifier: Modifier = Modifier,
    collection: Collection? = null,
    collectionSize: String,
    percentageOfAllPlates: String,
    fontSize: TextUnit = 16.sp,
    onClick: () -> Unit
) {
    val onClick = remember { Modifier.clickable { onClick() } }
    val cardColor = if (collection != null) {
        colorScheme.surfaceContainerHighest
    } else {
        colorScheme.surfaceContainerLowest
    }
    val iconColor = if (collection != null) {
        colorScheme.primary
    } else {
        colorScheme.outline
    }
    val textColor = if (collection != null) {
        LocalContentColor.current
    } else {
        colorScheme.onSurfaceVariant
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
                    modifier = Modifier.offset(x = (-2).dp).padding(horizontal = 8.dp)
                ) {
                    if (collection != null) {
                        if (collection.emoji != null) {
                            Text(
                                text = collection.emoji,
                                fontSize = fontSize,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        } else {
                            val collectionColor = collection.color.color
                            if (collectionColor != CollectionColor.DEFAULT.color) {
                                IconCollectionLabel(
                                    color = collectionColor,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            } else {
                                // As iconColor will not equal CollectionColor.DEFAULT.color,
                                // this is a workaround to avoid the hue in the middle.
                                // TODO improve either this or the IconCollectionLabel
                                Icon(
                                    painter = painterResource(R.drawable.rounded_label),
                                    contentDescription = null,
                                    tint = iconColor,
                                    modifier = Modifier.offset(x = 1.dp).padding(end = 8.dp)
                                )
                            }
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
                        text = collectionSize,
                        color = textColor,
                        fontSize = fontSize * 1.5,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    Text(
                        text = stringResource(
                            R.string.percentage_of_plates,
                            percentageOfAllPlates,
                            percentageOfAllPlates
                        ),
                        color = colorScheme.onSurfaceVariant,
                        fontSize = fontSize * 0.8,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

    }
}

@Composable
private fun CostCardContent(uiState: StatsUiState) {
    val showInfo = rememberSaveable { mutableStateOf(false) }

    Spacer(modifier = Modifier.height(4.dp))
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = stringResource(R.string.total) +
                    stringResource(R.string.punctuation_colon),
                //color = colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = uiState.selectionCost,
                //color = colorScheme.onSurfaceVariant,
            )
        }
        Row(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = stringResource(R.string.cost_per_plate) +
                    stringResource(R.string.punctuation_colon),
                //color = colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = uiState.selectionCostPerPlate,
                //color = colorScheme.onSurfaceVariant,
            )
        }
        HorizontalDivider(
            color = colorScheme.surface,
            modifier = Modifier.padding(vertical = 20.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.cost_gross), modifier = Modifier.padding(bottom = 16.dp))
            Box(modifier = Modifier.size(32.dp).offset(x = 0.dp, y = (-6).dp)) {
                IconButton(onClick = { showInfo.value = true }) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_info),
                        contentDescription = null,
                        tint = colorScheme.outline,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        Row(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = stringResource(R.string.total) +
                    stringResource(R.string.punctuation_colon),
                //color = colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = uiState.combinedCostGross,
                //color = colorScheme.onSurfaceVariant,
            )
        }
        Row(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = stringResource(R.string.cost_per_plate) +
                    stringResource(R.string.punctuation_colon),
                //color = colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = uiState.combinedCostGrossPerPlate,
                //color = colorScheme.onSurfaceVariant,
            )
        }
        Text(stringResource(R.string.cost_net), modifier = Modifier.padding(vertical = 16.dp))
        Row(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = stringResource(R.string.total) +
                    stringResource(R.string.punctuation_colon),
                //color = colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = uiState.combinedCostNet,
                //color = colorScheme.onSurfaceVariant,
            )
        }
        Row(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = stringResource(R.string.cost_per_plate) +
                    stringResource(R.string.punctuation_colon),
                //color = colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = uiState.combinedCostNetPerPlate,
                //color = colorScheme.onSurfaceVariant,
            )
        }
    }
    Spacer(modifier = Modifier.height(20.dp))

    if (showInfo.value) {
        InfoDialog(
            onDismissRequest = { showInfo.value = false },
            text = stringResource(R.string.info_stats_cost)
        )
    }
}

@Composable
private fun ValueCardContent(uiState: StatsUiState) {
    Spacer(modifier = Modifier.height(4.dp))
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = stringResource(R.string.total) +
                        stringResource(R.string.punctuation_colon),
                //color = colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = uiState.selectionValue,
                //color = colorScheme.onSurfaceVariant,
            )
        }
        Row(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = stringResource(R.string.cost_per_plate) +
                        stringResource(R.string.punctuation_colon),
                //color = colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = uiState.selectionValuePerPlate,
                //color = colorScheme.onSurfaceVariant,
            )
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
private fun SourceTypeContent(
    uiState: StatsUiState,
    userPreferences: UserPreferences,
    propertyExtractorSourceType: (Item) -> String?
) {
    Table(
        userPreferences = userPreferences,
        rows = uiState.sourceTypes,
        items = uiState.activeItems,
        propertyExtractor = propertyExtractorSourceType
    )
}

@Composable
private fun SourceCountryContent(
    uiState: StatsUiState,
    userPreferences: UserPreferences,
    propertyExtractorSourceCountry: (Item) -> String?
) {
    Table(
        userPreferences = userPreferences,
        rows = uiState.sourceCountries,
        items = uiState.activeItems,
        propertyExtractor = propertyExtractorSourceCountry
    )
}

@Composable
private fun ArchivalReasonContent(
    uiState: StatsUiState,
    userPreferences: UserPreferences,
    propertyExtractorArchivalReason: (Item) -> String?
) {
    Table(
        userPreferences = userPreferences,
        rows = uiState.archivalReasons,
        items = uiState.activeItems,
        propertyExtractor = propertyExtractorArchivalReason
    )
}

@Composable
private fun ArchivalPriceContent(
    uiState: StatsUiState
) {
    Text(
        text = uiState.archivePriceSum,
        modifier = Modifier.padding(start = 20.dp, end = 16.dp, bottom = 16.dp)
    )
}

@Composable
private fun RecipientCountryContent(
    uiState: StatsUiState,
    userPreferences: UserPreferences,
    propertyExtractorRecipientCountry: (Item) -> String?
) {
    Table(
        userPreferences = userPreferences,
        rows = uiState.recipientCountries,
        items = uiState.activeItems,
        propertyExtractor = propertyExtractorRecipientCountry
    )
}

@Composable
private fun Subheader(text: String) {
    Text(
        text = text,
        color = colorScheme.secondary,
        style = typography.titleSmall,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun Table(
    userPreferences: UserPreferences,
    rows: Set<String?>,
    items: List<Item>,
    propertyExtractor: (Item) -> String?,
    modifier: Modifier = Modifier
) {
    val allItems = items.size
    val dividerColor = colorScheme.surface

    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(0.dp))
        rows.forEach { row ->
            val filteredItems = items.filter { propertyExtractor(it) == row }
            val quantity = filteredItems.size
                .toFormattedString(userPreferences.userCountry)
            val percentage = (quantity.toFloat() / allItems.toFloat())
                .toPercentage(userPreferences.userCountry)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = row.takeIf { !it.isNullOrEmpty() } ?:
                        stringResource(R.string.not_applicable),
                    color = colorScheme.secondary,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                Text(
                    text = quantity,
                    color = colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.3f).padding(end = 8.dp)
                )
                Text(
                    text = percentage,
                    color = colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(0.5f)
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

@Composable
private fun SumRow(sum: Int) {
    Column {
        Row(modifier = Modifier.padding(start = 16.dp, bottom = 16.dp, end = 16.dp)) {
            Text(
                text = stringResource(R.string.total) + stringResource(R.string.punctuation_colon),
                //color = colorScheme.outline
            )
            Text(
                text = "$sum",
                //color = colorScheme.outline
            )
        }
    }
}
