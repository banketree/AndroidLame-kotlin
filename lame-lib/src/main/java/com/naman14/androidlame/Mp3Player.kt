package com.naman14.androidlame

import android.media.MediaPlayer
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import java.lang.Exception

class Mp3Player {
    private var mediaPlayer: MediaPlayer? = null

    fun start(_filePath: String) {
        if (TextUtils.isEmpty(_filePath)) return //路径异常
        Thread(Runnable {
            try {
                startPlayByThread(_filePath)
            } catch (ex: Exception) {
                Log.e("Mp3Player", "Exception:${ex.message}")
            }

        }).start()
    }

    @Throws(Exception::class)
    private fun startPlayByThread(filePath: String) {
        if (isMainThread()) return
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer()

        // 存储在SD卡或其他文件路径下的媒体文件
        mediaPlayer!!.setDataSource(filePath)
        // 音乐文件准备
        mediaPlayer!!.prepare()
        mediaPlayer!!.start()
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    // 获取当前位置
    fun getPosition(): Int {
        if (mediaPlayer == null) return -1
        return mediaPlayer!!.currentPosition
    }

    // 获取时长
    fun getDuration(): Int {
        if (mediaPlayer == null) return -1
        return mediaPlayer!!.duration
    }

    private fun isMainThread(): Boolean {
        return Looper.getMainLooper().thread === Thread.currentThread()
    }
}