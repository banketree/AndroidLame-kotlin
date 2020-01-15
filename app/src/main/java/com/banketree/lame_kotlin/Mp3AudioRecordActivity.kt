package com.banketree.lame_kotlin


import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.naman14.androidlame.AndroidLame
import com.naman14.androidlame.LameBuilder

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class Mp3AudioRecordActivity : AppCompatActivity() {

    var minBuffer: Int = 0
    var inSamplerate = 8000

    var filePath = Environment.getExternalStorageDirectory().toString() + "/testrecord.mp3"

    var isRecording = false

    lateinit var audioRecord: AudioRecord
    lateinit var androidLame: AndroidLame
    lateinit var outputStream: FileOutputStream

    lateinit var logFragment: LogFragment
    lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_record)

        val start = findViewById<View>(R.id.startRecording) as Button
        val stop = findViewById<View>(R.id.stopRecording) as Button

        statusText = findViewById<View>(R.id.statusText) as TextView
        logFragment = LogFragment()

        supportFragmentManager.beginTransaction().replace(R.id.log_container, logFragment).commit()

        start.setOnClickListener {
            if (!isRecording) {
                object : Thread() {
                    override fun run() {
                        isRecording = true
                        startRecording()
                    }
                }.start()

            } else
                Toast.makeText(
                    this@Mp3AudioRecordActivity,
                    "Already recording",
                    Toast.LENGTH_SHORT
                ).show()
        }

        stop.setOnClickListener { isRecording = false }

    }

    private fun startRecording() {

        minBuffer = AudioRecord.getMinBufferSize(
            inSamplerate, AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        addLog("Initialising audio recorder..")
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC, inSamplerate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT, minBuffer * 2
        )

        //5 seconds data
        addLog("creating short buffer array")
        val buffer = ShortArray(inSamplerate * 2 * 5)

        // 'mp3buf' should be at least 7200 bytes long
        // to hold all possible emitted data.
        addLog("creating mp3 buffer")
        val mp3buffer = ByteArray((7200 + buffer.size.toDouble() * 2.0 * 1.25).toInt())

        try {
            outputStream = FileOutputStream(File(filePath))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        addLog("Initialising Andorid Lame")
        androidLame = LameBuilder()
            .setInSampleRate(inSamplerate)
            .setOutChannels(1)
            .setOutBitrate(32)
            .setOutSampleRate(inSamplerate)
            .build()

        addLog("started audio recording")
        updateStatus("Recording...")
        audioRecord.startRecording()

        var bytesRead = 0

        while (isRecording) {

            addLog("reading to short array buffer, buffer sze- $minBuffer")
            bytesRead = audioRecord.read(buffer, 0, minBuffer)
            addLog("bytes read=$bytesRead")

            if (bytesRead > 0) {

                addLog("encoding bytes to mp3 buffer..")
                val bytesEncoded = androidLame.encode(buffer, buffer, bytesRead, mp3buffer)
                addLog("bytes encoded=$bytesEncoded")

                if (bytesEncoded > 0) {
                    try {
                        addLog("writing mp3 buffer to outputstream with $bytesEncoded bytes")
                        outputStream.write(mp3buffer, 0, bytesEncoded)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }

        addLog("stopped recording")
        updateStatus("Recording stopped")

        addLog("flushing final mp3buffer")
        val outputMp3buf = androidLame.flush(mp3buffer)
        addLog("flushed $outputMp3buf bytes")

        if (outputMp3buf > 0) {
            try {
                addLog("writing final mp3buffer to outputstream")
                outputStream.write(mp3buffer, 0, outputMp3buf)
                addLog("closing output stream")
                outputStream.close()
                updateStatus("Output recording saved in $filePath")
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        addLog("releasing audio recorder")
        audioRecord.stop()
        audioRecord.release()

        addLog("closing android lame")
        androidLame.close()

        isRecording = false
    }

    private fun addLog(log: String) {
        runOnUiThread { logFragment.addLog(log) }
    }

    private fun updateStatus(status: String) {
        runOnUiThread { statusText.text = status }
    }
}
