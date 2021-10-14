package com.designexxa.filepicker

import android.net.Uri

data class FileData(
    var fileName: String = "",
    var filePath: String = "",
    var fileUri: Uri? = null
)
