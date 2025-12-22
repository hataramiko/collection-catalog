package com.mikohatara.collectioncatalog.ui.item

import android.icu.util.MeasureUnit
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Collection
import com.mikohatara.collectioncatalog.data.Item
import com.mikohatara.collectioncatalog.data.ItemDetails
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.data.UserPreferences
import com.mikohatara.collectioncatalog.ui.components.DeletionDialog
import com.mikohatara.collectioncatalog.ui.components.ExpandableSummaryCard
import com.mikohatara.collectioncatalog.ui.components.IconCollectionLabel
import com.mikohatara.collectioncatalog.ui.components.InspectItemImage
import com.mikohatara.collectioncatalog.ui.components.ItemImage
import com.mikohatara.collectioncatalog.ui.components.ItemSummaryTopAppBar
import com.mikohatara.collectioncatalog.ui.components.TransferDialog
import com.mikohatara.collectioncatalog.util.toCurrencyString
import com.mikohatara.collectioncatalog.util.toFormattedDate
import com.mikohatara.collectioncatalog.util.toMeasurementString
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
    val item: Item = uiState.item ?: run {
        Log.e("ItemSummaryScreen", "Item is null")
        return
    }
    val itemDetails: ItemDetails = uiState.itemDetails

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults
        .exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    var isInspectingImage by rememberSaveable { mutableStateOf(false) }
    var showDeletionDialog by rememberSaveable { mutableStateOf(false) }
    var showCheckWishlistDialog by rememberSaveable { mutableStateOf(false) }
    var showTransferDialog by rememberSaveable { mutableStateOf(false) }
    val onDismissDeletionDialog = { showDeletionDialog = false }
    val onDismissCheckWishlistDialog = { showCheckWishlistDialog = false }
    val onDismissTransferDialog = { showTransferDialog = false }
    val onBackBehavior = { if (isInspectingImage) isInspectingImage = false else onBack() }
    val onCheckWishlistLambda = { showCheckWishlistDialog = true }
        .takeIf { uiState.itemType == ItemType.WANTED_PLATE }
    val onTransferLambda = { showTransferDialog = true }
        .takeIf { uiState.itemType != ItemType.FORMER_PLATE }

    val (transferButtonText, transferButtonPainter) = when (uiState.item) {
        is Item.WantedPlateItem -> stringResource(R.string.transfer_from_wishlist_button) to
            painterResource(R.drawable.rounded_done_all_24)
        is Item.PlateItem -> stringResource(R.string.transfer_from_plates_button) to
            painterResource(R.drawable.rounded_archive)
        else -> "" to painterResource(R.drawable.rounded_question_mark)
    }
    val (transferDialogTitle, transferDialogText) = when (uiState.item) {
        is Item.WantedPlateItem -> stringResource(
            R.string.transfer_from_wishlist_dialog_title
        ) to stringResource(R.string.transfer_from_wishlist_dialog_text)
        is Item.PlateItem -> stringResource(
            R.string.transfer_from_plates_dialog_title, uiState.itemDetails.regNo ?: ""
        ) to stringResource(R.string.transfer_from_plates_dialog_text)
        else -> "" to ""
    }

    val copyToast = stringResource(R.string.copied)
    val deletionToast = if (uiState.item is Item.WantedPlateItem) {
        stringResource(R.string.deletion_message_wishlist)
    } else {
        stringResource(
            R.string.deletion_message_plate,
            uiState.itemDetails.regNo ?: ""
        )
    }
    val transferToast = when (uiState.item) {
        is Item.WantedPlateItem -> stringResource(R.string.transferred_from_wishlist_message)
        is Item.PlateItem -> stringResource(
            R.string.transferred_from_plates_message,
            uiState.itemDetails.regNo ?: ""
        )
        else -> ""
    }

    BackHandler { onBackBehavior() }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ItemSummaryTopAppBar(
                title = itemDetails.regNo ?: "",
                item = item,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surfaceContainerHighest,
                    scrolledContainerColor = colorScheme.surfaceContainerHigh,
                ),
                scrollBehavior = scrollBehavior,
                onBack = onBack,
                onEdit = onEdit,
                onDelete = { showDeletionDialog = true },
                onCopy = {
                    viewModel.copyItemDetails()
                    viewModel.showToast(context, copyToast)
                },
                onCheckWishlist = onCheckWishlistLambda,
                onTransfer = onTransferLambda,
                transferButtonText = transferButtonText,
                transferButtonPainter = transferButtonPainter
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
                onDismissDeletionDialog()
                coroutineScope.launch {
                    viewModel.deleteItem()
                    viewModel.showToast(context, deletionToast)
                    onBack()
                }
            },
            onCancel = onDismissDeletionDialog
        )
    }
    if (showCheckWishlistDialog) {
        TransferDialog(
            title = stringResource(R.string.check_wishlist_dialog_title),
            text = stringResource(R.string.check_wishlist_dialog_text),
            onConfirm = {
                onDismissCheckWishlistDialog()
                coroutineScope.launch {
                    viewModel.transferItem()
                    viewModel.showToast(context, transferToast)
                }
            },
            onCancel = onDismissCheckWishlistDialog
        )
    }
    if (showTransferDialog) {
        TransferDialog(
            title = transferDialogTitle,
            text = transferDialogText,
            onConfirm = {
                onDismissTransferDialog()
                coroutineScope.launch {
                    viewModel.transferItem()
                    viewModel.deleteItem()
                    viewModel.showToast(context, transferToast)
                    onBack()
                }
            },
            onCancel = onDismissTransferDialog
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
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        CommonDetailsCard(
            itemDetails = itemDetails,
            image = {
                ItemImage(
                    onClick = onInspectImage,
                    imagePath = itemDetails.imagePath,
                    modifier = Modifier.padding(16.dp)
                )
            }
        )
        if (collections.isNotEmpty()) {
            Collections(collections = collections)
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }
        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            UniqueDetailsCard(
                itemDetails = itemDetails,
                localeCode = userPreferences.userCountry
            )
            SizeCard(
                itemDetails = itemDetails,
                localeCode = userPreferences.userCountry,
                lengthUnit = userPreferences.lengthUnit,
                weightUnit = userPreferences.weightUnit
            )
            ColorCard(
                itemDetails = itemDetails
            )
            SourceInfoCard(
                itemDetails = itemDetails
            )
            ArchivalInfoCard(
                itemDetails = itemDetails,
                localeCode = userPreferences.userCountry
            )
        }
    }
}

