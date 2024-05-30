package com.mikohatara.collectioncatalog.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mikohatara.collectioncatalog.util.filePathFromUri

@Composable
fun pickItemImage(): String? {

    var imageUri: Uri? by remember { mutableStateOf(null) }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()) { uri ->
        imageUri = uri
    }
    val imagePath: String? = imageUri?.let { filePathFromUri(it, LocalContext.current) }

    /*if (imageUri != null && isInEditMode == true) {
        /*val painter = rememberAsyncImagePainter(
            ImageRequest
                .Builder(LocalContext.current)
                .data(data = imageUri)
                .build()
        )*/

        AsyncImage(
            model = imageUri,
            contentDescription = null,
            //modifier = Modifier.fillMaxWidth(),
            //contentScale = ContentScale.FillWidth
        )
    } else */if (imageUri != null) {

        AsyncImage(
            model = imageUri,
            contentDescription = null,
            //modifier = Modifier.fillMaxWidth(),
            //contentScale = ContentScale.FillWidth
        )
        return imagePath
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(imageVector = Icons.Rounded.Clear, contentDescription = null)
                Text(text = "Press here to add an image")
            }
        }
        return null
    }
}
