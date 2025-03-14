package com.mikohatara.collectioncatalog.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mikohatara.collectioncatalog.R
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import java.io.File

@Composable
fun ItemImage(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageUri: Uri? = null,
    imagePath: String? = null,
    isEditMode: Boolean = false
) {
    val maxHeight = LocalConfiguration.current.screenWidthDp * 0.75
    val colors = CardDefaults.cardColors(
        if (imageUri != null || imagePath != null) {
            Color(185, 185, 185) // TODO replace with a dynamic background color
        } else {
            MaterialTheme.colorScheme.surface
        }
    )

    Card(
        onClick = onClick,
        colors = colors,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        if (imageUri != null || imagePath != null) {
            AsyncImage(
                model = ImageRequest
                    .Builder(LocalContext.current)
                    .data(imageUri ?: imagePath)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .heightIn(max = maxHeight.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
        } else {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                if (isEditMode) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_add_image),
                        contentDescription = null
                    )
                    Text(stringResource(R.string.add_image))
                } else {
                    Icon(
                        painter = painterResource(R.drawable.rounded_no_image),
                        contentDescription = null
                    )
                    Text(stringResource(R.string.no_image))
                }
            }
        }
    }
}

@Composable
fun pickItemImage(
    existingImagePath: String?,
    modifier: Modifier = Modifier,
    temporaryImageUri: Uri? = null,
    onPick: (Uri?) -> Unit = {},
    onRemove: () -> Unit = {}
): String? {
    var removeTemporaryImage by remember { mutableStateOf(false) }
    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()) { uri ->
            onPick(uri)
    }
    val launchPicker = { photoPicker.launch(PickVisualMediaRequest(
        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)) }
    val toggleRemove = { removeTemporaryImage = true }

    if (removeTemporaryImage) {
        onRemove()
        removeTemporaryImage = false
    }

    if (temporaryImageUri != null) {
        ItemEntryImageFrame(
            toggleRemove,
            modifier
        ) {
            ItemImage(
                onClick = { launchPicker() },
                imageUri = temporaryImageUri,
                modifier = Modifier.padding(4.dp)
            )
        }
        return null

    } else if (existingImagePath != null) {
        ItemEntryImageFrame(
            toggleRemove,
            modifier
        ) {
            ItemImage(
                onClick = { launchPicker() },
                imagePath = existingImagePath,
                modifier = Modifier.padding(4.dp)
            )
        }
        return existingImagePath

    } else {
        ItemEntryImageFrame(
            toggleRemove,
            modifier,
            hasExistingImage = false
        ) {
            ItemImage(
                onClick = { launchPicker() },
                isEditMode = true,
                modifier = Modifier.padding(4.dp)
            )
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
                .statusBarsPadding()
                .padding(start = 4.dp, top = 8.dp)
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
            .background(Color(0, 0, 0, 243))
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
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        filterQuality = FilterQuality.None,
        onSuccess = { state ->
            zoomState.setContentSize(state.painter.intrinsicSize)
        },
        modifier = Modifier
            .fillMaxSize()
            .zoomable(zoomState = zoomState)
    )
}

@Composable
private fun ItemEntryImageFrame(
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    hasExistingImage: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.padding(top = 4.dp)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(Color(0, 0, 0, 0)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
        RemovalButton(onRemove, hasExistingImage)
    }
}

@Composable
private fun RemovalButton(
    onClick: () -> Unit,
    hasExistingImage: Boolean
) {
    if (hasExistingImage) {
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = (-2).dp, y = (-8).dp)
        ) {
            FilledTonalIconButton(
                onClick = onClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Clear,
                    contentDescription = null
                )
            }
        }
    }
}
