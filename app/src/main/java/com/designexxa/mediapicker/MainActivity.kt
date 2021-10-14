package com.designexxa.mediapicker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.designexxa.filepicker.FileData
import com.designexxa.filepicker.FilePicker
import com.designexxa.mediapicker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val context: Context = this@MainActivity
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCamera.setOnClickListener {
            FilePicker.openCamera(context)
        }
        binding.btnGallery.setOnClickListener {
            FilePicker.openGallery(context)
        }
        binding.btnFile.setOnClickListener {
            FilePicker.openFileManager(context)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fileDataList: ArrayList<FileData> =
            FilePicker.getFileResult(context, requestCode, resultCode, data)
        if (fileDataList.size > 0) {
            binding.iv.setImageURI(fileDataList[0].fileUri)
        }
    }
}