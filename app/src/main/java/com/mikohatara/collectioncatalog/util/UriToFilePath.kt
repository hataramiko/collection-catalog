package com.mikohatara.collectioncatalog.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun Uri.toFilePath(context: Context): String? {
    val cursor = context.contentResolver.query(this, null, null, null, null)

    cursor?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

        if (nameIndex == -1 || !cursor.moveToFirst()) return null

        val name = cursor.getString(nameIndex)
        val sanitizedName = name
            ?.substringAfterLast('/')
            ?.substringAfterLast('\\')
            ?.takeIf { it.isNotBlank() && it != "." && it != ".." }
            ?: "temp_file_${System.currentTimeMillis()}"
        val file = File(context.filesDir, sanitizedName)

        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(this)

            inputStream?.use { input ->
                FileOutputStream(file).use { outputStream ->
                    input.copyTo(outputStream)
                }
            }
            return file.path

        } catch (e: Exception) {
            Log.e("Uri.toFilePath", e.message, e)
        }
    }
    return null
}
