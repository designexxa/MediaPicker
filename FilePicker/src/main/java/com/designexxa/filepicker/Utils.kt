package com.designexxa.filepicker

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.widget.Toast
import java.io.File

abstract class Utils {

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
}