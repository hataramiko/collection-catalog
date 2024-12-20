package com.mikohatara.collectioncatalog.ui.item

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Collection
import com.mikohatara.collectioncatalog.data.Item
import com.mikohatara.collectioncatalog.data.ItemDetails
import com.mikohatara.collectioncatalog.data.UserPreferences
import com.mikohatara.collectioncatalog.ui.components.CopyItemDetailsDialog
import com.mikohatara.collectioncatalog.ui.components.DeletionDialog
import com.mikohatara.collectioncatalog.ui.components.InspectItemImage
import com.mikohatara.collectioncatalog.ui.components.ItemImage
import com.mikohatara.collectioncatalog.ui.components.ItemSummaryTopAppBar
import com.mikohatara.collectioncatalog.util.toCurrencyString
import com.mikohatara.collectioncatalog.util.toLengthString
import com.mikohatara.collectioncatalog.util.toWeightString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ItemSummaryScreen(
    viewModel: ItemSummaryViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onEdit: (Item) -> Unit
) {
    val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val uiState: ItemSummaryUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val item: Item = uiState.item ?: return
    val itemDetails: ItemDetails = uiState.itemDetails
    val collections: List<Collection> = uiState.collections

    ItemSummaryScreen(
        viewModel,
        userPreferences,
        uiState,
        item,
        itemDetails,
        collections,
        coroutineScope,
        onBack,
        onEdit,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemSummaryScreen(
    viewModel: ItemSummaryViewModel,
    userPreferences: UserPreferences,
    uiState: ItemSummaryUiState,
    item: Item,
    itemDetails: ItemDetails,
    collections: List<Collection>,
    coroutineScope: CoroutineScope,
    onBack: () -> Unit,
    onEdit: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults
        .exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    var isInspectingImage by rememberSaveable { mutableStateOf(false) }
    var showDeletionDialog by rememberSaveable { mutableStateOf(false) }
    var showCopyDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ItemSummaryTopAppBar(
                title = itemDetails.regNo ?: "",
                item = item,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
                scrollBehavior = scrollBehavior,
                onBack = onBack,
                onEdit = onEdit,
                onDelete = { showDeletionDialog = true },
                onCopy = {
                    //viewModel.copyItemDetailsToClipboard(context, uiState.itemDetails)
                    showCopyDialog = true
                }
            )
        },
        content = { innerPadding ->
            ItemSummaryScreenContent(
                userPreferences = userPreferences,
                itemDetails = itemDetails,
                collections = collections,
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
    if (showCopyDialog) {
        CopyItemDetailsDialog(
            itemDetails = itemDetails,
            onConfirm = {
                showCopyDialog = false
            },
            onCancel = { showCopyDialog = false }
        )
    }
}

@Composable
private fun ItemSummaryScreenContent(
    userPreferences: UserPreferences,
    itemDetails: ItemDetails,
    onInspectImage: () -> Unit,
    modifier: Modifier = Modifier,
    collections: List<Collection> = emptyList()
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        CommonDetailsCard(
            itemDetails = itemDetails,
            image = {
                ItemImage(
                    onClick = onInspectImage,
                    imagePath = itemDetails.imagePath
                )
            }
        )
        if (collections.isNotEmpty()) {
            Collections(collections = collections)
        }
        UniqueDetailsCard(
            itemDetails = itemDetails,
            localeCode = userPreferences.userCountry
        )
        PhysicalAttributesCard(
            itemDetails = itemDetails
        )
        SourceInfoCard(
            itemDetails = itemDetails
        )
        ArchivalInfoCard(
            itemDetails = itemDetails
        )
    }
}

@Composable
private fun Collections(
    collections: List<Collection>
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        collections.forEach {
            AssistChip(
                onClick = {},
                label = { Text(it.name) },
                leadingIcon = {
                    if (!it.emoji.isNullOrBlank()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Text(it.emoji)
                        }
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.rounded_bookmark),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun DataFieldCard(
    label: String,
    value: String = "",
    modifier: Modifier = Modifier,
    isSingleLine: Boolean = true,
    hasContainer: Boolean = true
) {
    val colors = if (hasContainer) {
        CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer)
    } else {
        CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHighest)
    }

    Card(
        colors = colors,
        modifier = modifier.fillMaxWidth()
    ) {
        if (isSingleLine) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = value,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = value,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

/*@Composable
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
}*/

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
private fun CommonDetailsCard(
    itemDetails: ItemDetails,
    image: @Composable () -> Unit
) {
    val year: String? = if (itemDetails.year != null) itemDetails.year.toString() else null
    val period: String? = if (itemDetails.periodStart != null || itemDetails.periodEnd != null) {
        "${itemDetails.periodStart ?: ""} – ${itemDetails.periodEnd ?: ""}"
    } else {
        null
    }

    Card(
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 24.dp,
            bottomEnd = 24.dp
        ),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        image.invoke()
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            itemDetails.country?.let {
                Text(
                    text = it
                )
            }
            itemDetails.region1st?.let {
                Text(
                    text = it
                )
            }
            itemDetails.region2nd?.let {
                Text(
                    text = it
                )
            }
            itemDetails.region3rd?.let {
                Text(
                    text = it
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(

            ) {
                itemDetails.type?.let {
                    Text(
                        text = it
                    )
                }
                period?.let {
                    Text(
                        text = "・$it"
                    )
                }
            }
            itemDetails.year?.let {
                Text(
                    text = it.toString()
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun UniqueDetailsCard(
    itemDetails: ItemDetails,
    localeCode: String
) {
    val modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)

    itemDetails.notes?.let {
        DataFieldCard(
            label = stringResource(R.string.notes),
            value = it,
            modifier = modifier,
            isSingleLine = false
        )
    }
    itemDetails.vehicle?.let {
        DataFieldCard(
            label = stringResource(R.string.vehicle),
            value = it,
            modifier = modifier,
            isSingleLine = false
        )
    }
    itemDetails.date?.let {
        DataFieldCard(
            label = stringResource(R.string.date),
            value = it,
            modifier = modifier
        )
    }
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        itemDetails.cost?.let {
            DataFieldCard(
                label = stringResource(R.string.cost),
                value = it.toCurrencyString(localeCode),
                modifier = Modifier.weight(1f),
                isSingleLine = itemDetails.value == null
            )
        }
        if (itemDetails.cost != null && itemDetails.value != null) {
            Spacer(modifier = Modifier.width(16.dp))
        }
        itemDetails.value?.let {
            DataFieldCard(
                label = stringResource(R.string.value),
                value = it.toCurrencyString(localeCode),
                modifier = Modifier.weight(1f),
                isSingleLine = itemDetails.cost == null
            )
        }
    }
    itemDetails.status?.let {
        DataFieldCard(
            label = stringResource(R.string.location),
            value = it,
            modifier = modifier
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun PhysicalAttributesCard(
    itemDetails: ItemDetails
) {
    val modifier = Modifier.padding(bottom = 8.dp)

    ExpandableSummaryCard(
        label = stringResource(R.string.physical_attributes),
        data = listOf(
            itemDetails.width,
            itemDetails.height,
            itemDetails.weight,
            itemDetails.colorMain,
            itemDetails.colorSecondary
        )
    ) {
        Row(
            modifier = modifier.fillMaxWidth()
        ) {
            itemDetails.width?.let {
                DataFieldCard(
                    label = stringResource(R.string.width),
                    value = it.toLengthString(),
                    modifier = Modifier.weight(1f),
                    isSingleLine = itemDetails.height == null
                )
            }
            if (itemDetails.width != null && itemDetails.height != null) {
                Spacer(modifier = Modifier.width(16.dp))
            }
            itemDetails.height?.let {
                DataFieldCard(
                    label = stringResource(R.string.height),
                    value = it.toLengthString(),
                    modifier = Modifier.weight(1f),
                    isSingleLine = itemDetails.width == null
                )
            }
        }
        itemDetails.weight?.let {
            DataFieldCard(
                label = stringResource(R.string.weight),
                value = it.toWeightString(),
                modifier = modifier
            )
        }
        itemDetails.colorMain?.let {
            DataFieldCard(
                label = stringResource(R.string.color_main),
                value = it,
                modifier = modifier,
                hasContainer = false
            )
        }
        itemDetails.colorSecondary?.let {
            DataFieldCard(
                label = stringResource(R.string.color_secondary),
                value = it,
                hasContainer = false
            )
        }
    }
}

@Composable
private fun SourceInfoCard(
    itemDetails: ItemDetails
) {
    val modifier = Modifier.padding(bottom = 8.dp)

    ExpandableSummaryCard(
        label = stringResource(R.string.source),
        data = listOf(
            itemDetails.sourceName,
            itemDetails.sourceAlias,
            itemDetails.sourceType,
            itemDetails.sourceCountry,
            itemDetails.sourceDetails
        )
    ) {
        itemDetails.sourceName?.let {
            DataFieldCard(
                label = stringResource(R.string.source_name),
                value = it,
                modifier = modifier
            )
        }
        itemDetails.sourceAlias?.let {
            DataFieldCard(
                label = stringResource(R.string.source_alias),
                value = it,
                modifier = modifier,
                hasContainer = false
            )
        }
        itemDetails.sourceType?.let {
            DataFieldCard(
                label = stringResource(R.string.source_type),
                value = it,
                modifier = modifier
            )
        }
        itemDetails.sourceCountry?.let {
            DataFieldCard(
                label = stringResource(R.string.source_country),
                value = it,
                modifier = modifier
            )
        }
        itemDetails.sourceDetails?.let {
            DataFieldCard(
                label = stringResource(R.string.source_details),
                value = it,
                modifier = modifier,
                isSingleLine = false,
                hasContainer = false
            )
        }
    }
}

@Composable
private fun ArchivalInfoCard(
    itemDetails: ItemDetails
) {
    val modifier = Modifier.padding(bottom = 8.dp)

    ExpandableSummaryCard(
        label = stringResource(R.string.archival),
        data = listOf(
            itemDetails.archivalDate,
            itemDetails.archivalType,
            itemDetails.archivalDetails,
            itemDetails.price,
            itemDetails.recipientName,
            itemDetails.recipientAlias,
            itemDetails.recipientCountry
        )
    ) {
        itemDetails.archivalDate?.let {
            DataFieldCard(
                label = stringResource(R.string.archival_date),
                value = it,
                modifier = modifier
            )
        }
        itemDetails.archivalType?.let {
            DataFieldCard(
                label = stringResource(R.string.archival_reason),
                value = it,
                modifier = modifier,
                hasContainer = false
            )
        }
        itemDetails.price?.let {
            DataFieldCard(
                label = stringResource(R.string.sold_price),
                value = it.toString(),
                modifier = modifier
            )
        }
        itemDetails.archivalDetails?.let {
            DataFieldCard(
                label = stringResource(R.string.archival_details),
                value = it,
                modifier = modifier,
                hasContainer = false
            )
        }
        itemDetails.recipientName?.let {
            DataFieldCard(
                label = stringResource(R.string.recipient_name),
                value = it,
                modifier = modifier
            )
        }
        itemDetails.recipientAlias?.let {
            DataFieldCard(
                label = stringResource(R.string.recipient_alias),
                value = it,
                modifier = modifier,
                hasContainer = false
            )
        }
        itemDetails.recipientCountry?.let {
            DataFieldCard(
                label = stringResource(R.string.recipient_country),
                value = it,
                modifier = modifier,
                hasContainer = false
            )
        }
    }
}

@Composable
private fun ExpandableSummaryCard(
    label: String,
    data: List<Any?> = emptyList(),
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val onClick = remember { Modifier.clickable { isExpanded = !isExpanded } }

    if (data.any { it != null }) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .padding(/*horizontal = 8.dp, vertical = if (isExpanded) 16.dp else */8.dp)
                .animateContentSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(onClick)
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Rounded.KeyboardArrowUp
                    else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp)
                )
            }
            if (isExpanded) {
                Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                    content()
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private enum class SectionType {
    COMMON_DETAILS,
    UNIQUE_DETAILS,
    PHYSICAL_ATTRIBUTES,
    SOURCE_INFO,
    ARCHIVAL_INFO
}
