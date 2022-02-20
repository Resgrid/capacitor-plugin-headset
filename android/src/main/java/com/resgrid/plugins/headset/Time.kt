package com.resgrid.plugins.headset

import android.content.Context
import android.os.SystemClock

/**
 * Android-specific [Time] implementation.
 */
class Time(context: Context) {

    private val context = context.applicationContext

    val tickCount: Long
        get() = SystemClock.elapsedRealtime()

    fun createTimer(): Timer {
        return Timer(context)
    }

}