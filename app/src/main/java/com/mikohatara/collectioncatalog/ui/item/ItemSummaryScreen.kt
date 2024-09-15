package com.mikohatara.collectioncatalog.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Item
import com.mikohatara.collectioncatalog.data.ItemDetails
import com.mikohatara.collectioncatalog.ui.components.DeletionDialog
import com.mikohatara.collectioncatalog.ui.components.InspectItemImage
import com.mikohatara.collectioncatalog.ui.components.ItemImage
import com.mikohatara.collectioncatalog.ui.components.ItemScreenModifiers
import com.mikohatara.collectioncatalog.ui.components.ItemSummaryTopAppBar
import com.mikohatara.collectioncatalog.ui.components.ItemSummaryVerticalSpacer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ItemSummaryScreen(
    viewModel: ItemSummaryViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onEdit: (Item) -> Unit
) {
    val uiState: ItemSummaryUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val item: Item = uiState.item ?: return
    val itemDetails: ItemDetails = uiState.itemDetails

    ItemSummaryScreen(
        item,
        itemDetails,
        viewModel,
        coroutineScope,
        onBack,
        onEdit,
    )
}

@Composable
private fun ItemSummaryScreen(
    item: Item,
    itemDetails: ItemDetails,
    viewModel: ItemSummaryViewModel,
    coroutineScope: CoroutineScope,
    onBack: () -> Unit,
    onEdit: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    var isInspectingImage by rememberSaveable { mutableStateOf(false) }
    var showDeletionDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = { ItemSummaryTopAppBar(
            itemDetails.regNo ?: "",
            item,
            onBack,
            onEdit,
            onDelete = { showDeletionDialog = true }
        ) },
        content = { innerPadding ->
            ItemSummaryScreenContent(
                itemDetails = itemDetails,
                onInspectImage = { isInspectingImage = true },
                modifier = modifier.padding(innerPadding)
            )
        }
    )
    if (isInspectingImage) {
        InspectItemImage(
            imagePath = itemDetails.imagePath,
            onBack = { isInspectingImage = false }
        )
    }
    if (showDeletionDialog) {
        DeletionDialog(
            onConfirm = {
                showDeletionDialog = false
                coroutineScope.launch {
                    viewModel.deleteItem()
                    onBack()
                }
            },
            onCancel = { showDeletionDialog = false }
        )
    }
}

@Composable
private fun ItemSummaryScreenContent(
    itemDetails: ItemDetails,
    onInspectImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        ItemImage(itemDetails.imagePath, onInspectImage)
        ItemSummaryVerticalSpacer(true)

        ItemSummarySection(
            itemDetails = itemDetails,
            SectionType.COMMON_DETAILS,
            sectionDetails = listOf(
                itemDetails.country,
                itemDetails.region1st,
                itemDetails.region2nd,
                itemDetails.region3rd,
                itemDetails.type,
                itemDetails.periodStart,
                itemDetails.periodEnd,
                itemDetails.year
            )
        )
        ItemSummarySection(
            itemDetails = itemDetails,
            SectionType.UNIQUE_DETAILS,
            sectionDetails = listOf(
                itemDetails.notes,
                itemDetails.vehicle,
                itemDetails.date,
                itemDetails.cost,
                itemDetails.value,
                itemDetails.status,
                itemDetails.isKeeper,
                itemDetails.isForTrade
            )
        )
        ItemSummarySection(
            itemDetails = itemDetails,
            SectionType.PHYSICAL_ATTRIBUTES,
            sectionDetails = listOf(
                itemDetails.width,
                itemDetails.height,
                itemDetails.weight
            )
        )
        ItemSummarySection(
            itemDetails = itemDetails,
            SectionType.SOURCE_INFO,
            sectionDetails = listOf(
                itemDetails.sourceName,
                itemDetails.sourceAlias,
                itemDetails.sourceType,
                itemDetails.sourceDetails,
                itemDetails.sourceCountry
            )
        )
        ItemSummarySection(
            itemDetails = itemDetails,
            SectionType.ARCHIVAL_INFO,
            sectionDetails = listOf(
                itemDetails.archivalDate,
                itemDetails.archivalType,
                itemDetails.archivalDetails,
                itemDetails.price,
                itemDetails.recipientName,
                itemDetails.recipientAlias,
                itemDetails.recipientCountry
            )
        )
    }
}

