package com.mikohatara.collectioncatalog.ui.item

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.samplePlates
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme

@Composable
fun ItemScreen(
    viewModel: ItemViewModel = hiltViewModel(),
    //navController: NavController,
    onBack: () -> Unit,
) {
    //val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uiState: ItemUiState by viewModel.uiState.collectAsStateWithLifecycle()
    Log.d("uiState plateNumber", viewModel.plateNumber)
    Log.d("uiState numberVariant", viewModel.numberVariant)

    val item: Plate = uiState.item!!
    Log.d("uiState item", item.toString())

    /*if (item == null) {
        Log.d("item == null", "item was null, setting a samplePlate")
        item = samplePlates[2]
    }*/

    ItemScreenContent(item, onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreenContent(
    item: Plate,
    //navController: NavController,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        item.uniqueDetails.number,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /*navController.popBackStack()*/ onBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit"
                        )
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More actions")
                    }
                }
            )
        },
        content = { innerPadding ->
            ItemInformation(
                item = item,
                modifier = Modifier
                    .padding(innerPadding)
                    //.verticalScroll(rememberScrollState())
            )
        }
    )
}

@Composable
private fun Image() {
    /*if (imageNotNull) {
        Image(painter = , contentDescription = )
    } else {*/
        Image(
            imageVector = Icons.Rounded.Clear,
            contentDescription = null,
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .height(128.dp)
        )
    //}
}

@Composable
private fun ItemInformation(
    item: Plate,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Image()

        Text(text = "Details")
        ItemInformationField(
            label = "Country",
            entry = item.commonDetails.country
        )
        ItemInformationField(
            label = "Region",
            entry = item.commonDetails.region.toString()
        )
        item.commonDetails.area?.let {
            ItemInformationField(
                label = "Region",
                entry = item.commonDetails.region.toString()
            )
        }
        ItemInformationField(
            label = "Area",
            entry = item.commonDetails.area.toString()
        )
        ItemInformationField(
            label = "Type",
            entry = item.commonDetails.type
        )
        ItemInformationField(
            label = "Period",
            entry = item.commonDetails.period.toString()
        )
        ItemInformationField(
            label = "Year",
            entry = item.commonDetails.year.toString()
        )

        Text(text = "Unique Details")
        ItemInformationField(
            label = "Number",
            entry = item.uniqueDetails.number
        )

        Text(text = "Source")
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
        )
    }

    //fields = listOf(Plate())
}

@Composable
private fun ItemInformationField(
    label: String,
    entry: String
) {
    if (entry != "null" || entry == null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {
                Text(
                    text = label,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
                Text(text = entry)
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