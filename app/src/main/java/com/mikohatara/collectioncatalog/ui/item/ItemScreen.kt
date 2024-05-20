package com.mikohatara.collectioncatalog.ui.item

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.ui.components.ItemScreenTopAppBar
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ItemScreen(
    viewModel: ItemViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    //val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uiState: ItemUiState by viewModel.uiState.collectAsStateWithLifecycle()
    Log.d("uiState plateNumber", viewModel.plateNumber)
    Log.d("uiState numberVariant", viewModel.numberVariant)

    val coroutineScope = rememberCoroutineScope()

    val item: Plate = uiState.item!!
    Log.d("uiState item", item.toString())

    /*if (item == null) {
        Log.d("item == null", "item was null, setting a samplePlate")
        item = samplePlates[2]
    }*/

    ItemScreenContent(item, viewModel, coroutineScope, onBack, onDelete)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreenContent(
    item: Plate,
    viewModel: ItemViewModel,
    coroutineScope: CoroutineScope,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var photoUri: Uri? by remember { mutableStateOf(null) }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()) { uri ->
        photoUri = uri
    }

    Scaffold(
        topBar = { ItemScreenTopAppBar(
            item.uniqueDetails.number,
            onBack,
            onAddPhoto = {
                photoPicker.launch(PickVisualMediaRequest(
                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                ))
            },
            onDelete = {
                coroutineScope.launch {
                    viewModel.deleteItem()
                    onBack()
                }
            }
        ) },
            /*TopAppBar(
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
        },*/
        content = { innerPadding ->
            ItemInformation(
                item = item,
                photoUri = photoUri,
                modifier = Modifier
                    .padding(innerPadding)
                    //.verticalScroll(rememberScrollState())
            )
        }
    )
}

@Composable
private fun Image(photoUri: Uri?) {

    if (photoUri != null) {
        val painter = rememberAsyncImagePainter(
            ImageRequest
                .Builder(LocalContext.current)
                .data(data = photoUri)
                .build()
        )

        AsyncImage(
            model = photoUri,
            contentDescription = null,
        )

        /*
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .background(Color.LightGray)
                .height(256.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )*/
    } else {
        Image(
            imageVector = Icons.Rounded.Clear,
            contentDescription = null,
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .height(128.dp)
        )
    }
}

@Composable
private fun ItemInformation(
    item: Plate,
    photoUri: Uri?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        Image(photoUri)

        Text(
            text = "Details",
            modifier = Modifier
                .padding(8.dp)
        )
        ItemInformationField(
            label = "Country",
            entry = item.commonDetails.country
        )
        ItemInformationField(
            label = "Region",
            entry = item.commonDetails.region.toString()
        )
        item.commonDetails.area?.let { /********************/
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

        Text(
            text = "Unique Details",
            modifier = Modifier
                .padding(8.dp)
        )
        ItemInformationField(
            label = "Number",
            entry = item.uniqueDetails.number
        )

        Text(
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