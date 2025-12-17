package com.mikohatara.collectioncatalog.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
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
    ItemCardContent(
        title = title,
        imagePath = imagePath,
        itemWidth = itemWidth,
        maxWidth = maxWidth,
        onCardClick = onClick
    )
}

@Composable
private fun ItemCardContent(
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

    Box(
        modifier = modifier
            .clip(CardDefaults.shape)
            .then(onClick)
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
            ItemCardImage(painter = painter, title = title, imageWidth = imageWidth.dp)
        } else {
            ItemCardNoImage(title = title, width = imageWidth.dp)
        }
    }
}

@Composable
private fun ItemCardImage(
    painter: AsyncImagePainter,
    title: String,
    imageWidth: Dp,
    modifier: Modifier = Modifier
) {
    when (painter.state) {
        is AsyncImagePainter.State.Empty,
        is AsyncImagePainter.State.Loading -> {
            ItemCardImageLoading(
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
            ItemCardImageError(
                title = title,
                imageWidth = imageWidth
            )
        }
    }
}

@Composable
private fun ItemCardImageLoading(
    title: String,
    imageWidth: Dp
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(colorScheme.surfaceContainerLow),
        modifier = Modifier
            .width(imageWidth)
            .height(96.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 4.dp, end = 8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.rounded_hourglass_24),
                contentDescription = null,
                tint = colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$title…",
                color = colorScheme.outlineVariant,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ItemCardNoImage(
    title: String,
    width: Dp,
    modifier: Modifier = Modifier
) {
    // New centered implementation for testing
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(colorScheme.surfaceContainerHigh),
        modifier = modifier
            .width(width)
            .height(96.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.rounded_no_image),
                contentDescription = null,
                tint = colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }

    /* Original implementation refactored
    Card(
        colors = CardDefaults.cardColors(colorScheme.surfaceContainer),
        modifier = modifier
            .fillMaxWidth()
            .height(88.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                painter = painterResource(R.drawable.rounded_no_image),
                contentDescription = null,
                tint = colorScheme.secondary,
                modifier = modifier.padding(24.dp)
            )
            Column {
                Text(
                    text = title,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(R.string.no_image),
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        }
    }*/

    /* Original implementation
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.rounded_no_image),
            contentDescription = null,
            tint = colorScheme.secondary,
            modifier = modifier.padding(26.dp)
        )
        Column {
            Text(
                text = title
            )
            Text(
                text = stringResource(R.string.no_image),
                color = colorScheme.onSurfaceVariant
            )
        }
    }*/
}

@Composable
private fun ItemCardImageError(
    title: String,
    imageWidth: Dp,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(colorScheme.surfaceContainer),
        modifier = modifier
            .width(imageWidth)
            .height(96.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.rounded_broken_image_24),
                contentDescription = null,
                tint = colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.error),
                    color = colorScheme.onErrorContainer,
                    fontSize = 14.sp,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
