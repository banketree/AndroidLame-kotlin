# AndroidLame-kotlin
AndroidLame kotlin suport mp3 recorder and player


      implementation 'com.github.banketree:AndroidLame-kotlin:v0.0.1'




        val mp3AudioRecorder: Mp3AudioRecorder by lazy { Mp3AudioRecorder() }
        val mp3Player: Mp3Player by lazy { Mp3Player() }
    
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
        
        
        
![Image text](https://github.com/banketree/AndroidLame-kotlin/blob/master/screenShot/1.jpg)        