@Composable
private fun ItemSummarySection(
    itemDetails: ItemDetails,
    sectionType: SectionType,
    sectionDetails: List<Any?>
) {
    if (sectionDetails.any {
        it !is Boolean && it != null || it is Boolean && it != false && it != true
    }) {
        SummaryCard {
            when (sectionType) {
                SectionType.COMMON_DETAILS -> CommonDetailsCard(itemDetails)
                SectionType.UNIQUE_DETAILS -> UniqueDetailsCard(itemDetails)
                SectionType.PHYSICAL_ATTRIBUTES -> PhysicalAttributesCard(itemDetails)
                SectionType.SOURCE_INFO -> SourceInfoCard(itemDetails)
                SectionType.ARCHIVAL_INFO -> ArchivalInfoCard(itemDetails)
            }
        }
    }
}

@Composable
private fun SummaryCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
private fun SummaryCardSection(
    icon: @Composable (() -> Unit)?,
    content: @Composable () -> Unit
) {
    Row {
        icon?.invoke()
        Card(
            modifier = Modifier
                .padding(top = 8.dp, end = 16.dp, bottom = 8.dp),
            colors = CardDefaults
                .cardColors(containerColor = Color(0, 0, 0, 25))
        ) {
            content()
        }
    }
}

@Composable
private fun ItemInfoField(
    label: String?,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        label?.let {
            Text(
                label,
                color = Color(0, 0, 0, 128),
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(start = 8.dp)
            )
        }
        Text(
            value,
            modifier = Modifier
                .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 12.dp)
        )
    }
}

