package com.mikohatara.collectioncatalog.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
    //Log.d("uiState item", item.toString())

    ItemSummaryScreenContent(
        item,
        viewModel,
        coroutineScope,
        onBack,
        onEdit,
        onDelete
    )
}

@Composable
fun ItemSummaryScreenContent(
    item: Plate,
    viewModel: ItemSummaryViewModel,
    coroutineScope: CoroutineScope,
    onBack: () -> Unit,
    onEdit: (Plate) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isInspectingImage by rememberSaveable { mutableStateOf(false) }
    var isDeletionDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = { ItemSummaryTopAppBar(
            item.uniqueDetails.number,
            item,
            onBack,
            onEdit,
            onDelete = { isDeletionDialog = true
                /*coroutineScope.launch {
                    viewModel.deleteItem()
                    onBack()
                }*/
            }
        ) },
        content = { innerPadding ->
            ItemInformation(
                item = item,
                onInspectImage = { isInspectingImage = true },
                modifier = modifier
                    .padding(innerPadding)
                    //.verticalScroll(rememberScrollState())
            )
        }
    )

    if (isInspectingImage) {
        InspectItemImage(
            imagePath = item.uniqueDetails.imagePath,
            onBack = { isInspectingImage = false }
        )
    }

    if (isDeletionDialog) {
        DeletionDialog(
            onConfirm = {
                isDeletionDialog = false
                coroutineScope.launch {
                    viewModel.deleteItem()
                    onBack()
                }
            },
            onCancel = { isDeletionDialog = false }
        )
    }
}

/*
    TODO come up with a dynamic way to display data entries in relation to their neighbors

    The commented out sections don't ensure that the icons come properly aligned.
    The if else if else if else mess works for now as an "easy" placeholder replacement in
    that regard, but is a pain to read and maintain. Replace with a proper solution
*/