@Composable
private fun DataFieldBackground(
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(colorScheme.surfaceContainerLow),
    content: @Composable () -> Unit
) {
    Card(
        colors = colors,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        content()
    }
}

@Composable
private fun DataFieldContent(
    label: String,
    value: String = "",
    isSingleLine: Boolean = true
) {
    if (isSingleLine) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = label,
                color = colorScheme.secondary,
                style = typography.labelLarge
            )
            Text(
                text = value,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = label,
                color = colorScheme.secondary,
                style = typography.labelLarge
            )
            Text(
                text = value,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
        }
    }
}

@Composable
private fun CommonDetailsCard(
    itemDetails: ItemDetails,
    image: @Composable () -> Unit
) {
    val primaryColor = colorScheme.onSurface
    val secondaryColor = colorScheme.onSurfaceVariant
    val period: String? = (itemDetails.periodStart != null || itemDetails.periodEnd != null)
        .takeIf { it }?.let {
            "${itemDetails.periodStart ?: ""}–${itemDetails.periodEnd ?: ""}"
        }

    Card(
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 24.dp,
            bottomEnd = 24.dp
        ),
        colors = CardDefaults.cardColors(colorScheme.surfaceContainerHighest)
    ) {
        image.invoke()
        Column(
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp)
        ) {
            itemDetails.let {
                it.country?.let { value ->
                    Text(text = value, color = primaryColor)
                }
                it.region1st?.let { value -> Text(text = value, color = secondaryColor) }
                it.region2nd?.let { value -> Text(text = value, color = secondaryColor) }
                it.region3rd?.let { value -> Text(text = value, color = secondaryColor) }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row {
                itemDetails.type?.let { Text(text = it, color = primaryColor) }
                if (itemDetails.type?.isNotBlank() == true && period?.isNotBlank() == true) {
                    Text(text = "・", color = primaryColor)
                }
                period?.let { Text(text = it, color = primaryColor) }
            }
            itemDetails.year?.let { Text(text = it.toString(), color = secondaryColor) }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun Collections(
    collections: List<Collection>
) {
    val sortedCollections = collections.sortedBy { it.name }

    Column(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            sortedCollections.forEach { collection ->
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = collection.name,
                            color = colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        if (!collection.emoji.isNullOrBlank()) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Text(text = collection.emoji)
                            }
                        } else {
                            IconCollectionLabel(
                                color = collection.color.color
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun UniqueDetailsCard(
    itemDetails: ItemDetails,
    localeCode: String
) {
    ExpandableSummaryCard(
        label = stringResource(R.string.unique_details_label),
        data = listOf(
            itemDetails.notes,
            itemDetails.vehicle,
            itemDetails.date,
            itemDetails.cost,
            itemDetails.value,
            itemDetails.status
        )
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
                    value = it.toFormattedDate(localeCode)
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
                DataFieldRowSpacer()
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
private fun SizeCard(
    itemDetails: ItemDetails,
    localeCode: String,
    lengthUnit: MeasureUnit,
    weightUnit: MeasureUnit
) {
    ExpandableSummaryCard(
        label = stringResource(R.string.size),
        data = listOf(
            itemDetails.width,
            itemDetails.height,
            itemDetails.weight
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
                        value = it.toMeasurementString(lengthUnit, localeCode),
                        isSingleLine = itemDetails.height == null
                    )
                }
            }
            if (itemDetails.width != null && itemDetails.height != null) {
                DataFieldRowSpacer()
            }
            itemDetails.height?.let {
                DataFieldBackground(
                    modifier = Modifier.weight(1f)
                ) {
                    DataFieldContent(
                        label = stringResource(R.string.height),
                        value = it.toMeasurementString(lengthUnit, localeCode),
                        isSingleLine = itemDetails.width == null
                    )
                }
            }
        }
        itemDetails.weight?.let {
            DataFieldBackground {
                DataFieldContent(
                    label = stringResource(R.string.weight),
                    value = it.toMeasurementString(weightUnit, localeCode)
                )
            }
        }
    }
}

@Composable
private fun ColorCard(
    itemDetails: ItemDetails
) {
    ExpandableSummaryCard(
        label = stringResource(R.string.color),
        data = listOf(
            itemDetails.colorMain,
            itemDetails.colorSecondary
        )
    ) {
        if (itemDetails.colorMain != null || itemDetails.colorSecondary != null) {
            DataFieldBackground {
                itemDetails.colorMain?.let {
                    DataFieldContent(
                        label = stringResource(R.string.color_base),
                        value = it
                    )
                }
                itemDetails.colorSecondary?.let {
                    DataFieldContent(
                        label = stringResource(R.string.color_characters),
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
    itemDetails: ItemDetails,
    localeCode: String
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
                    value = it.toFormattedDate(localeCode)
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
                    value = it.toCurrencyString(localeCode)
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
private fun DataFieldRowSpacer() {
    Spacer(modifier = Modifier.width(8.dp))
}
