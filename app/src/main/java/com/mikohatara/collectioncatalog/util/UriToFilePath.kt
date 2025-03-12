package com.mikohatara.collectioncatalog.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun filePathFromUri(uri: Uri, context: Context): String? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)

    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        it.moveToFirst()
        val name = it.getString(nameIndex)
        val file = File(context.filesDir, name)

        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

            inputStream?.use { input ->
                FileOutputStream(file).use { outputStream ->
                    input.copyTo(outputStream)
                }
            }
            return file.path

        } catch (e: Exception) {
            Log.d("Exception", e.message.toString())
        }
    }
    return null
}
