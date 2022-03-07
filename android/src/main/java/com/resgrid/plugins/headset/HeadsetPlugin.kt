package com.resgrid.plugins.headset

import android.Manifest
import android.util.Log
import com.getcapacitor.*
import com.getcapacitor.annotation.CapacitorPlugin
import android.app.Activity
import android.content.Context
import io.reactivex.disposables.CompositeDisposable
import android.Manifest.permission_group.MICROPHONE

import com.getcapacitor.PluginCall

import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.Permission


@CapacitorPlugin(name = "HeadsetPlugin",
        permissions = [
            Permission(
                    strings = [ Manifest.permission.RECORD_AUDIO,
                    ], alias = "MICROPHONE"
            )
        ])
class HeadsetPlugin : Plugin() {
    companion object {
        private val TAG = HeadsetPlugin::class.java.simpleName
    }

    private var isActive: Boolean = false
    private var type: HeadsetTypes = HeadsetTypes.B01
    private val disposables = CompositeDisposable()
    private val headsetManager by lazy { HeadsetManager(activity.applicationContext, type) }

    override fun load() {

    }

    @PluginMethod
    public fun start(call: PluginCall) {
        var typeValue = call.getInt("type")
        if (typeValue == null)
            typeValue = 0;

        type = HeadsetTypes.fromInt(typeValue)

        val context: Context = activity.applicationContext;
        val activity: Activity = activity;

        Log.i(TAG, "HeadsetPlugin: Start type: $typeValue")

        if (disposables.size() <= 0) {
            val disposable1 = headsetManager.connectedObservable
                    .subscribe(
                            { connected ->
                                    Log.i(TAG, "handset ${if (connected) "connected" else "disconnected"}")

                                    if (connected)
                                        onHeadsetConnected();
                                    else
                                        onHeadsetDisconnected();
                            },
                            { err -> Log.e(TAG, "error thrown by aina connection observable: $err") }
                    )

            val disposable2 = headsetManager.buttonEventsObservable
                    .subscribe(
                            { event ->
                                    Log.i(TAG, "${event.which} is ${if (event.pressed) "PRESSED" else "RELEASED"}")

                                when (event.which) {
                                    PttButton.PTT1 -> if (event.pressed) onHeadsetPress() else onHeadsetRelease()
                                    PttButton.VOL_UP -> onHeadsetVolUp()
                                    PttButton.VOL_DOWN -> onHeadsetVolDown()
                                    PttButton.EMERGENCY -> onHeadsetEmergency()
                                }
                            },
                            { err -> Log.e(TAG, "error thrown by aina buttons: $err") }
                    )

            disposables.addAll(disposable1, disposable2)
        } else {
            Log.i(TAG,"HeadsetPlugin: Already initialized")
        }

        call.resolve()
    }

    @PluginMethod
    public fun stop(call: PluginCall) {
        Log.i(TAG,"HeadsetPlugin: Stop")

        val context: Context = activity.applicationContext;
        val activity: Activity = activity;

        if (disposables != null && disposables.size() > 0) {
            disposables.dispose()
        }

        call.resolve()
    }

    @PluginMethod
    public fun setActive(call: PluginCall) {
        var isActiveValue = call.getBoolean("isActive")

        isActive = if (isActiveValue != null)
            isActiveValue!!;
        else
            false

        call.resolve()
    }

    @PluginMethod
    override fun requestPermissions(call: PluginCall) {
        // Save the call to be able to access it in microphonePermissionsCallback
        bridge.saveCall(call)
        // If the microphone permission is defined in the manifest, then we have to prompt the user
        // or else we will get a security exception when trying to present the microphone. If, however,
        // it is not defined in the manifest then we don't need to prompt and it will just work.
        if (isPermissionDeclared(MICROPHONE)) {
            // just request normally
            super.requestPermissions(call)
        } else {
            // the manifest does not define microphone permissions, so we need to decide what to do
            // first, extract the permissions being requested
            requestPermissionForAlias(MICROPHONE, call, "checkPermissions")
        }
    }

    override fun handleOnStart() {
        Log.i(TAG,"HeadsetPlugin: handleOnStart")

        super.handleOnStart()
    }

    override fun handleOnStop() {
        Log.i(TAG,"HeadsetPlugin: handleOnStop")

        super.handleOnStop()
    }

    private fun onHeadsetPress() {
        Log.i(TAG,"HeadsetPlugin press")

        val ret = JSObject()
        ret.put("event", "headsetPress")
        notifyListeners("onHeadsetPress", ret)
    }

    private fun onHeadsetRelease() {
        Log.i(TAG,"HeadsetPlugin release")

        val ret = JSObject()
        ret.put("event", "headsetRelease")
        notifyListeners("onHeadsetRelease", ret)
    }

    private fun onHeadsetToggle() {
        Log.i(TAG,"HeadsetPlugin toggle")

        val ret = JSObject()
        ret.put("event", "headsetToggle")
        notifyListeners("onHeadsetToggle", ret)
    }

    private fun onHeadsetConnected() {
        Log.i(TAG,"HeadsetPlugin connected")

        val ret = JSObject()
        ret.put("event", "headsetConnected")
        notifyListeners("onHeadsetConnected", ret)
    }

    private fun onHeadsetDisconnected() {
        Log.i(TAG,"HeadsetPlugin disconnected")

        val ret = JSObject()
        ret.put("event", "headsetDisconnected")
        notifyListeners("onHeadsetDisconnected", ret)
    }

    private fun onHeadsetVolUp() {
        Log.i(TAG,"HeadsetPlugin vol up")

        val ret = JSObject()
        ret.put("event", "headsetVolUp")
        notifyListeners("onHeadsetVolUp", ret)
    }

    private fun onHeadsetVolDown() {
        Log.i(TAG,"HeadsetPlugin vol down")

        val ret = JSObject()
        ret.put("event", "headsetVolDown")
        notifyListeners("onHeadsetVolDown", ret)
    }

    private fun onHeadsetEmergency() {
        Log.i(TAG,"HeadsetPlugin vol emergency")

        val ret = JSObject()
        ret.put("event", "headsetEmergency")
        notifyListeners("onHeadsetEmergency", ret)
    }
}