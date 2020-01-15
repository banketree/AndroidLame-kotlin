package com.banketree.lame_kotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            checkPermissions()
        }

        val record = findViewById<View>(R.id.record_btn) as Button
        record.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    Mp3AudioRecordActivity::class.java
                )
            )
        }

        val encode = findViewById<View>(R.id.encode_btn) as Button
        encode.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    EncodeActivity::class.java
                )
            )
        }
        val github = findViewById<View>(R.id.github_btn) as Button
        github.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://github.com/banketree/AndroidLame-kotlin"))) }

    }


    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                2
            )
            else -> {
            }
        }
    }

}
