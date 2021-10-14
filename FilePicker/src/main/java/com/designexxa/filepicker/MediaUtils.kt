package com.designexxa.filepicker

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.net.URI

abstract class MediaUtils : Permissions() {

    private val REQUEST_CODE_CAMERA: Int = 6
    private val REQUEST_CODE_GALLERY: Int = 7

    var imgUri: Uri? = null

    protected fun camera(
        context: Context,
        isSquare: Boolean = false
    ) {
        if (checkPermissionCamera(context)) {

            /*val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            (context as Activity).startActivityForResult(intent, REQUEST_CODE_CAMERA)*/

            // media store
            val values: ContentValues = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "New picture from the camera")

            imgUri =
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
            (context as Activity).startActivityForResult(intent, REQUEST_CODE_CAMERA)
        } else {
            requestPermissionCamera(context)
        }
    }

    protected open fun gallery(
        context: Context,
        imageType: Array<String> = arrayOf(),
        isSquare: Boolean = false
    ) {
        if (checkPermissionManageStorage(context)) {

            val intent: Intent = Intent(Intent.ACTION_OPEN_DOCUMENT).applyImageType(imageType)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            (context as Activity).startActivityForResult(intent, REQUEST_CODE_GALLERY)
        } else {
            requestPermissionManageStorage(context)
        }
    }

    private fun Intent.applyImageType(mimeTypes: Array<String>): Intent {
        type = "image/*"
        if (mimeTypes.isNotEmpty()) {
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        return this
    }

    protected open fun fileManager(
        context: Context
    ) {
        if (checkPermissionManageStorage(context)) {

        } else {
            requestPermissionManageStorage(context)
        }
    }

    // result
    protected fun fileResult(
        context: Context,
        requestCode: Int,
        resultCode: Int,
        intent: Intent?,
        maxSize: Int = 0
    ): ArrayList<FileData> {

        val fileDataList: ArrayList<FileData> = ArrayList()

        var fileUri: Uri? = null
        var filePath: String? = null
        var fileName: String = ""

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK && intent != null) {
            fileUri = intent.data
            filePath = intent.data?.path
            fileName = getFileName(filePath.toString())
        }
        else if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK && intent != null) {
            fileUri = intent.data
            filePath = intent.data?.path
            fileName = getFileName(filePath.toString())
        }

        if (fileName.isNotEmpty() && !filePath.isNullOrEmpty()) {
            Log.e("RRR", fileUri.toString())
            Log.e("RRR", filePath.toString())
            if (maxSize > 0) {
                /*val fileSize = getFileSize(context, fileUri).asKb*/
                val fileSize = getFileSize(filePath).asMb
                if (fileSize < maxSize) {
                    fileDataList.add(
                        FileData(
                            fileName = fileName,
                            filePath = filePath,
                            fileUri = fileUri
                        )
                    )
                } else {
                    toast(context, "File size is more than $maxSize MB")
                }
            } else {
                fileDataList.add(
                    FileData(
                        fileName = fileName,
                        filePath = filePath,
                        fileUri = fileUri
                    )
                )
            }
        }

        return fileDataList
    }
}