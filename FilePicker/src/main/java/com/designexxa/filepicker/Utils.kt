package com.designexxa.filepicker

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

abstract class Utils {

    protected val packageName: String = "com.designexxa.filepicker"
    protected val authorityProvider: String = "${packageName}.provider"

    protected fun toast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    protected fun getFileName(fileString: String?, withExtension: Boolean = true): String {
        if (withExtension) {
            return if (!fileString.isNullOrEmpty()) {
                if (fileString.toString().contains("/")) {
                    (fileString.toString()
                        .substring(fileString.toString().lastIndexOf("/"))).removePrefix(
                            "/"
                        ).filter { !it.isWhitespace() }
                } else {
                    fileString.toString().filter { !it.isWhitespace() }
                }
            } else {
                ""
            }
        } else {
            return if (!fileString.isNullOrEmpty()) {
                if (fileString.toString().contains("/")) {
                    ((fileString.toString()
                        .substring(fileString.toString().lastIndexOf("/"))).removePrefix(
                            "/"
                        )).removeSuffix(
                            getFileExtension(fileString)
                        ).filter { !it.isWhitespace() }
                } else {
                    fileString.toString().removeSuffix(getFileExtension(fileString))
                        .filter { !it.isWhitespace() }
                }
            } else {
                ""
            }
        }
    }

    protected fun getFileExtension(fileString: String?, withDot: Boolean = true): String {
        if (withDot) {
            return if (!fileString.isNullOrEmpty()) {
                if (fileString.toString().contains(".")) {
                    fileString.toString().substring(fileString.toString().lastIndexOf("."))
                } else {
                    ".${fileString.toString()}"
                }
            } else {
                ""
            }
        } else {
            return if (!fileString.isNullOrEmpty()) {
                if (fileString.toString().contains(".")) {
                    (fileString.toString()
                        .substring(fileString.toString().lastIndexOf("."))).removePrefix(
                            "."
                        )
                } else {
                    fileString.toString()
                }
            } else {
                ""
            }
        }
    }

    protected val Long.asKb get() = this.toFloat() / 1024
    protected val Long.asMb get() = asKb / 1024
    protected val Long.asGb get() = asMb / 1024

    protected fun getFileSize(filePath: String?): Long {
        var size: Long = 0
        if (!filePath.isNullOrEmpty()) {
            size = File(filePath).length()
        }
        return size
    }

    protected fun getFileSize(context: Context, uri: Uri?): Long {
        var size: Long = 0
        if (uri != null) {
            try {
                val descriptor = context.contentResolver.openAssetFileDescriptor(uri, "r")
                size = descriptor?.length ?: return 0
                descriptor.close()
            } catch (e: Resources.NotFoundException) {
            }
        }
        return size
    }

    // FileUriUtils
    protected fun getFilePath(context: Context, uri: Uri): String? {
        val path = getPathFromLocalUri(context, uri)
        /*if (path == null) {
            path = getPathFromRemoteUri(context, uri)
        }*/
        return path
    }

