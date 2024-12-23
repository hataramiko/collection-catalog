package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
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
    region1st: String?,
    region2nd: String?,
    region3rd: String?,
    type: String,
    periodStart: Int?,
    periodEnd: Int?,
    year: Int?,
    regNo: String?,
    imagePath: String?,
    notes: String?,
    onClick: () -> Unit,
) {
    WishlistCard(
        country = country,
        onCardClick = onClick,
        imagePath = imagePath,
        regNo = regNo,
        notes = notes,
        region1st = region1st,
        region2nd = region2nd,
        region3rd = region3rd,
        type = type.ifBlank { null },
        periodStart = periodStart?.toString(),
        periodEnd = periodEnd?.toString(),
        year = year?.toString()
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
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier,
    imagePath: String? = null,
    regNo: String? = null,
    notes: String? = null,
    region1st: String? = null,
    region2nd: String? = null,
    region3rd: String? = null,
    type: String? = null,
    periodStart: String? = null,
    periodEnd: String? = null,
    year: String? = null,
) {
    val onClick = remember { Modifier.clickable { onCardClick() } }
    val regions = listOfNotNull(region1st, region2nd, region3rd).filterNot { it.isBlank() }
    val mainText = listOf(country).plus(regions).joinToString(", ")
    val period = if (periodStart.isNullOrBlank() && periodEnd.isNullOrBlank()) {
        null
    } else {
        "${periodStart ?: ""} – ${periodEnd ?: ""}".trim()
    }
    val subText = listOfNotNull(type, notes)
        .filterNot { it.isBlank() }
        .joinToString(", ")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .then(onClick)
    ) {
        Card(
            modifier = Modifier.size(80.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                if (imagePath != null) {
                    /*Text(
                        text = stringResource(R.string.image_loading),
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                    )*/
                    AsyncImage(
                        model = ImageRequest
                            .Builder(LocalContext.current)
                            .data(data = File(imagePath))
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier,
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.rounded_no_image),
                        contentDescription = null,
                        modifier = Modifier
                    )
                }
            }
        }
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = mainText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subText.isNotBlank()) {
                Text(
                    text = subText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Preview
@Composable
fun CardPreview() {
    WishlistCard(
        country = "Lorem ipsum",
        onCardClick = {},
        regNo = "V-####",
        notes = "With inspection stamp",
        region1st = "QWERTYUIOPÅ",
        region2nd = "ASDFGHJKLÖÄ",
        region3rd = "ZXCVBNM",
        type = "Passenger",
        periodStart = "1930",
        periodEnd = "1949",
        year = "1989",
    )
}
