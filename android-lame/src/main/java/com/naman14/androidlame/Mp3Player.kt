package com.naman14.androidlame

import android.media.MediaPlayer
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import java.lang.Exception

class Mp3Player {
    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer()
    }

    fun start(_filePath: String): MediaPlayer? {
        if (TextUtils.isEmpty(_filePath)) return null //路径异常
        return startPlayByThread(_filePath)
    }

    @Throws(Exception::class)
    private fun startPlayByThread(filePath: String): MediaPlayer? {
        if (isMainThread()) return null
        mediaPlayer.stop()
        mediaPlayer.reset()

        // 存储在SD卡或其他文件路径下的媒体文件
        mediaPlayer.setDataSource(filePath)
        // 音乐文件准备
        mediaPlayer.prepare()
        mediaPlayer.start()
        return mediaPlayer
    }

    fun stop() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    fun pause() {
        mediaPlayer.pause()
    }

    fun resume() {
        mediaPlayer.start()
    }

    // 获取当前位置
    fun getCurPosition(): Int {
        if (mediaPlayer == null) return -1
        return mediaPlayer!!.currentPosition
    }

    // 获取时长
    fun getDuration(): Int {
        if (mediaPlayer == null) return -1
        return mediaPlayer!!.duration
    }

    fun isPlaying(): Boolean {
        if (mediaPlayer == null) return false
        return mediaPlayer!!.isPlaying
    }

    fun getPlayer(): MediaPlayer {
        return mediaPlayer
    }

    private fun isMainThread(): Boolean {
        return Looper.getMainLooper().thread === Thread.currentThread()
    }
}