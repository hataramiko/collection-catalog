package com.mikohatara.collectioncatalog.ui.components

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val context = LocalContext.current
    val onClickModifier = remember(onClick) { Modifier.clickable { onClick() } }

    val period = if (periodStart == null && periodEnd == null) {
        null
    } else {
        "${periodStart ?: ""}–${periodEnd ?: ""}"
    }

    WishlistCardContent(
        context = context,
        country = country,
        onClickModifier = onClickModifier,
        imagePath = imagePath,
        regNo = regNo,
        notes = notes,
        region1st = region1st,
        region2nd = region2nd,
        region3rd = region3rd,
        type = type,
        period = period,
        year = year?.toString()
    )
}

@Composable
private fun WishlistCardContent(
    context: Context,
    onClickModifier: Modifier,
    modifier: Modifier = Modifier,
    imagePath: String? = null,
    regNo: String? = null,
    notes: String? = null,
    country: String? = null,
    region1st: String? = null,
    region2nd: String? = null,
    region3rd: String? = null,
    type: String? = null,
    period: String? = null,
    year: String? = null,
) {
    val primaryText = listOfNotNull(country, region1st, region2nd, region3rd)
        .filterNot { it.isBlank() }
        .joinToString(", ")
    val secondaryText = listOfNotNull(type, period, year)
        .filterNot { it.isBlank() }
        .joinToString("・")
    val tertiaryText = listOfNotNull(regNo, notes)
        .filterNot { it.isBlank() }
        .joinToString("・")

    Card(
        colors = CardDefaults.cardColors(colorScheme.surfaceContainerLow),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .then(onClickModifier)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Card(
                modifier = Modifier.size(80.dp)
            ) {
                if (imagePath != null) {
                    val containerColor = getImageContainerColor(context, imagePath)
                    val imageUri = remember { Uri.fromFile(File(imagePath)) }
                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(imageUri)
                            .size(512)
                            .crossfade(true)
                            .build()
                    )
                    WishlistCardImage(painter = painter, containerColor = containerColor)
                } else {
                    WishlistCardNoImage()
                }
            }
            Column(
                modifier = Modifier.heightIn(max = 88.dp).padding(start = 12.dp, end = 16.dp)
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
                        color = colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (tertiaryText.isNotBlank()) {
                    Text(
                        text = tertiaryText,
                        color = colorScheme.outline,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun WishlistCardImage(
    painter: AsyncImagePainter,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    when (painter.state) {
        is AsyncImagePainter.State.Empty,
        is AsyncImagePainter.State.Loading -> {
            WishlistCardImageLoading()
        }
        is AsyncImagePainter.State.Success -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.fillMaxSize().background(containerColor)
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                )
            }
        }
        is AsyncImagePainter.State.Error -> {
            WishlistCardImageError()
        }
    }
}

@Composable
private fun WishlistCardImageLoading(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize().background(colorScheme.surfaceContainerLow)
    ) {
        Icon(
            painter = painterResource(R.drawable.rounded_hourglass_24),
            contentDescription = null,
            tint = colorScheme.outlineVariant
        )
    }
}

@Composable
private fun WishlistCardNoImage(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize().background(colorScheme.surfaceContainerHigh)
    ) {
        Icon(
            painter = painterResource(R.drawable.rounded_no_image),
            contentDescription = null,
            tint = colorScheme.secondary
        )
    }
}

@Composable
private fun WishlistCardImageError(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize().background(colorScheme.surfaceContainer)
    ) {
        Icon(
            painter = painterResource(R.drawable.rounded_broken_image_24),
            contentDescription = null,
            tint = colorScheme.outlineVariant
        )
    }
}

@Composable
private fun getImageContainerColor(
    context: Context,
    imagePath: String?
): Color {
    val defaultColor = colorScheme.surfaceContainerHigh
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
    return containerColor
}
