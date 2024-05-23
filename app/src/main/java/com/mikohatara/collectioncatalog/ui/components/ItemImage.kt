package com.mikohatara.collectioncatalog.ui.components

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.FileUtils
import android.provider.MediaStore
import android.provider.OpenableColumns
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
import androidx.loader.content.CursorLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.mikohatara.collectioncatalog.ui.item.AddItemUiState
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun ItemImage(uiState: AddItemUiState): String? {

    var imageUri: Uri? by remember { mutableStateOf(null) }
    Log.d("imageUri", imageUri.toString())

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()) { uri ->
        imageUri = uri
    }

    /* TOIMII siten että viimesin valittu kuva näkyy kaikille jotka on luotu
    val contentResolver = LocalContext.current.contentResolver
    val inputStream = imageUri?.let { contentResolver.openInputStream(it) }
    val imageFile = File(LocalContext.current.cacheDir, "temp_file.jpg")
    imageFile.outputStream().use { outputStream ->
        inputStream?.copyTo(outputStream)
    }*/

    val imagePath: String? = imageUri?.let { getFilePathFromUri(it, LocalContext.current) }

    /*val tempImagePath: String? = imageUri?.let { getFilePathFromUri(it, LocalContext.current) }

    val imagePath = tempImagePath?.let { File(it).absolutePath }*/

    //val imagePath = imageFile.absolutePath
    //val imagePath = imageUri?.path

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

fun getFilePathFromUri(uri: Uri, context: Context): String? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    val nameIndex = cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
    cursor.moveToFirst()
    val name = cursor.getString(nameIndex)
    val size = cursor.getLong(sizeIndex).toString()
    val file = File(context.filesDir, name)
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        var read = 0
        val maxBufferSize = 1 * 1024 * 1024
        val bytesAvailable: Int = inputStream?.available() ?: 0
        val bufferSize = Math.min(bytesAvailable, maxBufferSize)
        val buffers = ByteArray(bufferSize)
        while (inputStream?.read(buffers).also {
            if (it != null) {
                read = it
            }
        } != -1) {
            outputStream.write(buffers, 0, read)
        }
        Log.d("File Size", "Size " + file.length())
        inputStream?.close()
        outputStream.close()
        Log.d("File Path", "Path " + file.path)
    } catch (e: java.lang.Exception) {
        Log.d("Exception", e.message.toString())
    }
    return file.path
}

/* TOIMII mut ei enää sen jälkee ku käynnistää uudestaa
fun getFilePathFromUri(uri: Uri, context: Context): String? {
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val loader = CursorLoader(context, uri, proj, null, null, null)
    val cursor: Cursor? = loader.loadInBackground()
    val columnIndex: Int = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA) ?: return null
    cursor.moveToFirst()
    val result: String? = cursor.getString(columnIndex)
    cursor.close()
    return result
}*/

/*
fun Uri.toFile(contentResolver: ContentResolver): File? {
    /*return contentResolver.query(this, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val displayName = cursor.getString(columnIndex)
            contentResolver.openFileDescriptor(this, "r")?.fileDescriptor?.let { cacheDir ->
                val tempFile = File(cacheDir.absolutePath, displayName)
                with(tempFile) {
                    contentResolver.openInputStream(this@toFile)?.copyTo(outputStream())
                }
                return tempFile
            }
        }
        null
    }*/
    val cursor = contentResolver.query(this,null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val displayName = it.getString(columnIndex)
            val cacheDir = contentResolver.openFileDescriptor(this, "r")?.fileDescriptor
            cacheDir?.let {
                val tempFile = File(it.absolutePath, displayName)
                contentResolver.openInputStream(this)?.use { inputStream ->
                    tempFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                return tempFile
            }
        }
    }
    return null
}*/