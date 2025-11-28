package com.mikohatara.collectioncatalog.ui.components

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.util.generatePalette
import com.mikohatara.collectioncatalog.util.getBitmapFromEdges
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
    country: String?,
    region1st: String?,
    region2nd: String?,
    region3rd: String?,
    type: String?,
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
        type = type,
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
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val onClick = remember(onCardClick) { Modifier.clickable { onCardClick() } }
    val screenWidth = remember { configuration.screenWidthDp }

    val maxWidthAsFloat = remember(maxWidth) { if (maxWidth > 0) maxWidth.toFloat() else 1f }
    val itemWidthAsFloat = remember(itemWidth) { itemWidth?.toFloat() ?: maxWidthAsFloat }

    val scale = remember(screenWidth, maxWidthAsFloat) { screenWidth / maxWidthAsFloat }
    val imageWidth = remember(scale, itemWidthAsFloat) { itemWidthAsFloat * scale }

    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
        modifier = modifier.then(onClick)
    ) {
        if (imagePath != null) {
            val imageUri = remember { Uri.fromFile(File(imagePath)) }
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data(imageUri)
                    .size(imageWidth.toInt() * 2)
                    .crossfade(true)
                    .build()
            )
            ItemCardContentImage(painter = painter, title = title, imageWidth = imageWidth.dp)
        } else {
            ItemCardContentNoImage(title = title)
        }
    }
}

@Composable
private fun WishlistCard(
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier,
    imagePath: String? = null,
    regNo: String? = null,
    notes: String? = null,
    country: String? = null,
    region1st: String? = null,
    region2nd: String? = null,
    region3rd: String? = null,
    type: String? = null,
    periodStart: String? = null,
    periodEnd: String? = null,
    year: String? = null,
) {
    val context = LocalContext.current
    val onClick = remember { Modifier.clickable { onCardClick() } }

    val period = if (periodStart.isNullOrBlank() && periodEnd.isNullOrBlank()) {
        null
    } else {
        "${periodStart ?: ""}–${periodEnd ?: ""}"
    }

    val primaryText = listOfNotNull(country, region1st, region2nd, region3rd)
        .filterNot { it.isBlank() }
        .joinToString(", ")
    val secondaryText = listOfNotNull(type, period, year)
        .filterNot { it.isBlank() }
        .joinToString("・")
    val tertiaryText = listOfNotNull(regNo, notes)
        .filterNot { it.isBlank() }
        .joinToString("・")

    val defaultColor = MaterialTheme.colorScheme.surfaceContainerHigh
    var containerColor by remember { mutableStateOf(defaultColor) }

    LaunchedEffect(imagePath) {
        containerColor = defaultColor

        if (imagePath != null) {
            val imageUri = Uri.fromFile(File(imagePath))
            val imageLoader = ImageLoader.Builder(context).build()
            val imageRequest = ImageRequest.Builder(context)
                .data(imageUri)
                .allowHardware(false)
                .build()

            val result = try {
                imageLoader.execute(imageRequest)
            } catch (e: Exception) {
                Log.e("WishlistCard", "Error loading image: ${e.message}", e)
                null
            }

            if (result != null && result is SuccessResult) {
                val drawable = result.drawable
                if (drawable is BitmapDrawable) {
                    val entireBitmap = drawable.bitmap
                    val edgesOnlyBitmap = getBitmapFromEdges(entireBitmap)
                    val newColor = generatePalette(edgesOnlyBitmap)
                    if (newColor != null) {
                        containerColor = newColor
                    }
                }
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .then(onClick)
    ) {
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
            modifier = Modifier.size(80.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                if (imagePath != null) {
                    val imageUri = remember { Uri.fromFile(File(imagePath)) }
                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(imageUri)
                            .size(512)
                            .crossfade(true)
                            .build()
                    )
                    val painterState = painter.state

                    when (painterState) {
                        is AsyncImagePainter.State.Empty,
                        is AsyncImagePainter.State.Loading -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.rounded_hourglass_24),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                        is AsyncImagePainter.State.Success -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(containerColor)
                            ) {
                                Image(
                                    painter = painter,
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                )
                            }
                        }
                        is AsyncImagePainter.State.Error -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.rounded_broken_image_24),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                } else {
                    Icon(
                        painter = painterResource(R.drawable.rounded_no_image),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            if (primaryText.isNotBlank()) {
                Text(
                    text = primaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (secondaryText.isNotBlank()) {
                Text(
                    text = secondaryText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (tertiaryText.isNotBlank()) {
                Text(
                    text = tertiaryText,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ItemCardContentImage(
    painter: AsyncImagePainter,
    title: String,
    imageWidth: Dp,
    modifier: Modifier = Modifier
) {
    when (painter.state) {
        is AsyncImagePainter.State.Empty,
        is AsyncImagePainter.State.Loading -> {
            ItemCardContentLoading(
                title = title,
                imageWidth = imageWidth
            )
        }
        is AsyncImagePainter.State.Success -> {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = modifier.width(imageWidth),
                contentScale = ContentScale.FillWidth,
            )
        }
        is AsyncImagePainter.State.Error -> {
            ItemCardContentError(
                title = title,
                imageWidth = imageWidth
            )
        }
    }
}

@Composable
private fun ItemCardContentLoading(
    title: String,
    imageWidth: Dp
) {
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier
            .width(imageWidth)
            .height(80.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                painter = painterResource(R.drawable.rounded_hourglass_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(start = 4.dp, end = 8.dp)
            )
            Text(
                text = "$title...",
                color = MaterialTheme.colorScheme.outlineVariant,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ItemCardContentNoImage(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.rounded_no_image),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = modifier.padding(26.dp)
        )
        Column {
            Text(
                text = title,
            )
            Text(
                text = stringResource(R.string.no_image),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ItemCardContentError(
    title: String,
    imageWidth: Dp,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
            .width(imageWidth)
            .height(80.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                painter = painterResource(R.drawable.rounded_broken_image_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = modifier.padding(26.dp)
            )
            Column {
                Text(
                    text = title,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(R.string.error),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
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
