package com.naman14.androidlame

class LameBuilder {
    var inSampleRate: Int = 0
        private set
    var outSampleRate: Int = 0
        private set
    var outBitrate: Int = 0
        private set
    var outChannel: Int = 0
        private set
    var quality: Int = 0
        private set
    var vbrQuality: Int = 0
        private set
    var abrMeanBitrate: Int = 0
        private set
    var lowpassFreq: Int = 0
        private set
    var highpassFreq: Int = 0
        private set
    var scaleInput: Float = 0.toFloat()
        private set
    var mode: Mode
    var vbrMode: VbrMode

    var id3tagTitle: String? = null
    var id3tagArtist: String? = null
    var id3tagAlbum: String? = null
    var id3tagComment: String? = null
    var id3tagYear: String? = null

    enum class Mode {
        STEREO, JSTEREO, MONO, DEFAULT
        //DUAL_CHANNEL not supported
    }

    enum class VbrMode {
        VBR_OFF, VBR_RH, VBR_MTRH, VBR_ABR, VBR_DEFAUT
    }

    init {
        this.id3tagTitle = null
        this.id3tagAlbum = null
        this.id3tagArtist = null
        this.id3tagComment = null
        this.id3tagYear = null

        this.inSampleRate = 44100

        //default 0, Lame picks best according to compression
        this.outSampleRate = 0

        this.outChannel = 2
        this.outBitrate = 128
        this.scaleInput = 1f

        this.quality = 5
        this.mode = Mode.DEFAULT
        this.vbrMode = VbrMode.VBR_OFF
        this.vbrQuality = 5
        this.abrMeanBitrate = 128

        //default =0, Lame chooses
        this.lowpassFreq = 0
        this.highpassFreq = 0
    }

    fun setQuality(quality: Int): LameBuilder {
        this.quality = quality
        return this
    }

    fun setInSampleRate(inSampleRate: Int): LameBuilder {
        this.inSampleRate = inSampleRate
        return this
    }

    fun setOutSampleRate(outSampleRate: Int): LameBuilder {
        this.outSampleRate = outSampleRate
        return this
    }

    fun setOutBitrate(bitrate: Int): LameBuilder {
        this.outBitrate = bitrate
        return this
    }

    fun setOutChannels(channels: Int): LameBuilder {
        this.outChannel = channels
        return this
    }

    fun setId3tagTitle(title: String): LameBuilder {
        this.id3tagTitle = title
        return this
    }

    fun setId3tagArtist(artist: String): LameBuilder {
        this.id3tagArtist = artist
        return this
    }

    fun setId3tagAlbum(album: String): LameBuilder {
        this.id3tagAlbum = album
        return this
    }

    fun setId3tagComment(comment: String): LameBuilder {
        this.id3tagComment = comment
        return this
    }

    fun setId3tagYear(year: String): LameBuilder {
        this.id3tagYear = year
        return this
    }

    fun setScaleInput(scaleAmount: Float): LameBuilder {
        this.scaleInput = scaleAmount
        return this
    }

    fun setMode(mode: Mode): LameBuilder {
        this.mode = mode
        return this
    }

    fun setVbrMode(mode: VbrMode): LameBuilder {
        this.vbrMode = mode
        return this
    }

    fun setVbrQuality(quality: Int): LameBuilder {
        this.vbrQuality = quality
        return this
    }

    fun setAbrMeanBitrate(bitrate: Int): LameBuilder {
        this.abrMeanBitrate = bitrate
        return this
    }

    fun setLowpassFreqency(freq: Int): LameBuilder {
        this.lowpassFreq = freq
        return this
    }

    fun setHighpassFreqency(freq: Int): LameBuilder {
        this.highpassFreq = freq
        return this
    }

    fun build(): AndroidLame {
        return AndroidLame(this)
    }
}