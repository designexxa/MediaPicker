package com.designexxa.filepicker

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat

abstract class Utils {

    private val PERMISSION_MANAGE_STORAGE_REQUEST_CODE: Int = 2
    private val PERMISSION_STORAGE_REQUEST_CODE: Int = 3
    private val PERMISSION_CAMERA_REQUEST_CODE: Int = 4
    private val PERMISSION_GALLERY_REQUEST_CODE: Int = 5

    private val MSG_PERMISSION_GRANTED: String = "Permission granted"
    private val MSG_PERMISSION_DENIED: String = "Permission denied"

    protected fun toast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    //----------------------------------- permission : START  ------------------------------------//

    // check permissions
    protected fun checkPermissionManageStorage(context: Context): Boolean {
        /*return if (SDK_INT >= Build.VERSION_CODES.R) { // R is Android 11
            Environment.isExternalStorageManager()
        } else {
            (ActivityCompat.checkSelfPermission(
                (context as Activity),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                (context as Activity),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED)
        }*/

        return (ActivityCompat.checkSelfPermission(
            (context as Activity),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            (context as Activity),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    protected fun checkPermissionCamera(context: Context): Boolean {
        return (ActivityCompat.checkSelfPermission(
            (context as Activity),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            (context as Activity),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    // request permissions
    protected fun requestPermissionManageStorage(context: Context) {
        /*if (SDK_INT >= Build.VERSION_CODES.R) { // R is Android 11
            try {
                (context as Activity).startActivityForResult(
                    Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        .addCategory("android.intent.category.DEFAULT")
                        .setData(
                            Uri.parse(
                                String.format(
                                    "package:%s",
                                    context.getPackageName()
                                )
                            )
                        ),
                    PERMISSION_MANAGE_STORAGE_REQUEST_CODE
                )
            } catch (e: Exception) {
                (context as Activity).startActivityForResult(
                    Intent()
                        .setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION),
                    PERMISSION_MANAGE_STORAGE_REQUEST_CODE
                )
            }
        } else {
            ActivityCompat.requestPermissions(
                (context as Activity), arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), PERMISSION_STORAGE_REQUEST_CODE
            )
        }*/

        ActivityCompat.requestPermissions(
            (context as Activity), arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), PERMISSION_STORAGE_REQUEST_CODE
        )
    }

    protected fun requestPermissionCamera(context: Context) {
        ActivityCompat.requestPermissions(
            (context as Activity), arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), PERMISSION_CAMERA_REQUEST_CODE
        )
    }

    // onRequestResultPermissions
    protected fun onPermissionsRequestResult(
        context: Context,
        requestCode: Int,
        grantResults: IntArray? = null
    ): Boolean {
        val isPermissionsGranted: Boolean = when (requestCode) {
            /*Constants.PERMISSION_MANAGE_STORAGE_REQUEST_CODE -> {
                if (SDK_INT >= Build.VERSION_CODES.R) { // R is Android 11
                    Environment.isExternalStorageManager()
                } else {
                    false
                }
            }*/
            PERMISSION_STORAGE_REQUEST_CODE -> {
                (grantResults!!.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            }
            PERMISSION_CAMERA_REQUEST_CODE -> {
                (grantResults!!.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            }
            else -> {
                false
            }
        }

        return if (isPermissionsGranted) {
            toast(context, MSG_PERMISSION_GRANTED)
            true
        } else {
            toast(context, MSG_PERMISSION_DENIED)
            false
        }
    }

    //---------------------------------------- permission : END ----------------------------//
}