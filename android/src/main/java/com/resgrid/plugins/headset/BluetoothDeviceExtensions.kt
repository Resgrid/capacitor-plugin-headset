package com.resgrid.plugins.headset

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.lang.Exception
import java.util.*

private val UUID_GENERIC_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
private val HYS_HEADSET_SPP = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB")

internal val BluetoothDevice.isAinaPttVoiceResponder: Boolean
    @SuppressLint("MissingPermission")
    get() = name?.contains("APTT") ?: false

internal val BluetoothDevice.isPttAccessory: Boolean
    @SuppressLint("MissingPermission")
    get() {
        if (name?.contains("APTT") == true || name?.contains("B01") == true || name?.contains("PTT-KEY") == true)
            return true

        return false
    }

@SuppressLint("MissingPermission")
internal fun BluetoothDevice.connectSppSocket(type: HeadsetTypes, retryCount: Int = 3): BluetoothSocket {
    var attempts = 0

    when (type) {
        HeadsetTypes.HYS -> {
            while (true) {
                try {
                    return createRfcommSocketToServiceRecord(HYS_HEADSET_SPP).apply { connect() }
                } catch (e: Throwable) {
                    if (++attempts == retryCount) {
                        throw e
                    }
                }
            }
        }
        else -> { // B01 by default too
            while (true) {
                try {
                    return createRfcommSocketToServiceRecord(UUID_GENERIC_SPP).apply { connect() }
                } catch (e: Throwable) {
                    if (++attempts == retryCount) {
                        throw e
                    }
                }
            }
        }
    }
}