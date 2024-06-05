package com.mikohatara.collectioncatalog.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.util.filePathFromUri
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import java.io.File

@Composable
fun ItemImage(imagePath: String?, onInspectImage: () -> Unit) {

    if (imagePath != null) {

        AsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .data(data = File(imagePath))
                .build(),
            contentDescription = null,
            modifier = Modifier
                .clickable { onInspectImage() }
                .fillMaxSize(),
            contentScale = ContentScale.FillWidth
        )

    } else {

        Card(
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    painter = painterResource(R.drawable.rounded_no_image),
                    contentDescription = null
                )
                Text(text = "No image")
            }
        }
    }
}

@Composable
fun pickItemImage(oldImagePath: String?): String? {
    var imageUri: Uri? by remember { mutableStateOf(null) }
    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) imageUri = uri
    }
    val newImagePath: String? = imageUri?.let { filePathFromUri(it, LocalContext.current) }

    if (imageUri != null) {
        Card(
            onClick = {
                photoPicker.launch(PickVisualMediaRequest(
                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                ))
            },
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillWidth
            )
        }
        return newImagePath

    } else if (oldImagePath != null) {
        Card(
            onClick = {
                photoPicker.launch(PickVisualMediaRequest(
                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                ))
            },
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = oldImagePath,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillWidth
            )
        }
        return oldImagePath

    } else {
        Card(
            onClick = {
                photoPicker.launch(PickVisualMediaRequest(
                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                ))
            },
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    painter = painterResource(R.drawable.rounded_add_image),
                    contentDescription = null
                )
                Text(text = "Press here to select an image")
            }
        }
        return null
    }
}

@Composable
fun InspectItemImage(
    imagePath: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (imagePath != null) {
        Box(
            modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Scrim(onBack, Modifier.fillMaxSize())
            ZoomableImage(imagePath)
        }
        IconButton(
            onClick = { onBack() },
            modifier = Modifier
                .padding(start = 4.dp, top = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Clear,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
private fun Scrim(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .pointerInput(onBack) { detectTapGestures { onBack() } }
            .semantics(mergeDescendants = true) {
                onClick {
                    onBack()
                    true
                }
            }
            .onKeyEvent {
                if (it.key == Key.Escape) {
                    onBack()
                    true
                } else {
                    false
                }
            }
            .background(Color(0, 0, 0, 230))
    )
}

@Composable
private fun ZoomableImage(
    imagePath: String
) {
    val zoomState = rememberZoomState()

    AsyncImage(
        model = ImageRequest
            .Builder(LocalContext.current)
            .data(data = File(imagePath))
            .build(),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        onSuccess = { state ->
            zoomState.setContentSize(state.painter.intrinsicSize)
        },
        modifier = Modifier
            .fillMaxSize()
            .zoomable(zoomState = zoomState)
    )
}
