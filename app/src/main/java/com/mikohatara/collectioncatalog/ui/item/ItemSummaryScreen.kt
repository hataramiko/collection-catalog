package com.mikohatara.collectioncatalog.ui.item

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.samplePlates
import com.mikohatara.collectioncatalog.ui.components.DeletionDialog
import com.mikohatara.collectioncatalog.ui.components.IconAbc123
import com.mikohatara.collectioncatalog.ui.components.InspectItemImage
import com.mikohatara.collectioncatalog.ui.components.ItemImage
import com.mikohatara.collectioncatalog.ui.components.ItemScreenColumnSpacer
import com.mikohatara.collectioncatalog.ui.components.ItemScreenModifiers
import com.mikohatara.collectioncatalog.ui.components.ItemScreenRowSpacer
import com.mikohatara.collectioncatalog.ui.components.ItemSummaryTopAppBar
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
            "${item.uniqueDetails.number} : ${item.uniqueDetails.variant}",
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

@Composable
private fun ItemInformation(
    item: Plate,
    onInspectImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        ItemImage(item.uniqueDetails.imagePath, onInspectImage)

        Card(ItemScreenModifiers.card) {
            Column(ItemScreenModifiers.column) {
                Row(ItemScreenModifiers.rowWithIcon) {
                    IconAbc123()
                    StaticTextField(
                        label = stringResource(R.string.reg_no),
                        value = item.uniqueDetails.number,
                        modifier = Modifier
                    )
                }
            }
        }

        Card(ItemScreenModifiers.card) {
            Column(ItemScreenModifiers.column) {
                Row(ItemScreenModifiers.rowWithIcon) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_globe),
                        contentDescription = null,
                        modifier = ItemScreenModifiers.icon
                    )
                    StaticTextField(
                        label = "Country",
                        value = item.commonDetails.country,
                        modifier = Modifier
                    )
                }
                item.commonDetails.region?.let {
                    Row(ItemScreenModifiers.rowNoIcon) {
                        StaticTextField(
                            label = "Region",
                            value = it,
                            modifier = Modifier
                        )
                    }
                }
                item.commonDetails.area?.let {
                    Row(ItemScreenModifiers.rowNoIcon) {
                        StaticTextField(
                            label = "Area",
                            value = it,
                            modifier = Modifier
                        )
                    }
                }
                ItemScreenColumnSpacer()
                Row(ItemScreenModifiers.rowWithIcon) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_category),
                        contentDescription = null,
                        modifier = ItemScreenModifiers.icon
                    )
                    StaticTextField(
                        label = stringResource(R.string.type),
                        value = item.commonDetails.type,
                        modifier = Modifier
                    )
                }
                if(item.commonDetails.period != null || item.commonDetails.year != null) {
                    Row(ItemScreenModifiers.rowWithIcon) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_date_range),
                            contentDescription = null,
                            modifier = ItemScreenModifiers.icon
                        )
                        item.commonDetails.period?.let {
                            StaticTextField(
                                label = stringResource(R.string.period),
                                value = it,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        item.commonDetails.year?.let {
                            ItemScreenRowSpacer()
                            StaticTextField(
                                label = stringResource(R.string.year),
                                value = it.toString(),
                                modifier = Modifier.weight(0.66f)
                            )
                        }
                    }
                }
            }
        }

        /*
        Card(ItemScreenModifiers.card) {
            Column(ItemScreenModifiers.column) {
                item.uniqueDetails.date?.let {
                    Row(ItemScreenModifiers.rowWithIcon) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_event),
                            contentDescription = null,
                            modifier = ItemScreenModifiers.icon
                        )
                        StaticTextField(
                            label = stringResource(R.string.date),
                            value = it,
                            modifier = Modifier
                        )
                    }
                }
                if(item.uniqueDetails.cost != null || item.uniqueDetails.value != null) {
                    Row(ItemScreenModifiers.rowWithIcon) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_currency),
                            contentDescription = null,
                            modifier = ItemScreenModifiers.icon
                        )
                    }
                    item.uniqueDetails.cost?.let {
                        StaticTextField(
                            label = "Cost",
                            value = it.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    item.uniqueDetails.cost?.let {
                        StaticTextField(
                            label = stringResource(R.string.value),
                            value = it.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Card(ItemScreenModifiers.card) {
            Column(ItemScreenModifiers.column) {
                item.measurements.width?.let {
                    Row(ItemScreenModifiers.rowWithIcon) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_ruler),
                            contentDescription = null,
                            modifier = ItemScreenModifiers.icon
                        )
                        StaticTextField(
                            label = "Width",
                            value = it.toString(),
                            modifier = Modifier
                        )
                    }
                }
            }
        }*/

        /*
        Card(ItemScreenModifiers.card) {
            Column(ItemScreenModifiers.column) {
                Row() {

                }
            }
        }*/
    }
}

@Composable
private fun StaticTextField(label: String, value: String, modifier: Modifier) {

    //if (entry != "null" || entry != "" || entry != " ") {  }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0, 0, 0, 15)),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            label,
            color = Color(0, 0, 0, 128),
            fontSize = 11.sp,
            fontStyle = FontStyle.Italic,
            modifier = Modifier
                .padding(start = 8.dp)
        )
        Text(
            value,
            modifier = Modifier
                .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 12.dp)
        )
    }

    /*
    Column() {
        Row() {
            Column(modifier = Modifier.weight(1f)) {
                Box {
                    Text(
                        label,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .absoluteOffset(y = (-4).dp)
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0, 0, 0, 15)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(
                            value,
                            modifier = Modifier
                                .padding(start = 20.dp)//, top = 14.dp, bottom = 10.dp)
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }
            OutlinedTextField(
                value = value,
                label = { Text(label) },
                onValueChange = {},
                modifier = Modifier.weight(1f)
            )
        }
    }*/
/*
    Box(
        modifier = modifier.heightIn(min = 64.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0, 0, 0, 15)),
            modifier = Modifier
                .height(20.dp)
                .padding(start = 14.dp)
        ) {
            Text(
                label,
                fontSize = 10.sp,
                modifier = Modifier
                    .absoluteOffset(y = (-1).dp)
                    .padding(horizontal = 8.dp)//vertical = 12.dp)
            )
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0, 0, 0, 15)),
            modifier = Modifier
                //.heightIn(min = 64.dp)
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            /*Card(
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0, 0, 0, 15)),
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth()
            ) {}*/
            /*Box {
                Text(
                    label,
                    fontSize = 10.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier
                        .absoluteOffset(y = (-40).dp)
                        .padding(start = 12.dp)
                )

            }*/
            Text(
                value,
                modifier = Modifier
                    .absoluteOffset(y = 3.dp)
                    .padding(horizontal = 20.dp, vertical = 14.dp)//vertical = 12.dp)
            )
        }
    }
/*
    Box() {
        Text(
            label,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(start = 8.dp)
                .absoluteOffset(y = (-4).dp)
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0, 0, 0, 15)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(
                value,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }*/*/
}

@Preview
@Composable
fun StaticTextFieldPreview() {
    CollectionCatalogTheme {
        StaticTextField(label = "Details", value = "Passenger", modifier = Modifier)
    }
}

@Preview
@Composable
fun ItemSummaryScreenPreview() {
    CollectionCatalogTheme {
        ItemInformation(item = samplePlates[6], onInspectImage = { /*TODO*/ })
    }
}
