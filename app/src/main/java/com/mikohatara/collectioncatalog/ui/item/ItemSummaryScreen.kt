package com.mikohatara.collectioncatalog.ui.item

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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Collection
import com.mikohatara.collectioncatalog.data.Item
import com.mikohatara.collectioncatalog.data.ItemDetails
import com.mikohatara.collectioncatalog.ui.components.CopyItemDetailsDialog
import com.mikohatara.collectioncatalog.ui.components.DeletionDialog
import com.mikohatara.collectioncatalog.ui.components.InspectItemImage
import com.mikohatara.collectioncatalog.ui.components.ItemImage
import com.mikohatara.collectioncatalog.ui.components.ItemScreenModifiers
import com.mikohatara.collectioncatalog.ui.components.ItemSummaryTopAppBar
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
    val collections: List<Collection> = uiState.collections

    ItemSummaryScreen(
        item,
        itemDetails,
        collections,
        uiState,
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
    collections: List<Collection>,
    uiState: ItemSummaryUiState,
    viewModel: ItemSummaryViewModel,
    coroutineScope: CoroutineScope,
    onBack: () -> Unit,
    onEdit: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isInspectingImage by rememberSaveable { mutableStateOf(false) }
    var showDeletionDialog by rememberSaveable { mutableStateOf(false) }
    var showCopyDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ItemSummaryTopAppBar(
                title = itemDetails.regNo ?: "",
                item = item,
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
    itemDetails: ItemDetails,
    onInspectImage: () -> Unit,
    modifier: Modifier = Modifier,
    collections: List<Collection> = emptyList()
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Card(
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 24.dp,
                bottomEnd = 24.dp
            )
        ) {
            Card(
                modifier = Modifier.padding(horizontal = 8.dp)
                    .padding(top = 8.dp) //TODO remove this when topBar is re-touched
            ) {
                ItemImage(itemDetails.imagePath, onInspectImage)
            }
            Spacer(modifier = Modifier.height(20.dp))
            CommonDetails(
                regNo = itemDetails.regNo ?: "",
                country = itemDetails.country ?: "",
                region1st = itemDetails.region1st,
                region2nd = itemDetails.region2nd,
                region3rd = itemDetails.region3rd,
                type = itemDetails.type ?: "",
                year = itemDetails.year?.toString(),
                period = if (itemDetails.periodStart != null || itemDetails.periodEnd != null) {
                    itemDetails.periodStart?.toString() + " – " + itemDetails.periodEnd?.toString()
                } else {
                    null
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (collections.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Collections(collections = collections)
        }

        Card(
            modifier = Modifier.padding(16.dp)
        ) {
            UniqueDetails(
                notes = itemDetails.notes,
                vehicle = itemDetails.vehicle,
                date = itemDetails.date,
                cost = itemDetails.cost.toString(),
                value = itemDetails.value.toString(),
                status = itemDetails.status
            )
        }
        Card(
            modifier = Modifier.padding(16.dp)
        ) {
            PhysicalAttributes(
                width = itemDetails.width.toString(),
                height = itemDetails.height.toString(),
                weight = itemDetails.weight.toString(),
                colorMain = itemDetails.colorMain,
                colorSecondary = itemDetails.colorSecondary
            )
        }
        Card(
            modifier = Modifier.padding(16.dp)
        ) {
            SourceInfo(
                name = itemDetails.sourceName,
                alias = itemDetails.sourceAlias,
                type = itemDetails.sourceType,
                details = itemDetails.sourceDetails,
                country = itemDetails.sourceCountry
            )
        }
        Card(
            modifier = Modifier.padding(16.dp)
        ) {
            ArchivalInfo(
                date = itemDetails.archivalDate,
                reason = itemDetails.archivalType,
                details = itemDetails.archivalDetails,
                price = itemDetails.price.toString(),
                recipientName = itemDetails.recipientName,
                recipientAlias = itemDetails.recipientAlias,
                recipientCountry = itemDetails.recipientCountry
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CommonDetails(
    regNo: String,
    country: String,
    region1st: String?,
    region2nd: String?,
    region3rd: String?,
    type: String,
    year: String?,
    period: String?
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Text(
            text = regNo,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Row(

            ) {
                Text(
                    text = country
                )
                region1st?.let {
                    Text(
                        text = "・$it"
                    )
                }
            }
            if (region2nd != null || region3rd != null) {
                Row(

                ) {
                    region2nd?.let {
                        Text(
                            text = it
                        )
                    }
                    if (region2nd != null && region3rd != null) {
                        Text(
                            text = "・"
                        )
                    }
                    region3rd?.let {
                        Text(
                            text = it
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = type
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
    /*Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.type),
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = type ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }*/
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
            modifier = Modifier.weight(0.6f)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.year),
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = year?.toString() ?: "",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        if (year != null && period != null) {
            Spacer(modifier = Modifier.width(16.dp))
        }
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
            modifier = Modifier.weight(1f)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.period),
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = period ?: "",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun Collections(
    collections: List<Collection>
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
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
private fun UniqueDetails(
    notes: String?,
    vehicle: String?,
    date: String?,
    cost: String?,
    value: String?,
    status: String?
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                //.padding(bottom = 20.dp)
        ) {
            Text(
                text = stringResource(R.string.notes),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(0.5f)
            )
            Text(
                text = notes ?: "",
                modifier = Modifier//.padding(horizontal = 16.dp)
                    .weight(1f)
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.vehicle),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(0.5f)
            )
            Text(
                text = vehicle ?: "",
                modifier = Modifier.weight(1f)
            )
        }
    }

    Column(
        //colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        //modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.notes),
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = notes ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.vehicle),
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = vehicle ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        modifier = Modifier.padding(horizontal = 14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.date),
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = date ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
            modifier = Modifier.weight(1f)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.cost),
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = cost ?: "",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        if (cost != null && value != null) {
            Spacer(modifier = Modifier.width(16.dp))
        }
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
            modifier = Modifier.weight(1f)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.value),
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = value ?: "",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Card(
        //colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        modifier = Modifier.padding(horizontal = 14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.location),
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = status ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PhysicalAttributes(
    width: String?,
    height: String?,
    weight: String?,
    colorMain: String?,
    colorSecondary: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
            modifier = Modifier.weight(1f)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.width),
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = width ?: "",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        if (width != null && height != null) {
            Spacer(modifier = Modifier.width(16.dp))
        }
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
            modifier = Modifier.weight(1f)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.height),
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = height ?: "",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        modifier = Modifier.padding(horizontal = 14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.weight),
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = weight ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    Card(
        //colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        modifier = Modifier.padding(horizontal = 14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.color_main),
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = colorMain ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.color_secondary),
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = colorSecondary ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SourceInfo(
    name: String?,
    alias: String?,
    type: String?,
    details: String?,
    country: String?
) {
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        modifier = Modifier.padding(horizontal = 14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Source Name",
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = name ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Source Alias",
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = alias ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Source Type",
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = type ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Source Details",
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = details ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Source Country",
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = country ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ArchivalInfo(
    date: String?,
    reason: String?,
    details: String?,
    price: String?,
    recipientName: String?,
    recipientAlias: String?,
    recipientCountry: String?
) {
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        modifier = Modifier.padding(horizontal = 14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Archival Date",
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = date ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Archival Reason",
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = reason ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Archival Details",
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = details ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Price",
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = price ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Recipient Name",
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = recipientName ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Recipient Alias",
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = recipientAlias ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Recipient Country",
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = recipientCountry ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
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

private enum class SectionType {
    COMMON_DETAILS,
    UNIQUE_DETAILS,
    PHYSICAL_ATTRIBUTES,
    SOURCE_INFO,
    ARCHIVAL_INFO
}
