package com.resgrid.plugins.headset

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log

class SilencePlayer(context: Context) {

    companion object {
        private const val TAG = "(SilencePlayer)"
    }

    private var asset: AssetFileDescriptor? = null
    private var player: MediaPlayer? = null

    init {
        try {
            asset = context.resources.openRawResourceFd(R.raw.silence)
        } catch (t: Throwable) {
            Log.e(TAG, "Can't open a resource", t)
        }
    }

    fun playOnce() {
        play(false)
    }

    fun playForever() {
        play(true)
    }

    fun release() {
        player?.release()
        player = null
        asset?.close()
        asset = null
    }

    private fun play(looping: Boolean) {
        player?.release()
        asset?.let { descriptor ->
            player = MediaPlayer().also { player ->
                player.setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                player.isLooping = looping
                player.setOnPreparedListener {
                    it.start()
                }
                player.setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
                player.prepareAsync()
            }
        }
    }

}