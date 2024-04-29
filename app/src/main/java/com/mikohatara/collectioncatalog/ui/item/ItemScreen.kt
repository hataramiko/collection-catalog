package com.mikohatara.collectioncatalog.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.samplePlates
import com.mikohatara.collectioncatalog.ui.home.HomeBody
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme

@Composable
fun ItemScreen(
    modifier: Modifier = Modifier,
    //viewModel: ItemScreenViewModel //= hiltViewModel()
) {
    ItemScreenTopAppBar(item = samplePlates[0])
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreenTopAppBar(
    item: Plate,
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
                    IconButton(onClick = { /*TODO*/ }) {
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
                fields = samplePlates,
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
    }*/
}

@Composable
private fun ItemInformation(
    item: Plate,
    fields: List<Plate>,
    modifier: Modifier = Modifier
) {
    Column {
        Image()
        ItemInformationField(
            header = "Country",
            entry = item.commonDetails.country
        )
        ItemInformationField(
            header = "Region",
            entry = item.commonDetails.region.toString()
        )
        ItemInformationField(
            header = "Number",
            entry = item.uniqueDetails.number
        )
    }
}

@Composable
private fun ItemScreenContent() {
    Column(modifier = Modifier.fillMaxSize()) {

    }
}

@Composable
private fun ItemInformationField(
    header: String,
    entry: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            Text(
                text = header,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
            Text(text = entry)
        }
    }
}

@Preview
@Composable
fun ItemScreenPreview() {
    CollectionCatalogTheme {
        //ItemScreen()
        //ItemScreenContent()

        /*ItemInformationField(
            "Number",
            samplePlates[2].uniqueDetails.number
        )*/

        ItemInformation(
            item = samplePlates[2],
            fields = samplePlates
        )
    }
}