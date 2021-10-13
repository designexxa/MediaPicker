package com.designexxa.filepicker

import android.content.Context

object FilePicker : MediaUtils() {

    fun openCamera(context: Context, isSquare: Boolean = false) {
        camera(context = context, isSquare = isSquare)
    }

    fun openGallery(context: Context, isSquare: Boolean = false) {
        gallery(context = context, isSquare = isSquare)
    }

    fun openFileManager(context: Context) {
        fileManager(context = context)
    }
}