package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import java.io.File

@Composable
fun ItemCard(
    title: String,
    imagePath: String?,
    itemWidth: Int?,
    maxWidth: Int,
    onClick: () -> Unit
) {
    ItemCard(
        title = title,
        imagePath = imagePath,
        itemWidth = itemWidth,
        maxWidth = maxWidth,
        onCardClick = onClick
    )
}

@Composable
fun WishlistCard(
    country: String,
    region: String?,
    type: String,
    period: String?,
    year: String?,
    imagePath: String?,
    onClick: () -> Unit,
) {
    WishlistCard(
        country = country,
        region = region,
        type = type,
        period = period,
        year = year,
        onCardClick = onClick,
        imagePath = imagePath
    )
}

@Composable
private fun ItemCard(
    title: String,
    imagePath: String?,
    itemWidth: Int?,
    maxWidth: Int,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val onClick = remember { Modifier.clickable { onCardClick() } }
    val screenWidth = LocalConfiguration.current.screenWidthDp

    val maxWidthAsFloat = if (maxWidth > 0) maxWidth.toFloat() else 1f
    val itemWidthAsFloat = itemWidth?.toFloat() ?: maxWidthAsFloat

    val scale = screenWidth / maxWidthAsFloat
    val imageWidth = itemWidthAsFloat * scale

    Card(
        modifier = modifier.then(onClick)
    ) {
        if (imagePath != null) {
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

@Composable
private fun WishlistCard(
    country: String,
    region: String?,
    type: String,
    period: String?,
    year: String?,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier,
    imagePath: String? = null
) {
    val onClick = remember { Modifier.clickable { onCardClick() } }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(80.dp)
            ) {
                if (imagePath != null) {
                    Text(
                        text = stringResource(R.string.image_loading),
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                    )
                    AsyncImage(
                        model = ImageRequest
                            .Builder(LocalContext.current)
                            .data(data = File(imagePath))
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier,
                        contentScale = ContentScale.FillWidth,
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.rounded_no_image),
                        contentDescription = null,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
            Column {
                Text(text = country + ", " + region)
                Text(text = type)
                Text(text = period + ", " + year)
            }
        }
    }
}

@Preview
@Composable
fun CardPreview() {
    WishlistCard(
        country = "United States",
        region = "Arizona",
        type = "Passenger",
        period = "1995 – 2011",
        year = "2004",
        onCardClick = {}
    )
}
