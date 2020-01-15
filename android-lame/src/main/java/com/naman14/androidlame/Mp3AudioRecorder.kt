package com.naman14.androidlame

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import kotlin.math.log10


class Mp3AudioRecorder {
    var inSamplerate = 8000
    var isRecording = false
        private set
    var filePath: String? = null

    fun start(_filePath: String, iAudioRecordListener: IAudioRecordListener? = null) {
        if (TextUtils.isEmpty(_filePath)) return //路径异常
        if (isRecording) return//正在录制
        this.filePath = _filePath
        isRecording = true
        Thread(Runnable {
            startRecordingByThread(iAudioRecordListener)
        }).start()
    }

    fun stop() {
        isRecording = false
    }

    private fun startRecordingByThread(iAudioRecordListener: IAudioRecordListener?) {
        if (isMainThread()) return
        iAudioRecordListener?.onStart()
        var minBuffer = AudioRecord.getMinBufferSize(
            inSamplerate, AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        Log.i("Mp3AudioRecorder", "Initialising audio recorder..")
        var audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC, inSamplerate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT, minBuffer * 2
        )

        //5 seconds data
        Log.i("Mp3AudioRecorder", "creating short buffer array")
        val buffer = ShortArray(inSamplerate * 2 * 5)

        // 'mp3buf' should be at least 7200 bytes long
        // to hold all possible emitted data.
        Log.i("Mp3AudioRecorder", "creating mp3 buffer")
        val mp3buffer = ByteArray((7200 + buffer.size.toDouble() * 2.0 * 1.25).toInt())

        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(File(filePath))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        Log.i("Mp3AudioRecorder", "Initialising Andorid Lame")
        var androidLame = LameBuilder()
            .setInSampleRate(inSamplerate)
            .setOutChannels(1)
            .setOutBitrate(32)
            .setOutSampleRate(inSamplerate)
            .build()

        Log.i("Mp3AudioRecorder", "started audio recording")
        audioRecord.startRecording()

        var bytesRead = 0
        var countVolumeTime = 0L

        while (isRecording) {
            Log.i("Mp3AudioRecorder", "reading to short array buffer, buffer sze- $minBuffer")
            bytesRead = audioRecord.read(buffer, 0, minBuffer)
            Log.i("Mp3AudioRecorder", "bytes read=$bytesRead")
            if (bytesRead <= 0) continue

            //realtime volume start
            iAudioRecordListener?.let {
                if ((System.currentTimeMillis() - countVolumeTime) < 500) return@let
                countVolumeTime = System.currentTimeMillis()
                var vTotal = 0
                // 将 buffer 内容取出，进行平方和运算
                for (i in 0 until buffer.size) {
                    vTotal += buffer[i] * buffer[i]
                }

                // 平方和除以数据总长度，得到音量大小。可以获取白噪声值，然后对实际采样进行标准化。
                val mean = vTotal / (bytesRead).toDouble()
                val volume = 10 * log10(mean) - 50   //50 基准起点
                if (volume > 0) {
                    it.onAudioVolume(volume)
                }
            }
            //realtime volume end

            Log.i("Mp3AudioRecorder", "encoding bytes to mp3 buffer..")
            val bytesEncoded = androidLame.encode(buffer, buffer, bytesRead, mp3buffer)
            Log.i("Mp3AudioRecorder", "bytes encoded=$bytesEncoded")
            if (bytesEncoded > 0) {
                try {
                    Log.i("Mp3AudioRecorder","writing mp3 buffer to outputstream with $bytesEncoded bytes")
                    outputStream?.write(mp3buffer, 0, bytesEncoded)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        Log.i("Mp3AudioRecorder", "stopped recording")
        Log.i("Mp3AudioRecorder", "flushing final mp3buffer")
        val outputMp3buf = androidLame.flush(mp3buffer)
        Log.i("Mp3AudioRecorder", "flushed $outputMp3buf bytes")

        if (outputMp3buf > 0) {
            try {
                Log.i("Mp3AudioRecorder", "writing final mp3buffer to outputstream")
                outputStream?.write(mp3buffer, 0, outputMp3buf)
                Log.i("Mp3AudioRecorder", "closing output stream")
                outputStream?.close()
                Log.i("Mp3AudioRecorder", "Output recording saved in $filePath")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        Log.i("Mp3AudioRecorder", "releasing audio recorder")
        audioRecord.stop()
        audioRecord.release()

        Log.i("Mp3AudioRecorder", "closing android lame")
        androidLame.close()
        isRecording = false
        iAudioRecordListener?.onStop()
    }

    private fun isMainThread(): Boolean {
        return Looper.getMainLooper().thread === Thread.currentThread()
    }

    interface IAudioRecordListener {
        fun onStart()
        fun onStop()
        fun onAudioVolume(volume: Double)
    }
}