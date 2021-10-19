package com.designexxa.filepicker

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.net.URI

abstract class MediaUtils : Permissions() {

    private val REQUEST_CODE_CAMERA: Int = 6
    private val REQUEST_CODE_GALLERY: Int = 7
    private val REQUEST_CODE_FILE: Int = 8

    private var imgUri: Uri? = null

    protected fun camera(
        context: Context,
        isSquare: Boolean = false
    ) {
        if (checkPermissionCamera(context)) {

            /*// Get File Directory
            val bundle = (context as Activity).intent.extras ?: Bundle()
            val fileDir = bundle.getString("extras.save_directory")
            val mFileDir = getFileDir(context, fileDir)
            val file = getImageFile(fileDir = mFileDir)

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val authority = authorityProvider
                val photoURI = FileProvider.getUriForFile(context, authority, file!!)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
            }
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
        imageType: Array<String> = emptyArray(),
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

    protected open fun fileManager(
        context: Context,
        fileType: Array<String> = emptyArray(),
    ) {
        if (checkPermissionManageStorage(context)) {
            val intent: Intent = Intent(Intent.ACTION_OPEN_DOCUMENT).applyFileType(fileType)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            (context as Activity).startActivityForResult(intent, REQUEST_CODE_FILE)
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

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            fileUri = imgUri
        } else if ((requestCode == REQUEST_CODE_GALLERY || requestCode == REQUEST_CODE_FILE)
            && resultCode == Activity.RESULT_OK && intent != null
        ) {
            fileUri = intent.data
        }

        if (fileUri != null) {
            val filePath = getFilePath(context, fileUri)
            val fileName = getFileName(filePath)
            if (!filePath.isNullOrEmpty() && fileName.isNotEmpty()) {
                if (maxSize > 0) {
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
        }

        return fileDataList
    }
}