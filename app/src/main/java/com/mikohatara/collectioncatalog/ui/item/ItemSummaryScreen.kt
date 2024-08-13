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
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.samplePlates
import com.mikohatara.collectioncatalog.ui.components.DeletionDialog
import com.mikohatara.collectioncatalog.ui.components.InspectItemImage
import com.mikohatara.collectioncatalog.ui.components.ItemImage
import com.mikohatara.collectioncatalog.ui.components.ItemScreenModifiers
import com.mikohatara.collectioncatalog.ui.components.ItemSummaryTopAppBar
import com.mikohatara.collectioncatalog.ui.components.ItemSummaryVerticalSpacer
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ItemSummaryScreen(
    viewModel: ItemSummaryViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onEdit: (Plate) -> Unit,
    onDelete: () -> Unit
) {
    val uiState: ItemSummaryUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val item: Plate = uiState.item!!

    ItemSummaryScreen(
        item,
        viewModel,
        coroutineScope,
        onBack,
        onEdit,
        onDelete
    )
}

@Composable
private fun ItemSummaryScreen(
    item: Plate,
    viewModel: ItemSummaryViewModel,
    coroutineScope: CoroutineScope,
    onBack: () -> Unit,
    onEdit: (Plate) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isInspectingImage by rememberSaveable { mutableStateOf(false) }
    var showDeletionDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = { ItemSummaryTopAppBar(
            item.uniqueDetails.regNo,
            item,
            onBack,
            onEdit,
            onDelete = { showDeletionDialog = true }
        ) },
        content = { innerPadding ->
            ItemSummaryScreenContent(
                item = item,
                onInspectImage = { isInspectingImage = true },
                modifier = modifier.padding(innerPadding)
            )
        }
    )
    if (isInspectingImage) {
        InspectItemImage(
            imagePath = item.uniqueDetails.imagePath,
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
    item: Plate,
    onInspectImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        ItemImage(item.uniqueDetails.imagePath, onInspectImage)
        ItemSummaryVerticalSpacer(true)

        ItemSummarySection(
            item = item,
            SectionType.COMMON_DETAILS,
            sectionDetails = listOf(
                item.commonDetails.country,
                item.commonDetails.region1st,
                item.commonDetails.region2nd,
                item.commonDetails.region3rd,
                item.commonDetails.type,
                item.commonDetails.periodStart,
                item.commonDetails.periodEnd,
                item.commonDetails.year
            )
        )
        ItemSummarySection(
            item = item,
            SectionType.UNIQUE_DETAILS,
            sectionDetails = listOf(
                item.uniqueDetails.notes,
                item.uniqueDetails.vehicle,
                item.uniqueDetails.date,
                item.uniqueDetails.cost,
                item.uniqueDetails.value,
                item.uniqueDetails.status,
                item.grading.isKeeper,
                item.grading.isForTrade
            )
        )
        ItemSummarySection(
            item = item,
            SectionType.PHYSICAL_ATTRIBUTES,
            sectionDetails = listOf(
                item.size.width,
                item.size.height,
                item.size.weight
            )
        )
        ItemSummarySection(
            item = item,
            SectionType.SOURCE_INFO,
            sectionDetails = listOf(
                item.source.name,
                item.source.alias,
                item.source.type,
                item.source.details,
                item.source.country
            )
        )
    }
}