@Composable
private fun CommonDetailsCard(
    itemDetails: ItemDetails
) {
    val countryAndRegion = listOf(
        itemDetails.country,
        itemDetails.region1st,
        itemDetails.region2nd,
        itemDetails.region3rd
    )

    if (countryAndRegion.any { it != null }) {
        SummaryCardSection(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.rounded_globe),
                    contentDescription = null,
                    modifier = ItemScreenModifiers.icon
                )
            }
        ) {
            itemDetails.country?.let {
                ItemInfoField(
                    label = stringResource(R.string.country),
                    value = it
                )
            }
            itemDetails.region1st?.let {
                ItemInfoField(
                    label = stringResource(R.string.region),
                    value = it
                )
            }
            itemDetails.region2nd?.let {
                ItemInfoField(
                    label = stringResource(R.string.area),
                    value = it
                )
            }
            itemDetails.region3rd?.let {
                ItemInfoField(
                    label = stringResource(R.string.area),
                    value = it
                )
            }
        }
    }
    if (itemDetails.type != null) {
        SummaryCardSection(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.rounded_category),
                    contentDescription = null,
                    modifier = ItemScreenModifiers.icon
                )
            }
        ) {
            itemDetails.type?.let {
                ItemInfoField(
                    label = stringResource(R.string.type),
                    value = it
                )
            }
        }
    }
    if (itemDetails.periodStart != null || itemDetails.year != null) { // TODO add periodEnd
        SummaryCardSection(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.rounded_date_range),
                    contentDescription = null,
                    modifier = ItemScreenModifiers.icon
                )
            }
        ) {
            Row {
                itemDetails.periodStart?.let {
                    ItemInfoField(
                        label = stringResource(R.string.period),
                        value = itemDetails.periodStart.toString() + " – " + itemDetails.periodEnd.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
                itemDetails.year?.let {
                    ItemInfoField(
                        label = stringResource(R.string.year),
                        value = it.toString(),
                        modifier = Modifier.weight(0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun UniqueDetailsCard(
    itemDetails: ItemDetails
) {
    if (itemDetails.notes != null || itemDetails.vehicle != null) {
        SummaryCardSection(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.rounded_note_stack),
                    contentDescription = null,
                    modifier = ItemScreenModifiers.icon
                )
            }
        ) {
            itemDetails.notes?.let {
                ItemInfoField(
                    label = stringResource(R.string.notes),
                    value = it
                )
            }
            itemDetails.vehicle?.let {
                ItemInfoField(
                    label = stringResource(R.string.vehicle),
                    value = it
                )
            }
        }
    }
    if (itemDetails.date != null) {
        SummaryCardSection(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.rounded_event),
                    contentDescription = null,
                    modifier = ItemScreenModifiers.icon
                )
            }
        ) {
            itemDetails.date?.let {
                ItemInfoField(
                    label = stringResource(R.string.date),
                    value = it
                )
            }
        }
    }
    if (itemDetails.cost != null || itemDetails.value != null) {
        SummaryCardSection(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.rounded_payments),
                    contentDescription = null,
                    modifier = ItemScreenModifiers.icon
                )
            }
        ) {
            Row {
                itemDetails.cost?.let {
                    ItemInfoField(
                        label = stringResource(R.string.cost),
                        value = it.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
                itemDetails.value?.let {
                    ItemInfoField(
                        label = stringResource(R.string.value),
                        value = it.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
    if (itemDetails.status != null) {
        SummaryCardSection(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.rounded_pin_dist),
                    contentDescription = null,
                    modifier = ItemScreenModifiers.icon
                )
            }
        ) {
            itemDetails.status?.let {
                ItemInfoField(
                    label = stringResource(R.string.location),
                    value = it
                )
            }
        }
    }
}

@Composable
private fun PhysicalAttributesCard(
    itemDetails: ItemDetails
) {
    SummaryCardSection(
        icon = {
            Icon(
                painter = painterResource(R.drawable.rounded_ruler),
                contentDescription = null,
                modifier = ItemScreenModifiers.icon
            )
        }
    ) {
        Row {
            itemDetails.width?.let {
                ItemInfoField(
                    label = stringResource(R.string.width),
                    value = it.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
            itemDetails.height?.let {
                ItemInfoField(
                    label = stringResource(R.string.height),
                    value = it.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
            itemDetails.weight?.let {
                ItemInfoField(
                    label = stringResource(R.string.weight),
                    value = it.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SourceInfoCard(
    itemDetails: ItemDetails
) {
    SummaryCardSection(
        icon = {
            Icon(
                painter = painterResource(R.drawable.rounded_person_pin_circle),
                contentDescription = null,
                modifier = ItemScreenModifiers.icon
            )
        }
    ) {
        itemDetails.sourceName?.let {
            ItemInfoField(
                label = "Source Name",
                value = it
            )
        }
        itemDetails.sourceAlias?.let {
            ItemInfoField(
                label = "Source Alias",
                value = it
            )
        }
        itemDetails.sourceType?.let {
            ItemInfoField(
                label = "Source Type",
                value = it
            )
        }
        itemDetails.sourceDetails?.let {
            ItemInfoField(
                label = "Source Details",
                value = it
            )
        }
        itemDetails.sourceCountry?.let {
            ItemInfoField(
                label = "Source Country",
                value = it
            )
        }
    }
}

@Composable
private fun ArchivalInfoCard(
    itemDetails: ItemDetails
) {
    SummaryCardSection(
        icon = {
            Icon(
                painter = painterResource(R.drawable.rounded_history),
                contentDescription = null,
                modifier = ItemScreenModifiers.icon
            )
        }
    ) {
        itemDetails.archivalDate?.let {
            ItemInfoField(
                label = "Archival Date",
                value = it
            )
        }
        itemDetails.archivalType?.let {
            ItemInfoField(
                label = "Archival Reason",
                value = it
            )
        }
        itemDetails.archivalDetails?.let {
            ItemInfoField(
                label = "Archival Details",
                value = it
            )
        }
        itemDetails.price?.let {
            ItemInfoField(
                label = "Price",
                value = it.toString()
            )
        }
        itemDetails.recipientName?.let {
            ItemInfoField(
                label = "Recipient Name",
                value = it
            )
        }
        itemDetails.recipientAlias?.let {
            ItemInfoField(
                label = "Recipient Alias",
                value = it
            )
        }
        itemDetails.recipientCountry?.let {
            ItemInfoField(
                label = "Recipient Country",
                value = it
            )
        }
    }
}

@Preview
@Composable
fun ItemSummaryScreenPreview() {
    /*CollectionCatalogTheme {
        ItemSummaryScreenContent(itemDetails = samplePlates[6], onInspectImage = {})
    }*/
}

private enum class SectionType {
    COMMON_DETAILS,
    UNIQUE_DETAILS,
    PHYSICAL_ATTRIBUTES,
    SOURCE_INFO,
    ARCHIVAL_INFO
}
