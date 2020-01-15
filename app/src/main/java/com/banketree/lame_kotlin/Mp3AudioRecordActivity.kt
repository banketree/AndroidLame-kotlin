package com.banketree.lame_kotlin


import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.naman14.androidlame.Mp3AudioRecorder
import com.naman14.androidlame.Mp3Player

class Mp3AudioRecordActivity : AppCompatActivity() {
    val mp3AudioRecorder: Mp3AudioRecorder by lazy { Mp3AudioRecorder() }
    val mp3Player: Mp3Player by lazy { Mp3Player() }

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
            if (!mp3AudioRecorder.isRecording) {
                mp3AudioRecorder.start(Environment.getExternalStorageDirectory().toString() + "/1/test_${System.currentTimeMillis()}.mp3",
                    object : Mp3AudioRecorder.IAudioRecordListener {
                        override fun onStart() {
                            addLog("start recording")
                        }

                        override fun onStop() {
                            addLog("stop recording -> file:${mp3AudioRecorder.filePath}")
                            mp3Player.start(mp3AudioRecorder.filePath!!)
                        }

                        override fun onAudioVolume(volume: Double) {
                            addLog("recording -> volume:${volume}")
                        }
                    })
            } else
                Toast.makeText(
                    this@Mp3AudioRecordActivity,
                    "Already recording",
                    Toast.LENGTH_SHORT
                ).show()
        }

        stop.setOnClickListener {
            mp3AudioRecorder.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mp3AudioRecorder.stop()
    }

    private fun addLog(log: String) {
        runOnUiThread { logFragment.addLog(log) }
    }

    private fun updateStatus(status: String) {
        runOnUiThread { statusText.text = status }
    }
}
