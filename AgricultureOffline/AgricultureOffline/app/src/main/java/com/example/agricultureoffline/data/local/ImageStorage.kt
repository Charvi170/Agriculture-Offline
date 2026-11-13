package com.example.agricultureoffline.data.local

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import java.io.File

object ImageStorage {
    private const val TAG = "ImageStorage"

    fun saveBitmap(
        context: Context,
        bitmap: Bitmap
    ): String? {
        return try {
            val file = File(
                context.filesDir,
                "leaf_${System.currentTimeMillis()}.jpg"
            )

            file.outputStream().use { outputStream ->
                bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    90,
                    outputStream
                )
            }

            file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save bitmap", e)
            null
        }
    }

    fun saveUri(
        context: Context,
        uri: Uri
    ): String? {
        return try {
            val file = File(
                context.filesDir,
                "leaf_${System.currentTimeMillis()}.jpg"
            )

            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return null

            file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save uri: $uri", e)
            null
        }
    }
}
