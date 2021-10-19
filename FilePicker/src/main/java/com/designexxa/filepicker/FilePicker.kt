package com.designexxa.filepicker

import android.content.Context
import android.content.Intent

class FilePicker {

    companion object {
        fun openCamera(context: Context, isSquare: Boolean = false) {
            val mediaUtils: MediaUtils = MediaUtils()
            mediaUtils.camera(context = context, isSquare = isSquare)
        }

        fun openGallery(context: Context, isSquare: Boolean = false) {
            val mediaUtils: MediaUtils = MediaUtils()
            mediaUtils.gallery(context = context, isSquare = isSquare)
        }

        fun openFileManager(context: Context) {
            val mediaUtils: MediaUtils = MediaUtils()
            mediaUtils.fileManager(context = context)
        }

        fun getFileResult(
            context: Context,
            requestCode: Int,
            resultCode: Int,
            intent: Intent?,
            maxSize: Int = 0
        ): ArrayList<FileData> {
            val mediaUtils: MediaUtils = MediaUtils()
            return mediaUtils.fileResult(
                context = context,
                requestCode = requestCode,
                resultCode = resultCode,
                intent = intent,
                maxSize = maxSize
            )
        }
    }
}