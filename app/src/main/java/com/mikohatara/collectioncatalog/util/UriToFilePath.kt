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
