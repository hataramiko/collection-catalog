package com.mikohatara.collectioncatalog.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.ui.components.ItemImage
import com.mikohatara.collectioncatalog.ui.components.ItemScreenTopAppBar
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ItemScreen(
    viewModel: ItemViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onEdit: (Plate) -> Unit,
    onDelete: () -> Unit
) {
    //val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uiState: ItemUiState by viewModel.uiState.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    val item: Plate = uiState.item!!
    //Log.d("uiState item", item.toString())

    ItemScreenContent(
        item,
        viewModel,
        coroutineScope,
        onBack,
        onEdit,
        onDelete
    )
}

@Composable
fun ItemScreenContent(
    item: Plate,
    viewModel: ItemViewModel,
    coroutineScope: CoroutineScope,
    onBack: () -> Unit,
    onEdit: (Plate) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { ItemScreenTopAppBar(
            item.uniqueDetails.number,
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
                modifier = modifier
                    .padding(innerPadding)
                    //.verticalScroll(rememberScrollState())
            )
        }
    )
}

@Composable
private fun ItemInformation(
    item: Plate,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        ItemImage(item.uniqueDetails.imagePath)

        Text(
            text = "Details",
            modifier = Modifier
                .padding(8.dp)
        )
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
        ItemInformationField(
            label = "Number",
            entry = item.uniqueDetails.number
        )
        ItemInformationField(
            label = "Variant",
            entry = item.uniqueDetails.variant
        )
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
fun ItemScreenPreview() {
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