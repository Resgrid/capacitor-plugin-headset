package com.resgrid.plugins.headset

import android.util.Log
import com.getcapacitor.*
import com.getcapacitor.annotation.CapacitorPlugin
import android.app.Activity
import android.content.Context
import io.reactivex.disposables.CompositeDisposable

@CapacitorPlugin(name = "HeadsetPlugin")
class HeadsetPlugin : Plugin() {
    companion object {
        private val TAG = HeadsetPlugin::class.java.simpleName
    }

    private val disposables = CompositeDisposable()
    private val headsetManager by lazy { HeadsetManager(activity.applicationContext) }

    override fun load() {

    }

    @PluginMethod
    public fun start(call: PluginCall) {
        var type = call.getInt("type")

        if (type == null)
            type = 0;

        val context: Context = activity.applicationContext;
        val activity: Activity = activity;

        Log.i(TAG,"HeadsetPlugin: Start for type: $type")

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