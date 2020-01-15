package com.naman14.androidlame

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

/**
 * @author
 * @time 2020/1/15 10:54
 * @description
 */
class WaveReader {
    private var mInFile: File? = null
    private var mInStream: BufferedInputStream? = null

    /**
     * Get sample rate
     *
     * @return input file's sample rate
     */
    var sampleRate: Int = 0
        private set
    /**
     * Get number of channels
     *
     * @return number of channels in input file
     */
    var channels: Int = 0
        private set
    /**
     * Get PCM format, S16LE or S8LE
     *
     * @return number of bits per sample
     */
    var pcmFormat: Int = 0
        private set
    private var mFileSize: Int = 0
    /**
     * Get input file's audio data size
     * Basically file size without headers included
     *
     * @return audio data size in bytes
     */
    var dataSize: Int = 0
        private set

    /**
     * Get file size
     *
     * @return total input file size in bytes
     */
    val fileSize: Int
        get() = mFileSize + 8

    /**
     * Get input file length
     *
     * @return length of file in seconds
     */
    val length: Int
        get() = if (sampleRate == 0 || channels == 0 || (pcmFormat + 7) / 8 == 0) {
            0
        } else {
            dataSize / (sampleRate * channels * ((pcmFormat + 7) / 8))
        }


    /**
     * Constructor; initializes LameUtils to read from given file
     *
     * @param path  path to input file
     * @param name  name of input file
     */
    constructor(path: String, name: String) {
        this.mInFile = File(path + File.separator + name)
    }

    /**
     * Constructor; initializes LameUtils to read from given file
     *
     * @param file  handle to input file
     */
    constructor(file: File) {
        this.mInFile = file
    }

    /**
     * Open WAV file for reading
     *
     * @throws FileNotFoundException if input file does not exist
     * @throws InvalidWaveException if input file is not a valid WAVE file
     * @throws IOException if I/O error occurred during file read
     */
    @Throws(FileNotFoundException::class, InvalidWaveException::class, IOException::class)
    fun openWave() {
        val fileStream = FileInputStream(mInFile!!)
        mInStream = BufferedInputStream(fileStream, STREAM_BUFFER_SIZE)

        val headerId = LameUtils.readUnsignedInt(mInStream!!)  // should be "RIFF"
        if (headerId != WAV_HEADER_CHUNK_ID) {
            throw InvalidWaveException(String.format("Invalid WAVE header chunk ID: %d", headerId))
        }
        mFileSize = LameUtils.readUnsignedIntLE(mInStream!!)  // length of header
        val format = LameUtils.readUnsignedInt(mInStream!!)  // should be "WAVE"
        if (format != WAV_FORMAT) {
            throw InvalidWaveException("Invalid WAVE format")
        }

        val formatId = LameUtils.readUnsignedInt(mInStream!!)  // should be "fmt "
        if (formatId != WAV_FORMAT_CHUNK_ID) {
            throw InvalidWaveException("Invalid WAVE format chunk ID")
        }
        val formatSize = LameUtils.readUnsignedIntLE(mInStream!!)
        if (formatSize != 16) {

        }
        val audioFormat = LameUtils.readUnsignedShortLE(mInStream!!).toInt()
        if (audioFormat != 1) {
            throw InvalidWaveException("Not PCM WAVE format")
        }
        channels = LameUtils.readUnsignedShortLE(mInStream!!).toInt()
        sampleRate = LameUtils.readUnsignedIntLE(mInStream!!)
        val byteRate = LameUtils.readUnsignedIntLE(mInStream!!)
        val blockAlign = LameUtils.readUnsignedShortLE(mInStream!!).toInt()
        pcmFormat = LameUtils.readUnsignedShortLE(mInStream!!).toInt()

        val dataId = LameUtils.readUnsignedInt(mInStream!!)
        if (dataId != WAV_DATA_CHUNK_ID) {
            throw InvalidWaveException("Invalid WAVE data chunk ID")
        }
        dataSize = LameUtils.readUnsignedIntLE(mInStream!!)
    }

    /**
     * Read audio data from input file (mono)
     *
     * @param dst  mono audio data output buffer
     * @param numSamples  number of samples to read
     *
     * @return number of samples read
     *
     * @throws IOException if file I/O error occurs
     */
    @Throws(IOException::class)
    fun read(dst: ShortArray, numSamples: Int): Int {
        if (channels != 1) {
            return -1
        }

        val buf = ByteArray(numSamples * 2)
        var index = 0
        val bytesRead = mInStream!!.read(buf, 0, numSamples * 2)

        var i = 0
        while (i < bytesRead) {
            dst[index] = LameUtils.byteToShortLE(buf[i], buf[i + 1])
            index++
            i += 2
        }

        return index
    }

    /**
     * Read audio data from input file (stereo)
     *
     * @param left  left channel audio output buffer
     * @param right  right channel audio output buffer
     * @param numSamples  number of samples to read
     *
     * @return number of samples read
     *
     * @throws IOException if file I/O error occurs
     */
    @Throws(IOException::class)
    fun read(left: ShortArray, right: ShortArray, numSamples: Int): Int {
        if (channels != 2) {
            return -1
        }
        val buf = ByteArray(numSamples * 4)
        var index = 0
        val bytesRead = mInStream!!.read(buf, 0, numSamples * 4)

        var i = 0
        while (i < bytesRead) {
            val `val` = LameUtils.byteToShortLE(buf[0], buf[i + 1])
            if (i % 4 == 0) {
                left[index] = `val`
            } else {
                right[index] = `val`
                index++
            }
            i += 2
        }

        return index
    }

    /**
     * Close WAV file. LameUtils object cannot be used again following this call.
     *
     * @throws IOException if I/O error occurred closing filestream
     */
    @Throws(IOException::class)
    fun closeWaveFile() {
        if (mInStream != null) {
            mInStream!!.close()
        }
    }

    companion object {
        private val WAV_HEADER_CHUNK_ID = 0x52494646  // "RIFF"
        private val WAV_FORMAT = 0x57415645  // "WAVE"
        private val WAV_FORMAT_CHUNK_ID = 0x666d7420 // "fmt "
        private val WAV_DATA_CHUNK_ID = 0x64617461 // "data"
        private val STREAM_BUFFER_SIZE = 4096
    }
}