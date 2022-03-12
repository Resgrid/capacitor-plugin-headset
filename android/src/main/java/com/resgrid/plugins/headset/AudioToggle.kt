package com.resgrid.plugins.headset

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.getcapacitor.JSArray
import com.getcapacitor.JSObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class AudioToggle(context: Context) {

    private val context: Context = context

    @RequiresApi(Build.VERSION_CODES.M)
    fun hasBuiltInEarpiece(): Boolean? {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return try {
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            for (dev in devices) {
                if (dev.isSink) {
                    if (dev.type == AudioDeviceInfo.TYPE_BUILTIN_EARPIECE) {
                        return true
                    }
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun hasBuiltInSpeaker(): Boolean? {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return try {
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            for (dev in devices) {
                if (dev.isSink) {
                    if (dev.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {
                        return true
                    }
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    fun setBluetoothScoOn(on: Boolean) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.isBluetoothScoOn = on
        if (on) {
            audioManager.startBluetoothSco()
        } else {
            audioManager.stopBluetoothSco()
        }
    }

    fun setSpeakerphoneOn(on: Boolean) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.isSpeakerphoneOn = on
    }

    fun setAudioMode(mode: String): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (mode == "bluetooth") {
            audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
            audioManager.isBluetoothScoOn = true
            audioManager.startBluetoothSco()
            return true
        } else if (mode == "earpiece") {
            audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
            audioManager.stopBluetoothSco()
            audioManager.isBluetoothScoOn = false
            audioManager.isSpeakerphoneOn = false
            return true
        } else if (mode == "speaker") {
            audioManager.mode = AudioManager.MODE_NORMAL
            audioManager.stopBluetoothSco()
            audioManager.isBluetoothScoOn = false
            audioManager.isSpeakerphoneOn = true
            return true
        } else if (mode == "ringtone") {
            audioManager.mode = AudioManager.MODE_RINGTONE
            audioManager.isSpeakerphoneOn = false
            return true
        } else if (mode == "normal") {
            audioManager.mode = AudioManager.MODE_NORMAL
            audioManager.isSpeakerphoneOn = false
            return true
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getOutputDevices(): JSObject? {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            val retdevs = JSArray()
            for (dev in devices) {
                if (dev.isSink) {
                    if (dev.type != AudioDeviceInfo.TYPE_BUILTIN_EARPIECE
                            && dev.type != AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {
                        retdevs.put(JSObject().put("id", dev.id).put("type", dev.type).put("name",
                                dev.productName.toString()))
                    }
                }
            }
            return JSObject().put("devices", retdevs)
        } catch (e: JSONException) {
            // lets hope json-object keys are not null and not duplicated :)
        }
        return JSObject()
    }

    fun getAudioMode(): String? {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val mode = audioManager.mode
        val isBluetoothScoOn = audioManager.isBluetoothScoOn
        val isSpeakerphoneOn = audioManager.isSpeakerphoneOn
        if (mode == AudioManager.MODE_IN_COMMUNICATION && isBluetoothScoOn) {
            return "bluetooth"
        } else if (mode == AudioManager.MODE_IN_COMMUNICATION && !isBluetoothScoOn && !isSpeakerphoneOn) {
            return "earpiece"
        } else if (mode == AudioManager.MODE_IN_COMMUNICATION && !isBluetoothScoOn && isSpeakerphoneOn) {
            return "speaker"
        } else if (mode == AudioManager.MODE_RINGTONE && !isSpeakerphoneOn) {
            return "ringtone"
        } else if (mode == AudioManager.MODE_NORMAL && !isSpeakerphoneOn) {
            return "normal"
        }
        return "normal"
    }

    fun isBluetoothScoOn(): Boolean? {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return audioManager.isBluetoothScoOn
    }

    fun isSpeakerphoneOn(): Boolean? {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return audioManager.isSpeakerphoneOn
    }
}