package com.mikohatara.collectioncatalog.ui.components

import android.content.ContentResolver
import android.net.Uri
import android.os.FileUtils
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.mikohatara.collectioncatalog.ui.item.AddItemUiState
import java.io.File
import java.io.FileOutputStream

@Composable
fun ItemImage(uiState: AddItemUiState): String? {

    var imageUri: Uri? by remember { mutableStateOf(null) }
    Log.d("imageUri", imageUri.toString())

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()) { uri ->
        imageUri = uri
    }

    val contentResolver = LocalContext.current.contentResolver
    val inputStream = imageUri?.let { contentResolver.openInputStream(it) }
    val imageFile = File(LocalContext.current.cacheDir, "temp_file.jpg")
    imageFile.outputStream().use { outputStream ->
        inputStream?.copyTo(outputStream)
    }

    //val imageFile = imageUri?.toFile()

    val imagePath = imageFile.absolutePath
    Log.d("imagePath", imagePath.toString())

    ///////////////////////////////

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

        uiState.newItemDetails.copy(imagePath = imagePath)

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