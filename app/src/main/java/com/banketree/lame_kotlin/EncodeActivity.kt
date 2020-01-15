package com.banketree.lame_kotlin


import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import com.naman14.androidlame.AndroidLame
import com.naman14.androidlame.LameBuilder
import com.naman14.androidlame.WaveReader

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class EncodeActivity : AppCompatActivity() {

    private val PICKFILE_REQUEST_CODE = 123

    val pickFile: TextView by lazy { findViewById<View>(R.id.pickFile) as TextView }
    var outputStream: BufferedOutputStream? = null
    var waveReader: WaveReader? = null
    var inputUri: Uri? = null

    val logFragment: LogFragment by lazy { LogFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encode)

        supportFragmentManager.beginTransaction().replace(R.id.log_container, logFragment).commit()

        val btnPickFile = findViewById<View>(R.id.btnPickFile) as Button

        btnPickFile.setOnClickListener { pickFile() }

        val encode = findViewById<View>(R.id.encode) as Button
        encode.setOnClickListener {
            pickFile.text = "Encoding to mp3..."
            object : Thread() {
                override fun run() {
                    encode()
                }
            }.start()
        }

    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "audio/*"
        startActivityForResult(intent, PICKFILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICKFILE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val filename = data.dataString
                inputUri = data.data
                if (inputUri != null) {
                    pickFile.text = getRealPathFromURI(inputUri)
                }
            }
        }
    }


    private fun encode() {
        val input = File(getRealPathFromURI(inputUri))
        val output = File(Environment.getExternalStorageDirectory().toString() + "/testencode.mp3")

        val CHUNK_SIZE = 8192

        addLog("Initialising wav reader")
        waveReader = WaveReader(input)

        try {
            waveReader!!.openWave()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        addLog("Intitialising encoder")
        val androidLame = LameBuilder()
            .setInSampleRate(waveReader!!.sampleRate)
            .setOutChannels(waveReader!!.channels)
            .setOutBitrate(128)
            .setOutSampleRate(waveReader!!.sampleRate)
            .setQuality(5)
            .build()

        try {
            outputStream = BufferedOutputStream(FileOutputStream(output), OUTPUT_STREAM_BUFFER)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        var bytesRead = 0

        val buffer_l = ShortArray(CHUNK_SIZE)
        val buffer_r = ShortArray(CHUNK_SIZE)
        val mp3Buf = ByteArray(CHUNK_SIZE)

        val channels = waveReader!!.channels

        addLog("started encoding")
        while (true) {
            try {
                if (channels == 2) {

                    bytesRead = waveReader!!.read(buffer_l, buffer_r, CHUNK_SIZE)
                    addLog("bytes read=$bytesRead")

                    if (bytesRead > 0) {

                        var bytesEncoded = 0
                        bytesEncoded = androidLame.encode(buffer_l, buffer_r, bytesRead, mp3Buf)
                        addLog("bytes encoded=$bytesEncoded")

                        if (bytesEncoded > 0) {
                            try {
                                addLog("writing mp3 buffer to outputstream with $bytesEncoded bytes")
                                outputStream!!.write(mp3Buf, 0, bytesEncoded)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        }

                    } else
                        break
                } else {

                    bytesRead = waveReader!!.read(buffer_l, CHUNK_SIZE)
                    addLog("bytes read=$bytesRead")

                    if (bytesRead > 0) {
                        var bytesEncoded = 0

                        bytesEncoded = androidLame.encode(buffer_l, buffer_l, bytesRead, mp3Buf)
                        addLog("bytes encoded=$bytesEncoded")

                        if (bytesEncoded > 0) {
                            try {
                                addLog("writing mp3 buffer to outputstream with $bytesEncoded bytes")
                                outputStream!!.write(mp3Buf, 0, bytesEncoded)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        }

                    } else
                        break
                }


            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        addLog("flushing final mp3buffer")
        val outputMp3buf = androidLame.flush(mp3Buf)
        addLog("flushed $outputMp3buf bytes")

        if (outputMp3buf > 0) {
            try {
                addLog("writing final mp3buffer to outputstream")
                outputStream!!.write(mp3Buf, 0, outputMp3buf)
                addLog("closing output stream")
                outputStream!!.close()

                runOnUiThread { pickFile.text = "Output mp3 saved in" + output.absolutePath }

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }


    }

    fun getRealPathFromURI(contentUri: Uri?): String {
        val proj = arrayOf(MediaStore.Audio.Media.DATA)
        val cursor = managedQuery(contentUri, proj, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    private fun addLog(log: String) {
        runOnUiThread { logFragment.addLog(log) }
    }

    companion object {

        private val OUTPUT_STREAM_BUFFER = 8192
    }
}
