package com.example.anoopmohanan.streamaudioapp.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.PowerManager
import android.util.Log
import java.io.File

class StreamAudioPlayer

    private constructor(context: Context){

        private var mMediaPlayer: MediaPlayer? = null
        private var currentContext: Context? = null
        private val callbacks = ArrayList<StreamAudioPlayerCallback>()

        private val mediaPlayer: MediaPlayer?
        get() {
            if (mMediaPlayer == null) {
                val attributes = AudioAttributes.Builder()
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build()
                mMediaPlayer = MediaPlayer()
                mMediaPlayer?.setWakeMode(currentContext, PowerManager.PARTIAL_WAKE_LOCK)
                mMediaPlayer?.setAudioAttributes(attributes)
                mMediaPlayer?.setOnCompletionListener(mediaPlayerCompletionListener)
                mMediaPlayer?.setOnPreparedListener(mediaPlayerPreparedListener)
                mMediaPlayer?.setOnErrorListener(mediaPlayerErrorListener)
            }
            return mMediaPlayer
        }
        /**
         * Once mediplayer completes, inform all the callbacks
         */
        private val mediaPlayerCompletionListener = MediaPlayer.OnCompletionListener {
            for (callback in callbacks) {

                callback.itemComplete()
            }
        }

        /**
         * Once mediplayer is prepared, inform all the callbacks
         */
        private val mediaPlayerPreparedListener = MediaPlayer.OnPreparedListener {
            for (callback in callbacks) {
                callback.playerPrepared()
            }
            mMediaPlayer!!.start()
        }

        /**
         * Once mediplayer hits error, inform all the callbacks
         */
        private val mediaPlayerErrorListener = MediaPlayer.OnErrorListener { mp, what, extra ->
            for (callback in callbacks) {
                callback.playerError()
            }
            false
        }

        // Set the context
        init {
            currentContext = context.applicationContext
        }

    //region Media player state functions
    /**
         * Check whether the MediaPlayer is currently playing
         * @return true if playing, false not
         */
        val isPlaying: Boolean
            get() = mediaPlayer!!.isPlaying

        /**
         * Request our MediaPlayer to play an item
         */
        private fun play(url: String) {
            if (isPlaying) {
                Log.w("Plat=yer", "Already playing an item, did you mean to play another?")
            }
            if (mediaPlayer!!.isPlaying) {
                // Stop the current playing content
                mediaPlayer!!.stop()
            }

            //reset our player
            mediaPlayer!!.reset()

            //Set the url to play
            mediaPlayer!!.setDataSource(url)

            //prepare the player, once prepared the respective listner is called
            try {
                mediaPlayer!!.prepareAsync()
            } catch (e: IllegalStateException) {

            }

        }

        /**
         * Public fucntion to pause the playback
         */
        fun pause() {
            mediaPlayer!!.pause()
        }

        /**
         * Public fucntion to play the playback
         */
        fun playNew() {
            mediaPlayer!!.start()
        }

        /**
         * Public fucntion to stop the playback
         */
        fun stop() {
            mediaPlayer!!.stop()
        }

        /**
         * Public function to play a specific  the playback
         * [url] is set for playback
         */
        fun playItem(url:String) {
             play(url)
        }
        /**
         * Public function to release mediaplayer
         */
        fun release() {
            if (mMediaPlayer != null) {
                if (mMediaPlayer!!.isPlaying) {
                    mMediaPlayer!!.stop()
                }
                mMediaPlayer!!.reset()
                mMediaPlayer!!.release()
            }
            mMediaPlayer = null
        }
    //endregion

        /**
         * Add a [callback] to our StreamAudioPlayer
         * @param callback Callback that listens to the events
         */
        fun addCallback(callback: StreamAudioPlayerCallback) {
            synchronized(callbacks) {
                if (!callbacks.contains(callback)) {
                    callbacks.add(callback)
                }
            }
        }

        /**
         * Remove a [callback] from our StreamAudioPlayer, this is removed from our list of callbacks
         * @param callback Callback that listens to the events
         */
        fun removeCallback(callback: StreamAudioPlayerCallback) {
            synchronized(callbacks) {
                callbacks.remove(callback)
            }
        }
    //region Interface to the Activity
        /**
         * An interface callback to keep track of the state of the MediaPlayer
         */
        interface StreamAudioPlayerCallback {
            fun playerPrepared()
            fun playerProgress(offsetInMilliseconds: Long, percent: Float)
            fun itemComplete()
            fun playerError()
        }
    //endregion

    //region Singleton Instance
    companion object {

            val TAG = "StreamAudioPlayer"

            private var sharedInstance: StreamAudioPlayer? = null

            /**
             * Get a reference to the StreamAudioPlayer instance, if it's null, we will create a new one
             * with the supplied context.
             * @param [context] any context
             * @return instance of the [StreamAudioPlayer]
             */
            fun getInstance(context: Context): StreamAudioPlayer {
                if (sharedInstance == null) {
                    sharedInstance = StreamAudioPlayer(context)
                    clearCache(context)
                }
                return sharedInstance!!
            }

            private fun clearCache(context: Context) {
                try {
                    val dir = context.cacheDir
                    if (dir != null && dir.isDirectory) {
                        deleteDir(dir)
                    }
                } catch (e: Exception) {
                    // TODO: handle exception
                }

            }

            private fun deleteDir(dir: File?): Boolean {
                if (dir != null && dir.isDirectory) {
                    val children = dir.list()
                    for (i in children!!.indices) {
                        val success = deleteDir(File(dir, children[i]))
                        if (!success) {
                            return false
                        }
                    }
                }
                return dir?.delete() ?: false
            }
        }
    //endregion

    }