    protected fun getPathFromLocalUri(context: Context, uri: Uri): String? {

        // DocumentProvider
        when {
            DocumentsContract.isDocumentUri(context, uri) -> {
                // ExternalStorageProvider
                when {
                    isExternalStorageDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val type = split[0]

                        // This is for checking Main Memory
                        return if ("primary".equals(type, ignoreCase = true)) {
                            if (split.size > 1) {
                                Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                            } else {
                                Environment.getExternalStorageDirectory().toString() + "/"
                            }
                            // This is for checking SD Card
                        } else {
                            val path = "storage" + "/" + docId.replace(":", "/")
                            if (File(path).exists()) {
                                path
                            } else {
                                "/storage/sdcard/" + split[1]
                            }
                        }
                    }
                    isDownloadsDocument(uri) -> {
                        return getDownloadDocument(context, uri)
                    }
                    isMediaDocument(uri) -> {
                        return getMediaDocument(context, uri)
                    }
                }
            }
            "content".equals(uri.scheme!!, ignoreCase = true) -> {
                // Return the remote address
                return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                    context,
                    uri,
                    null,
                    null
                )
            }
            "file".equals(uri.scheme!!, ignoreCase = true) -> {
                return uri.path
            }
        }
        return null
    }

    protected fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }

    protected fun getDownloadDocument(context: Context, uri: Uri): String? {
        val fileName = getFilePathName(context, uri)
        if (fileName != null) {
            val path =
                Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName
            if (File(path).exists()) {
                return path
            }
        }

        var id = DocumentsContract.getDocumentId(uri)
        if (id.contains(":")) {
            id = id.split(":")[1]
        }
        val contentUri = ContentUris.withAppendedId(
            Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
        )
        return getDataColumn(context, contentUri, null, null)
    }

    protected fun getMediaDocument(context: Context, uri: Uri): String? {
        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val type = split[0]

        var contentUri: Uri? = null
        when (type) {
            "image" -> {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            "video" -> {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }
            "audio" -> {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
        }

        val selection = "_id=?"
        val selectionArgs = arrayOf(split[1])

        return getDataColumn(context, contentUri, selection, selectionArgs)
    }

    protected fun getFilePathName(context: Context, uri: Uri): String? {

        var cursor: Cursor? = null
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)

        try {
            cursor = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    protected fun getPathFromRemoteUri(context: Context, uri: Uri): String? {
        // The code below is why Java now has try-with-resources and the Files utility.
        var file: File? = null
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        var success = false
        try {
            val extension = getImageExtension(uri)
            inputStream = context.contentResolver.openInputStream(uri)
            file = getImageFile(context.cacheDir, extension)
            if (file == null) return null
            outputStream = FileOutputStream(file)
            if (inputStream != null) {
                inputStream.copyTo(outputStream, bufferSize = 4 * 1024)
                success = true
            }
        } catch (ignored: IOException) {
        } finally {
            try {
                inputStream?.close()
            } catch (ignored: IOException) {
            }

            try {
                outputStream?.close()
            } catch (ignored: IOException) {
                // If closing the output stream fails, we cannot be sure that the
                // target file was written in full. Flushing the stream merely moves
                // the bytes into the OS, not necessarily to the file.
                success = false
            }
        }
        return if (success) file!!.path else null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    // FileUtils
    protected fun getImageFile(fileDir: File, extension: String? = null): File? {
        try {
            // Create an image file name
            val ext = extension ?: ".jpg"
            val fileName = getCreatedFileName()
            val imageFileName = "$fileName$ext"

            // Create Directory If not exist
            if (!fileDir.exists()) fileDir.mkdirs()

            // Create File Object
            val file = File(fileDir, imageFileName)

            // Create empty file
            file.createNewFile()

            return file
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
    }

    private fun getCreatedFileName() = "IMG_${getTimestamp()}"
    // private fun getFileName() = "IMAGE_PICKER"

    /**
     * Get Current Time in yyyyMMdd HHmmssSSS format
     *
     * 2019/01/30 10:30:20 000
     * E.g. 20190130_103020000
     */
    private fun getTimestamp(): String {
        val timeFormat = "yyyyMMdd_HHmmssSSS"
        return SimpleDateFormat(timeFormat, Locale.getDefault()).format(Date())
    }

    /**
     * Get Free Space size
     * @param file directory object to check free space.
     */
    private fun getFreeSpace(file: File): Long {
        val stat = StatFs(file.path)
        val availBlocks = stat.availableBlocksLong
        val blockSize = stat.blockSizeLong
        return availBlocks * blockSize
    }

    /**
     * Get Image Width & Height from Uri
     *
     * @param uri Uri to get Image Size
     * @return Int Array, Index 0 has width and Index 1 has height
     */
    private fun getImageResolution(context: Context, uri: Uri): Pair<Int, Int> {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val stream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(stream, null, options)
        return Pair(options.outWidth, options.outHeight)
    }

    /**
     * Get Image Width & Height from File
     *
     * @param file File to get Image Size
     * @return Int Array, Index 0 has width and Index 1 has height
     */
    private fun getImageResolution(file: File): Pair<Int, Int> {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.absolutePath, options)
        return Pair(options.outWidth, options.outHeight)
    }

    /**
     * Get Image File Size
     *
     * @param uri Uri to get Image Size
     * @return Int Image File Size
     */
    private fun getImageSize(context: Context, uri: Uri): Long {
        return getDocumentFile(context, uri)?.length() ?: 0
    }

    /**
     * Create copy of Uri into application specific local path
     *
     * @param context Application Context
     * @param uri Source Uri
     * @return File return copy of Uri object
     */
    private fun getTempFile(context: Context, uri: Uri): File? {
        try {
            val destination = File(context.cacheDir, "image_picker.png")

            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor ?: return null

            val src = FileInputStream(fileDescriptor).channel
            val dst = FileOutputStream(destination).channel
            dst.transferFrom(src, 0, src.size())
            src.close()
            dst.close()

            return destination
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return null
    }

    /**
     * Get DocumentFile from Uri
     *
     * @param context Application Context
     * @param uri Source Uri
     * @return DocumentFile return DocumentFile from Uri
     */
    private fun getDocumentFile(context: Context, uri: Uri): DocumentFile? {
        var file: DocumentFile? = null
        if (isFileUri(uri)) {
            val path = getFilePath(context, uri)
            if (path != null) {
                file = DocumentFile.fromFile(File(path))
            }
        } else {
            file = DocumentFile.fromSingleUri(context, uri)
        }
        return file
    }

    /**
     * Get Bitmap Compress Format
     *
     * @param extension Image File Extension
     * @return Bitmap CompressFormat
     */
    @Suppress("DEPRECATION")
    private fun getCompressFormat(extension: String): Bitmap.CompressFormat {
        return when {
            extension.contains("png", ignoreCase = true) -> Bitmap.CompressFormat.PNG
            extension.contains("webp", ignoreCase = true) -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Bitmap.CompressFormat.WEBP_LOSSLESS
                } else {
                    Bitmap.CompressFormat.WEBP
                }
            }
            else -> Bitmap.CompressFormat.JPEG
        }
    }

    /**
     * Get Image Extension i.e. .png, .jpg
     *
     * @return extension of image with dot, or default .jpg if it none.
     */
    private fun getImageExtension(file: File): String {
        return getImageExtension(Uri.fromFile(file))
    }

    /**
     * Get Image Extension i.e. .png, .jpg
     *
     * @return extension of image with dot, or default .jpg if it none.
     */
    private fun getImageExtension(uriImage: Uri): String {
        var extension: String? = null

        try {
            val imagePath = uriImage.path
            if (imagePath != null && imagePath.lastIndexOf(".") != -1) {
                extension = imagePath.substring(imagePath.lastIndexOf(".") + 1)
            }
        } catch (e: Exception) {
            extension = null
        }

        if (extension == null || extension.isEmpty()) {
            // default extension for matches the previous behavior of the plugin
            extension = "jpg"
        }

        return ".$extension"
    }

    /**
     * Check if provided URI is backed by File
     *
     * @return Boolean, True if Uri is local file object else return false
     */
    private fun isFileUri(uri: Uri): Boolean {
        return "file".equals(uri.scheme, ignoreCase = true)
    }

    // BaseProvider
    protected fun getFileDir(context: Context, path: String?): File {
        return if (path != null) File(path)
        else context.getExternalFilesDir(Environment.DIRECTORY_DCIM) ?: (context as Activity).filesDir
    }

    // Intent
    protected fun Intent.applyImageType(mimeTypes: Array<String>): Intent {
        type = "image/*"
        if (mimeTypes.isNotEmpty()) {
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        return this
    }

    protected fun Intent.applyFileType(mimeTypes: Array<String>): Intent {
        type = "*/*"
        if (mimeTypes.isNotEmpty()) {
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        return this
    }
}