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
import androidx.compose.material3.CardColors
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
import kotlinx.coroutines.launch

@Composable
fun ItemSummaryScreen(
    viewModel: ItemSummaryViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onEdit: (Item) -> Unit
) {
    val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val uiState: ItemSummaryUiState by viewModel.uiState.collectAsStateWithLifecycle()

    ItemSummaryScreen(
        viewModel,
        userPreferences,
        uiState,
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
    onBack: () -> Unit,
    onEdit: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    val item: Item = uiState.item ?: return
    val itemDetails: ItemDetails = uiState.itemDetails

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
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
                collections = uiState.collections,
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
private fun DataFieldBackground(
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
    content: @Composable () -> Unit
) {
    Card(
        colors = colors,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        content()
    }
}

@Composable
private fun DataFieldContent(
    label: String,
    value: String = "",
    modifier: Modifier = Modifier,
    isSingleLine: Boolean = true
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

@Composable
private fun CommonDetailsCard(
    itemDetails: ItemDetails,
    image: @Composable () -> Unit
) {
    val period: String? = (itemDetails.periodStart != null || itemDetails.periodEnd != null)
        .takeIf { it }?.let {
            "${itemDetails.periodStart ?: ""} – ${itemDetails.periodEnd ?: ""}"
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
            itemDetails.let {
                it.country?.let { value -> Text(text = value) }
                it.region1st?.let { value -> Text(text = value) }
                it.region2nd?.let { value -> Text(text = value) }
                it.region3rd?.let { value -> Text(text = value) }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row {
                itemDetails.type?.let { Text(text = it) }
                period?.let { Text(text = "・$it") }
            }
            itemDetails.year?.let { Text(text = it.toString()) }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun UniqueDetailsCard(
    itemDetails: ItemDetails,
    localeCode: String
) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        if (itemDetails.notes != null || itemDetails.vehicle != null) {
            DataFieldBackground {
                itemDetails.notes?.let {
                    DataFieldContent(
                        label = stringResource(R.string.notes),
                        value = it,
                        isSingleLine = false
                    )
                }
                itemDetails.vehicle?.let {
                    DataFieldContent(
                        label = stringResource(R.string.vehicle),
                        value = it,
                        isSingleLine = false
                    )
                }
            }
        }
        itemDetails.date?.let {
            DataFieldBackground {
                DataFieldContent(
                    label = stringResource(R.string.date),
                    value = it
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            itemDetails.cost?.let {
                DataFieldBackground(
                    modifier = Modifier.weight(1f)
                ) {
                    DataFieldContent(
                        label = stringResource(R.string.cost),
                        value = it.toCurrencyString(localeCode),
                        isSingleLine = itemDetails.value == null
                    )
                }
            }
            if (itemDetails.cost != null && itemDetails.value != null) {
                Spacer(modifier = Modifier.width(16.dp))
            }
            itemDetails.value?.let {
                DataFieldBackground(
                    modifier = Modifier.weight(1f)
                ) {
                    DataFieldContent(
                        label = stringResource(R.string.value),
                        value = it.toCurrencyString(localeCode),
                        isSingleLine = itemDetails.cost == null
                    )
                }
            }
        }
        itemDetails.status?.let {
            DataFieldBackground {
                DataFieldContent(
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
            modifier = Modifier.fillMaxWidth()
        ) {
            itemDetails.width?.let {
                DataFieldBackground(
                    modifier = Modifier.weight(1f)
                ) {
                    DataFieldContent(
                        label = stringResource(R.string.width),
                        value = it.toLengthString(),
                        isSingleLine = itemDetails.height == null
                    )
                }
            }
            if (itemDetails.width != null && itemDetails.height != null) {
                Spacer(modifier = Modifier.width(16.dp))
            }
            itemDetails.height?.let {
                DataFieldBackground(
                    modifier = Modifier.weight(1f)
                ) {
                    DataFieldContent(
                        label = stringResource(R.string.height),
                        value = it.toLengthString(),
                        isSingleLine = itemDetails.width == null
                    )
                }
            }
        }
        itemDetails.weight?.let {
            DataFieldBackground {
                DataFieldContent(
                    label = stringResource(R.string.weight),
                    value = it.toWeightString()
                )
            }
        }
        if (itemDetails.colorMain != null || itemDetails.colorSecondary != null) {
            DataFieldBackground {
                itemDetails.colorMain?.let {
                    DataFieldContent(
                        label = stringResource(R.string.color_main),
                        value = it
                    )
                }
                itemDetails.colorSecondary?.let {
                    DataFieldContent(
                        label = stringResource(R.string.color_secondary),
                        value = it
                    )
                }
            }
        }
    }
}

@Composable
private fun SourceInfoCard(
    itemDetails: ItemDetails
) {
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
        if (itemDetails.sourceName != null || itemDetails.sourceAlias != null) {
            DataFieldBackground {
                itemDetails.sourceName?.let {
                    DataFieldContent(
                        label = stringResource(R.string.source_name),
                        value = it
                    )
                }
                itemDetails.sourceAlias?.let {
                    DataFieldContent(
                        label = stringResource(R.string.source_alias),
                        value = it
                    )
                }
            }
        }
        itemDetails.sourceType?.let {
            DataFieldBackground {
                DataFieldContent(
                    label = stringResource(R.string.source_type),
                    value = it
                )
            }
        }
        itemDetails.sourceCountry?.let {
            DataFieldBackground {
                DataFieldContent(
                    label = stringResource(R.string.source_country),
                    value = it
                )
            }
        }
        itemDetails.sourceDetails?.let {
            DataFieldBackground {
                DataFieldContent(
                    label = stringResource(R.string.source_details),
                    value = it,
                    isSingleLine = false
                )
            }
        }
    }
}

@Composable
private fun ArchivalInfoCard(
    itemDetails: ItemDetails
) {
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
            DataFieldBackground {
                DataFieldContent(
                    label = stringResource(R.string.archival_date),
                    value = it
                )
            }
        }
        itemDetails.archivalType?.let {
            DataFieldBackground {
                DataFieldContent(
                    label = stringResource(R.string.archival_reason),
                    value = it
                )
            }
        }
        itemDetails.price?.let {
            DataFieldBackground {
                DataFieldContent(
                    label = stringResource(R.string.sold_price),
                    value = it.toString()
                )
            }
        }
        if (itemDetails.recipientName != null ||
            itemDetails.recipientAlias != null ||
            itemDetails.recipientCountry != null
        ) {
            DataFieldBackground {
                itemDetails.recipientName?.let {
                    DataFieldContent(
                        label = stringResource(R.string.recipient_name),
                        value = it
                    )
                }
                itemDetails.recipientAlias?.let {
                    DataFieldContent(
                        label = stringResource(R.string.recipient_alias),
                        value = it
                    )
                }
                itemDetails.recipientCountry?.let {
                    DataFieldContent(
                        label = stringResource(R.string.recipient_country),
                        value = it
                    )
                }
            }
        }
        itemDetails.archivalDetails?.let {
            DataFieldBackground {
                DataFieldContent(
                    label = stringResource(R.string.archival_details),
                    value = it,
                    isSingleLine = false
                )
            }
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
