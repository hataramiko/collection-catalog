package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Plate
import java.io.File

@Composable
fun ItemCard(
    item: Plate,
    maxWidth: Double,
    onClick: () -> Unit
) {
    ItemCard(
        imagePath = item.uniqueDetails.imagePath,
        width = item.measurements.width,
        title = item.uniqueDetails.number,
        maxImageWidth = maxWidth,
        onClick = onClick
    )
}

@Composable
private fun ItemCard(
    imagePath: String?,
    width: Double?,
    title: String,
    maxImageWidth: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val scale = screenWidth / maxImageWidth

    Card(
        onClick = onClick,
        modifier = modifier
            //.fillMaxWidth()
            //.height(IntrinsicSize.Min)
    ) {
        if (imagePath != null) {

            val imageWidth = (width ?: maxImageWidth) * scale
            //Log.d("ItemCard", "ItemCard: $imageWidth")

            AsyncImage(
                model = ImageRequest
                    .Builder(LocalContext.current)
                    .data(data = File(imagePath))
                    .build(),
                contentDescription = null,
                modifier = modifier
                    //.align(Alignment.CenterHorizontally)
                    .width(imageWidth.dp),
                    //.height(IntrinsicSize.Min),
                contentScale = ContentScale.FillWidth,
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.rounded_no_image),
                    contentDescription = null,
                    modifier = modifier
                        .padding(20.dp)
                )
                Text(
                    text = "$title\n${stringResource(R.string.no_image)}"
                )
            }
        }
    }
}

@Preview
@Composable
fun CardPreview() {
    ItemCard(
        imagePath = null,
        width = 440.0,
        title = "ABCD56789",
        maxImageWidth = 520.0,
        onClick = {}
    )
}