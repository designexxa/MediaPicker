package com.designexxa.filepicker

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore

abstract class MediaUtils : Utils() {

    private val REQUEST_CODE_GALLERY: Int = 6

    protected fun camera(
        context: Context,
        isSquare: Boolean = false
    ) {
        if (checkPermissionCamera(context)) {
            val values: ContentValues = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "New picture from the camera")

            val imgUri: Uri? =
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, REQUEST_CODE_GALLERY)
            (context as Activity).startActivityForResult(intent, REQUEST_CODE_GALLERY)
        } else {
            requestPermissionCamera(context)
        }
    }

    protected open fun gallery(
        context: Context,
        isSquare: Boolean = false
    ) {
        if (checkPermissionManageStorage(context)) {

        } else {
            requestPermissionManageStorage(context)
        }
    }

    protected open fun fileManager(
        context: Context
    ) {
        if (checkPermissionManageStorage(context)) {

        } else {
            requestPermissionManageStorage(context)
        }
    }
}