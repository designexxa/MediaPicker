package com.designexxa.filepicker

import android.content.Context
import android.content.Intent

object FilePicker : MediaUtils() {

    fun openCamera(context: Context, isSquare: Boolean = false) {
        camera(context = context, isSquare = isSquare)
    }

    fun openGallery(context: Context, isSquare: Boolean = false) {
        gallery(context = context, isSquare = isSquare)
    }

    fun openFileManager(context: Context) {
        //val fileType = arrayOf("video/*", "image/*", "application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        fileManager(context = context)
    }

    fun getFileResult(
        context: Context,
        requestCode: Int,
        resultCode: Int,
        intent: Intent?,
        maxSize: Int = 0
    ): ArrayList<FileData> {
        return fileResult(
            context = context,
            requestCode = requestCode,
            resultCode = resultCode,
            intent = intent,
            maxSize = maxSize
        )
    }
}