@Composable
private fun ItemSummarySection(
    item: Plate,
    sectionType: SectionType,
    sectionDetails: List<Any?>
) {
    if (sectionDetails.any { it !is Boolean && it != null || it is Boolean && it != false }) {
        SummaryCard {
            when (sectionType) {
                SectionType.COMMON_DETAILS -> CommonDetailsCard(item)
                SectionType.UNIQUE_DETAILS -> UniqueDetailsCard(item)
                SectionType.PHYSICAL_ATTRIBUTES -> PhysicalAttributesCard(item)
                SectionType.SOURCE_INFO -> SourceInfoCard(item)
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
    item: Plate
) {
    val countryAndRegion = listOf(
        item.commonDetails.country,
        item.commonDetails.region1st,
        item.commonDetails.region2nd,
        item.commonDetails.region3rd
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
            item.commonDetails.country?.let {
                ItemInfoField(
                    label = stringResource(R.string.country),
                    value = it
                )
            }
            item.commonDetails.region1st?.let {
                ItemInfoField(
                    label = stringResource(R.string.region),
                    value = it
                )
            }
            item.commonDetails.region2nd?.let {
                ItemInfoField(
                    label = stringResource(R.string.area),
                    value = it
                )
            }
            item.commonDetails.region3rd?.let {
                ItemInfoField(
                    label = stringResource(R.string.area),
                    value = it
                )
            }
        }
    }
    if (item.commonDetails.type != null) {
        SummaryCardSection(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.rounded_category),
                    contentDescription = null,
                    modifier = ItemScreenModifiers.icon
                )
            }
        ) {
            item.commonDetails.type?.let {
                ItemInfoField(
                    label = stringResource(R.string.type),
                    value = it
                )
            }
        }
    }
    if (item.commonDetails.periodStart != null || item.commonDetails.year != null) { // TODO add periodEnd
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
                item.commonDetails.periodStart?.let {
                    ItemInfoField(
                        label = stringResource(R.string.period),
                        value = item.commonDetails.periodStart.toString() + " – " + item.commonDetails.periodEnd.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
                item.commonDetails.year?.let {
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
    item: Plate
) {
    if (item.uniqueDetails.notes != null || item.uniqueDetails.vehicle != null) {
        SummaryCardSection(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.rounded_note_stack),
                    contentDescription = null,
                    modifier = ItemScreenModifiers.icon
                )
            }
        ) {
            item.uniqueDetails.notes?.let {
                ItemInfoField(
                    label = stringResource(R.string.notes),
                    value = it
                )
            }
            item.uniqueDetails.vehicle?.let {
                ItemInfoField(
                    label = stringResource(R.string.vehicle),
                    value = it
                )
            }
        }
    }
    if (item.uniqueDetails.date != null) {
        SummaryCardSection(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.rounded_event),
                    contentDescription = null,
                    modifier = ItemScreenModifiers.icon
                )
            }
        ) {
            item.uniqueDetails.date?.let {
                ItemInfoField(
                    label = stringResource(R.string.date),
                    value = it
                )
            }
        }
    }
    if (item.uniqueDetails.cost != null || item.uniqueDetails.value != null) {
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
                item.uniqueDetails.cost?.let {
                    ItemInfoField(
                        label = stringResource(R.string.cost),
                        value = it.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
                item.uniqueDetails.value?.let {
                    ItemInfoField(
                        label = stringResource(R.string.value),
                        value = it.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
    if (item.uniqueDetails.status != null) {
        SummaryCardSection(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.rounded_pin_dist),
                    contentDescription = null,
                    modifier = ItemScreenModifiers.icon
                )
            }
        ) {
            item.uniqueDetails.status?.let {
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
    item: Plate
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
            item.size.width?.let {
                ItemInfoField(
                    label = stringResource(R.string.width),
                    value = it.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
            item.size.height?.let {
                ItemInfoField(
                    label = stringResource(R.string.height),
                    value = it.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
            item.size.weight?.let {
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
    item: Plate
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
        item.source.name?.let {
            ItemInfoField(
                label = "Source Name",
                value = it
            )
        }
        item.source.alias?.let {
            ItemInfoField(
                label = "Source Alias",
                value = it
            )
        }
        item.source.type?.let {
            ItemInfoField(
                label = "Source Type",
                value = it
            )
        }
        item.source.details?.let {
            ItemInfoField(
                label = "Source Details",
                value = it
            )
        }
        item.source.country?.let {
            ItemInfoField(
                label = "Source Country",
                value = it
            )
        }
    }
}

@Preview
@Composable
fun ItemSummaryScreenPreview() {
    CollectionCatalogTheme {
        ItemSummaryScreenContent(item = samplePlates[6], onInspectImage = {})
    }
}

private enum class SectionType {
    COMMON_DETAILS,
    UNIQUE_DETAILS,
    PHYSICAL_ATTRIBUTES,
    SOURCE_INFO
}
