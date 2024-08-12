package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
    maxWidth: Int,
    onClick: () -> Unit
) {
    ItemCard(
        imagePath = item.uniqueDetails.imagePath,
        width = item.measurements.width,
        title = item.uniqueDetails.number,
        maxImageWidth = maxWidth,
        onCardClick = onClick
    )
}

@Composable
private fun ItemCard(
    imagePath: String?,
    width: Int?,
    title: String,
    maxImageWidth: Int,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val scale = screenWidth / maxImageWidth
    val onClick = remember { Modifier.clickable { onCardClick() } }

    Card(
        //onClick = onClick,
        modifier = modifier.then(onClick)
    ) {
        if (imagePath != null) {
            val imageWidth = (width ?: maxImageWidth) * scale

            Box {
                Text(
                    text = stringResource(R.string.image_loading),
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .widthIn(max = imageWidth.dp)
                        .align(Alignment.Center)
                )
                AsyncImage(
                    model = ImageRequest
                        .Builder(LocalContext.current)
                        .data(data = File(imagePath))
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = modifier
                        .width(imageWidth.dp),
                    contentScale = ContentScale.FillWidth,
                )
            }
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
        width = 440,
        title = "ABCD56789",
        maxImageWidth = 520,
        onCardClick = {}
    )
}