@Composable
private fun ItemInformation(
    item: Plate,
    onInspectImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        ItemImage(item.uniqueDetails.imagePath, onInspectImage)
        ItemSummaryVerticalSpacer(true)

        Card(ItemScreenModifiers.card) {
            ItemSummaryVerticalSpacer(false)

            Row(ItemScreenModifiers.row) {
                Column {
                    Icon(
                        painter = painterResource(R.drawable.rounded_globe),
                        contentDescription = null,
                        modifier = ItemScreenModifiers.icon
                    )
                }
                Card(
                    colors = CardDefaults.cardColors
                        (containerColor = Color(0, 0, 0, 15)),
                ) {
                    StaticTextField(
                        label = "Country",
                        value = item.commonDetails.country,
                        modifier = Modifier
                    )
                    item.commonDetails.region?.let {
                        StaticTextField(
                            label = "Region",
                            value = it,
                            modifier = Modifier
                        )
                    }
                    item.commonDetails.area?.let {
                        StaticTextField(
                            label = "Area",
                            value = it,
                            modifier = Modifier
                        )
                    }
                }
            }

            Column(ItemScreenModifiers.row) {
                Row {
                    Icon(
                        painter = painterResource(R.drawable.rounded_category),
                        contentDescription = null,
                        modifier = ItemScreenModifiers.icon
                    )
                    Card(
                        shape = if(
                            item.commonDetails.period != null || item.commonDetails.year != null
                        ) {
                            RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            )
                        } else {
                            CardDefaults.shape
                        },
                        colors = CardDefaults.cardColors
                            (containerColor = Color(0, 0, 0, 15))
                    ) {
                        StaticTextField(
                            label = stringResource(R.string.type),
                            value = item.commonDetails.type,
                            modifier = Modifier
                        )
                    }
                }
                if(item.commonDetails.period != null || item.commonDetails.year != null) {
                    Row {
                        Icon(
                            painter = painterResource(R.drawable.rounded_date_range),
                            contentDescription = null,
                            modifier = ItemScreenModifiers.icon
                        )
                        Card(
                            shape = RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 0.dp,
                                bottomStart = 12.dp,
                                bottomEnd = 12.dp
                            ),
                            colors = CardDefaults.cardColors
                                (containerColor = Color(0, 0, 0, 15))
                        ) {
                            ItemSummaryHorizontalDivider()
                            Row {
                                item.commonDetails.period?.let {
                                    StaticTextField(
                                        label = stringResource(R.string.period),
                                        value = it,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                item.commonDetails.year?.let {
                                    StaticTextField(
                                        label = stringResource(R.string.year),
                                        value = it.toString(),
                                        modifier = Modifier.weight(0.5f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            /*
            Row(ItemScreenModifiers.row) {
                Column {
                    Icon(
                        painter = painterResource(R.drawable.rounded_category),
                        contentDescription = null,
                        modifier = ItemScreenModifiers.icon
                    )
                    if(item.commonDetails.period != null || item.commonDetails.year != null) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_date_range),
                            contentDescription = null,
                            modifier = ItemScreenModifiers.icon
                        )
                    }
                }
                Card(
                    colors = CardDefaults.cardColors
                        (containerColor = Color(0, 0, 0, 15))
                ) {
                    StaticTextField(
                        label = stringResource(R.string.type),
                        value = item.commonDetails.type,
                        modifier = Modifier
                    )
                    if(item.commonDetails.period != null || item.commonDetails.year != null) {
                        ItemSummaryHorizontalDivider()
                        Row {
                            item.commonDetails.period?.let {
                                StaticTextField(
                                    label = stringResource(R.string.period),
                                    value = it,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            item.commonDetails.year?.let {
                                StaticTextField(
                                    label = stringResource(R.string.year),
                                    value = it.toString(),
                                    modifier = Modifier.weight(0.5f)
                                )
                            }
                        }
                    }
                }
            }*/
            ItemSummaryVerticalSpacer(true)
        }

        if(
            item.uniqueDetails.notes != null ||
            item.uniqueDetails.vehicle != null ||
            item.uniqueDetails.date != null ||
            item.uniqueDetails.cost != null ||
            item.uniqueDetails.value != null ||
            item.uniqueDetails.status != null
        ) {
            Card(ItemScreenModifiers.card) {
                ItemSummaryVerticalSpacer(false)

                if(item.uniqueDetails.notes != null || item.uniqueDetails.vehicle != null) {
                    Row(ItemScreenModifiers.row) {
                        Column {
                            Icon(
                                painter = painterResource(R.drawable.rounded_note_stack),
                                contentDescription = null,
                                modifier = ItemScreenModifiers.icon
                            )
                        }
                        Card(
                            colors = CardDefaults.cardColors
                                (containerColor = Color(0, 0, 0, 15)),
                        ) {
                            item.uniqueDetails.notes?.let {
                                StaticTextField(
                                    label = "Notes",
                                    value = it,
                                    modifier = Modifier
                                )
                            }
                            item.uniqueDetails.vehicle?.let {
                                StaticTextField(
                                    label = "Vehicle",
                                    value = it,
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                }

                if(
                    item.uniqueDetails.date != null ||
                    item.uniqueDetails.cost != null ||
                    item.uniqueDetails.value != null ||
                    item.uniqueDetails.status != null
                ) {
                    Column(ItemScreenModifiers.row) {
                        item.uniqueDetails.date?.let {
                            Row {
                                Icon(
                                    painter = painterResource(R.drawable.rounded_event),
                                    contentDescription = null,
                                    modifier = ItemScreenModifiers.icon
                                )
                                Card(
                                    shape = if(
                                        item.uniqueDetails.cost != null ||
                                        item.uniqueDetails.value != null ||
                                        item.uniqueDetails.status != null
                                    ) {
                                        RoundedCornerShape(
                                            topStart = 12.dp,
                                            topEnd = 12.dp,
                                            bottomStart = 0.dp,
                                            bottomEnd = 0.dp
                                        )
                                    } else {
                                        CardDefaults.shape
                                    },
                                    colors = CardDefaults.cardColors
                                        (containerColor = Color(0, 0, 0, 15)),
                                ) {
                                    StaticTextField(
                                        label = stringResource(R.string.date),
                                        value = it,
                                        modifier = Modifier
                                    )
                                }
                            }
                        }
                        if(
                            item.uniqueDetails.cost != null ||
                            item.uniqueDetails.value != null
                        ) {
                            Row {
                                Icon(
                                    painter = painterResource(R.drawable.rounded_payments),
                                    contentDescription = null,
                                    modifier = ItemScreenModifiers.icon
                                )
                                Card(
                                    shape = if(
                                        item.uniqueDetails.date != null &&
                                        item.uniqueDetails.status != null
                                    ) {
                                        RoundedCornerShape(0.dp)
                                    } else if(
                                        item.uniqueDetails.date != null &&
                                        item.uniqueDetails.status == null
                                    ) {
                                        RoundedCornerShape(
                                            topStart = 0.dp,
                                            topEnd = 0.dp,
                                            bottomStart = 12.dp,
                                            bottomEnd = 12.dp
                                        )
                                    } else if(
                                        item.uniqueDetails.date == null &&
                                        item.uniqueDetails.status != null
                                    ) {
                                        RoundedCornerShape(
                                            topStart = 12.dp,
                                            topEnd = 12.dp,
                                            bottomStart = 0.dp,
                                            bottomEnd = 0.dp
                                        )
                                    } else {
                                        CardDefaults.shape
                                    },
                                    colors = CardDefaults.cardColors
                                        (containerColor = Color(0, 0, 0, 15)),
                                ) {
                                    if(item.uniqueDetails.date != null) {
                                        ItemSummaryHorizontalDivider()
                                    }
                                    Row {
                                        item.uniqueDetails.cost?.let {
                                            StaticTextField(
                                                label = "Cost",
                                                value = it.toString(),
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        item.uniqueDetails.value?.let {
                                            StaticTextField(
                                                label = stringResource(R.string.value),
                                                value = it.toString(),
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        item.uniqueDetails.status?.let {
                            Row {
                                Icon(
                                    painter = painterResource(R.drawable.rounded_info),
                                    contentDescription = null,
                                    modifier = ItemScreenModifiers.icon
                                )
                                Card(
                                    shape = if(
                                        item.uniqueDetails.date != null ||
                                        item.uniqueDetails.cost != null ||
                                        item.uniqueDetails.value != null
                                    ) {
                                        RoundedCornerShape(
                                            topStart = 0.dp,
                                            topEnd = 0.dp,
                                            bottomStart = 12.dp,
                                            bottomEnd = 12.dp
                                        )
                                    } else {
                                        CardDefaults.shape
                                    },
                                    colors = CardDefaults.cardColors
                                        (containerColor = Color(0, 0, 0, 15)),
                                ) {
                                    if(
                                        item.uniqueDetails.date != null ||
                                        item.uniqueDetails.cost != null ||
                                        item.uniqueDetails.value != null
                                    ) {
                                        ItemSummaryHorizontalDivider()
                                    }
                                    StaticTextField(
                                        label = "Status",
                                        value = it,
                                        modifier = Modifier
                                    )
                                }
                            }
                        }
                    }

                    /*
                    Row(ItemScreenModifiers.row) {
                        Column {
                            if(item.uniqueDetails.date != null) {
                                Icon(
                                    painter = painterResource(R.drawable.rounded_event),
                                    contentDescription = null,
                                    modifier = ItemScreenModifiers.icon
                                )
                            }
                            if(item.uniqueDetails.cost != null ||
                                item.uniqueDetails.value != null
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.rounded_payments),
                                    contentDescription = null,
                                    modifier = ItemScreenModifiers.icon
                                )
                            }
                            if(item.uniqueDetails.status != null) {
                                Icon(
                                    painter = painterResource(R.drawable.rounded_info),
                                    contentDescription = null,
                                    modifier = ItemScreenModifiers.icon
                                )
                            }
                        }
                        Card(
                            colors = CardDefaults.cardColors
                                (containerColor = Color(0, 0, 0, 15)),
                        ) {
                            item.uniqueDetails.date?.let {
                                StaticTextField(
                                    label = stringResource(R.string.date),
                                    value = it,
                                    modifier = Modifier
                                )
                            }
                            if(
                                item.uniqueDetails.cost != null ||
                                item.uniqueDetails.value != null &&
                                item.uniqueDetails.date != null
                            ) {
                                ItemSummaryHorizontalDivider()
                            }
                            Row {
                                item.uniqueDetails.cost?.let {
                                    StaticTextField(
                                        label = "Cost",
                                        value = it.toString(),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                item.uniqueDetails.value?.let {
                                    StaticTextField(
                                        label = stringResource(R.string.value),
                                        value = it.toString(),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            item.uniqueDetails.status?.let {
                                if(
                                    item.uniqueDetails.date != null ||
                                    item.uniqueDetails.cost != null ||
                                    item.uniqueDetails.value != null
                                ) {
                                    ItemSummaryHorizontalDivider()
                                }
                                StaticTextField(
                                    label = "Status",
                                    value = it,
                                    modifier = Modifier
                                )
                            }
                        }
                    }*/
                }
                ItemSummaryVerticalSpacer(true)
            }
        }

        Card(ItemScreenModifiers.card) {
            ItemSummaryVerticalSpacer(false)

            Row(ItemScreenModifiers.row) {
                Column {
                    Icon(
                        painter = painterResource(R.drawable.rounded_stars_layered),
                        contentDescription = null,
                        modifier = ItemScreenModifiers.icon
                    )
                }
                Card(
                    colors = CardDefaults.cardColors
                        (containerColor = Color(0, 0, 0, 15)),
                ) {
                    item.grading.condition?.let {
                        StaticTextField(
                            label = "Condition",
                            value = it,
                            modifier = Modifier
                        )
                    }
                    StaticSwitch("Keeper", item.grading.isKeeper)
                    StaticSwitch("For Trade", item.grading.isForTrade)
                }
            }
            ItemSummaryVerticalSpacer(true)
        }

        if(
            item.measurements.width != null ||
            item.measurements.height != null ||
            item.measurements.weight != null
        ) {
            Card(ItemScreenModifiers.card) {
                ItemSummaryVerticalSpacer(false)

                Row(ItemScreenModifiers.row) {
                    Column {
                        Icon(
                            painter = painterResource(R.drawable.rounded_ruler),
                            contentDescription = null,
                            modifier = ItemScreenModifiers.icon
                        )
                    }
                    Card(
                        colors = CardDefaults.cardColors
                            (containerColor = Color(0, 0, 0, 15)),
                    ) {
                        Row {
                            item.measurements.width?.let {
                                StaticTextField(
                                    label = "Width",
                                    value = it.toString(),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            item.measurements.height?.let {
                                StaticTextField(
                                    label = "Height",
                                    value = it.toString(),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            item.measurements.weight?.let {
                                StaticTextField(
                                    label = "Weight",
                                    value = it.toString(),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                ItemSummaryVerticalSpacer(true)
            }
        }

        if(
            item.source.sourceName != null ||
            item.source.sourceAlias != null ||
            item.source.sourceType != null ||
            item.source.sourceDetails != null ||
            item.source.sourceCountry != null
        ) {
            Card(ItemScreenModifiers.card) {
                ItemSummaryVerticalSpacer(false)

                Row(ItemScreenModifiers.row) {
                    Column {
                        Icon(
                            painter = painterResource(R.drawable.rounded_person_pin_circle),
                            contentDescription = null,
                            modifier = ItemScreenModifiers.icon
                        )
                    }
                    Card(
                        colors = CardDefaults.cardColors
                            (containerColor = Color(0, 0, 0, 15)),
                    ) {
                        item.source.sourceName?.let {
                            StaticTextField(
                                label = "Source Name",
                                value = it,
                                modifier = Modifier
                            )
                        }
                        item.source.sourceAlias?.let {
                            StaticTextField(
                                label = "Source Alias",
                                value = it,
                                modifier = Modifier
                            )
                        }
                        if(
                            item.source.sourceName != null ||
                            item.source.sourceAlias != null &&
                            item.source.sourceType != null ||
                            item.source.sourceDetails != null ||
                            item.source.sourceCountry != null
                        ) {
                            //ItemSummaryHorizontalDivider()
                            HorizontalDivider(Modifier.padding(start = 8.dp, end = 16.dp))
                        }
                        item.source.sourceType?.let {
                            StaticTextField(
                                label = "Source Type",
                                value = it,
                                modifier = Modifier
                            )
                        }
                        item.source.sourceDetails?.let {
                            StaticTextField(
                                label = "Source Details",
                                value = it,
                                modifier = Modifier
                            )
                        }
                        item.source.sourceCountry?.let {
                            if(
                                item.source.sourceName != null ||
                                item.source.sourceAlias != null ||
                                item.source.sourceType != null ||
                                item.source.sourceDetails != null
                            ) {
                                //ItemSummaryHorizontalDivider()
                                HorizontalDivider(Modifier.padding(start = 8.dp, end = 16.dp))
                            }
                            StaticTextField(
                                label = "Source Country",
                                value = it,
                                modifier = Modifier
                            )
                        }
                    }
                }
                ItemSummaryVerticalSpacer(true)
            }
        }
    }
}

@Composable
private fun StaticTextField(label: String, value: String, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            label,
            color = Color(0, 0, 0, 128),
            fontSize = 12.sp,
            modifier = Modifier
                .padding(start = 8.dp)
        )
        Text(
            value,
            modifier = Modifier
                .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 12.dp)
        )
    }
}

@Composable
private fun StaticSwitch(label: String, value: Boolean) {
    Row {
        Text(
            label,
            color = Color(0, 0, 0, 128),
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = value,
            onCheckedChange = {  },
            enabled = false,
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}

@Composable
private fun ItemSummaryHorizontalDivider() {
    HorizontalDivider(
        color = Color(0, 0, 0, 128),
        modifier = Modifier.padding(start = 8.dp, end = 16.dp)
    )
}

@Preview
@Composable
fun ItemSummaryScreenPreview() {
    CollectionCatalogTheme {
        ItemInformation(item = samplePlates[6], onInspectImage = {})
    }
}
