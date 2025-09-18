package me.rajesh.expensetracker.ui.fragments.expense_detail

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageHandler(private val context: Context) {

    fun copyUriToAppStorage(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "expense_${System.currentTimeMillis()}.jpg"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)

            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // Compress the saved image
            compressImage(file.absolutePath)
        } catch (e: Exception) {
            Log.e("ImageHandler", "Error copying URI to app storage", e)
            null
        }
    }

    private fun compressImage(imagePath: String): String {
        return try {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            val file = File(imagePath)

            file.outputStream().use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
            }

            imagePath
        } catch (e: Exception) {
            Log.e("ImageHandler", "Error compressing image", e)
            imagePath
        }
    }

    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("EXPENSE_${timeStamp}_", ".jpg", storageDir)
    }

    fun deleteImage(imagePath: String?) {
        imagePath?.let {
            val file = File(it)
            if (file.exists()) {
                file.delete()
            }
        }
    }
}