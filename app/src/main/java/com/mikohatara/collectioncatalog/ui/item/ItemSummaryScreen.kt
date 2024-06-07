package com.mikohatara.collectioncatalog.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.ui.components.InspectItemImage
import com.mikohatara.collectioncatalog.ui.components.ItemImage
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
    //val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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

    Scaffold(
        topBar = { ItemSummaryTopAppBar(
            "${item.uniqueDetails.number} : ${item.uniqueDetails.variant}",
            item,
            onBack,
            onEdit,
            onDelete = {
                coroutineScope.launch {
                    viewModel.deleteItem()
                    onBack()
                }
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
}

@Composable
private fun ItemInformation(
    item: Plate,
    onInspectImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        ItemImage(item.uniqueDetails.imagePath, onInspectImage)

        Text(
            text = "Details",
            modifier = Modifier
                .padding(8.dp)
        )
        Card(modifier = Modifier.padding(horizontal = (8.dp), vertical = (4.dp))) {
            ItemInformationField(
                label = "Country",
                entry = item.commonDetails.country
            )
            item.commonDetails.region?.let { // toString() ???
                ItemInformationField(
                    label = "Region",
                    entry = it // item.commonDetails.region!! ???
                )
            }
            item.commonDetails.area?.let {
                ItemInformationField(
                    label = "Area",
                    entry = it
                )
            }
        }
        /*ItemInformationField(
            label = "Country",
            entry = item.commonDetails.country
        )
        item.commonDetails.region?.let { // toString() ???
            ItemInformationField(
                label = "Region",
                entry = it // item.commonDetails.region!! ???
            )
        }
        item.commonDetails.area?.let {
            ItemInformationField(
                label = "Area",
                entry = it
            )
        }*/
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

        /*Text(
            text = "Unique Details",
            modifier = Modifier
                .padding(8.dp)
        )*/
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
        }

        /*Text(
            text = "Source",
            modifier = Modifier
                .padding(8.dp)
        )
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
private fun ItemInformationField(
    label: String,
    entry: String
) {
    if (entry != "null" || entry == null) {
        Card(
            modifier = Modifier
                .padding(horizontal = (8.dp), vertical = (4.dp))
                .fillMaxWidth()
        ) {
            Column {
                Text(
                    text = label
                )
                Text(
                    text = entry,
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun ItemSummaryScreenPreview() {
    CollectionCatalogTheme {
        //ItemScreen(samplePlates[4])

        /*ItemInformationField(
            "Number",
            samplePlates[2].uniqueDetails.number
        )*/

        /*ItemInformation(
            item = samplePlates[2],
            fields = samplePlates
        )*/
    }
}