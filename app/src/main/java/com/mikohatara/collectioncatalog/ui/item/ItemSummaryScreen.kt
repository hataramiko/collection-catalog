package com.mikohatara.collectioncatalog.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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

        Card(modifier = ItemScreenModifiers.card) {
            Row(modifier = ItemScreenModifiers.row) {
                Icon(
                    painter = painterResource(R.drawable.rounded_globe),
                    contentDescription = null,
                    modifier = ItemScreenModifiers.icon
                )
                Column(ItemScreenModifiers.column) {
                    StaticTextField("Country", item.commonDetails.country)
                    item.commonDetails.region?.let {
                        StaticTextField("Region", it)
                    }
                    item.commonDetails.area?.let {
                        StaticTextField("Area", it)
                    }
                }
            }
            Row(modifier = ItemScreenModifiers.row) {
                Icon(
                    painter = painterResource(R.drawable.rounded_category),
                    contentDescription = null,
                    modifier = ItemScreenModifiers.icon
                )
                Column(ItemScreenModifiers.column) {
                    StaticTextField("Type", item.commonDetails.type)
                }
            }
        }

        /*
        ItemInformationField(
            label = "Type",
            entry = item.commonDetails.type
        )
        item.commonDetails.period?.let {
            ItemInformationField(
                label = "Period",
                entry = it
            )
        }
        item.commonDetails.year?.let {
            ItemInformationField(
                label = "Year",
                entry = it.toString() // item.commonDetails.year.toString() ??
            )
        }

        item.measurements.width?.let {
            ItemInformationField(
                label = "Width",
                entry = it.toString()
            )
        }
        item.uniqueDetails.imagePath?.let {
            ItemInformationField(
                label = "imagePath debug",
                entry = it
            )
        }*/

        /*
        ItemInformationField(
            label = "Name",
            entry = item.source.sourceName.toString()
        )
        ItemInformationField(
            label = "Alias",
            entry = item.source.sourceAlias.toString()
        )
        ItemInformationField(
            label = "Details",
            entry = item.source.sourceDetails.toString()
        )
        ItemInformationField(
            label = "Type",
            entry = item.source.sourceType.toString()
        )
        ItemInformationField(
            label = "Country",
            entry = item.source.sourceCountry.toString()
        )*/
    }
}

@Composable
private fun StaticTextField(label: String, value: String) {

    //if (entry != "null" || entry != "" || entry != " ") {  }

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
    }
}

@Preview
@Composable
fun StaticTextFieldPreview() {
    CollectionCatalogTheme {
        StaticTextField(label = "Details", value = "Passenger")
    }
}

@Preview
@Composable
fun ItemSummaryScreenPreview() {
    CollectionCatalogTheme {
        ItemInformation(item = samplePlates[6], onInspectImage = { /*TODO*/ })
    }
}
