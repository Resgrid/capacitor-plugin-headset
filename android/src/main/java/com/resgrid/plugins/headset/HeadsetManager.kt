package com.resgrid.plugins.headset

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.io.InputStream
import java.lang.ref.WeakReference
import java.text.Normalizer
import java.util.*
import java.util.regex.Pattern

class HeadsetManager(context: Context, type: HeadsetTypes) : BluetoothProfile.ServiceListener, AccessoryManager {

    //private val TAG = HeadsetPlugin::class.java.simpleName
    override val connected: Boolean
        get() = connectedSubject.value ?: false
    override val connectedObservable: Observable<Boolean>
        get() = connectedSubject
    override val buttonEventsObservable: Observable<PttButtonEvent>
        get() = buttonEventSubject

    private val contextRef = WeakReference(context)
    private val headsetType: HeadsetTypes = type;
    private val connectedSubject = BehaviorSubject.create<Boolean>()
    private val buttonEventSubject = PublishSubject.create<PttButtonEvent>()
    private var headsetProfile: BluetoothHeadset? = null
    private var ainaDevice: BluetoothDevice? = null
    private var ioThread: Thread? = null
    private var sppSocket: BluetoothSocket? = null

    private val bluetoothAdapter: BluetoothAdapter?
        get() = BluetoothAdapter.getDefaultAdapter()

    private val connectionEventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    ?: return
            val connected = intent.action == BluetoothDevice.ACTION_ACL_CONNECTED
            if (device.isPttAccessory) {
                ainaDevice = if (connected) device else null
                updateConnectedState()
            }
        }
    }

    init {
        when (type)
        {
            HeadsetTypes.HYS -> bluetoothAdapter?.getProfileProxy(context, this, BluetoothProfile.GATT)
            else -> { // B01 by default too
                bluetoothAdapter?.getProfileProxy(context, this, BluetoothProfile.HEADSET)
            }
        }

        context.apply {
            val filter = IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
            registerReceiver(connectionEventReceiver, filter)
        }
    }

    override fun dispose() {
        stopSppIo()
        contextRef.apply {
            get()?.unregisterReceiver(connectionEventReceiver)
            clear()
        }
        headsetProfile?.apply {
            Log.v(TAG, "closing profile proxy")
            bluetoothAdapter?.closeProfileProxy(BluetoothProfile.HEADSET, this)
        }
    }

    override fun onServiceDisconnected(profile: Int) {
        if (BluetoothHeadset.HEADSET == profile) {
            Log.v(TAG, "headset service disconnected")
            headsetProfile = null
        }
    }

    @SuppressLint("MissingPermission")
    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
        if (BluetoothHeadset.HEADSET == profile) {
            Log.v(TAG, "headset service connected")
            headsetProfile = proxy as BluetoothHeadset
            ainaDevice = headsetProfile!!.connectedDevices.find { it.isAinaPttVoiceResponder }
            updateConnectedState()
        }
    }

    private fun updateConnectedState() {
        val connected = ainaDevice != null
        Log.v(TAG, "voice responder connected: $connected")
        connectedSubject.onNext(connected)
        if (connected) {
            startSppIo()
        } else {
            stopSppIo()
        }
    }

    private fun startSppIo() {
        Log.v(TAG, "starting io thread...")
        ioThread = Thread({ ioLoop() }, "aina-spp-io").apply { start() }
    }

    private fun stopSppIo() {
        try {
            sppSocket?.apply {
                Log.v(TAG, "closing spp socket...")
                close()
            }
            sppSocket = null
        } catch (err: Throwable) {
            Log.v(TAG, "exception thrown closing SPP socket: $err")
        }
    }

    private fun ioLoop() {
        Log.v(TAG, "io thread started")
        val device = ainaDevice ?: return
        Log.v(TAG, "connecting SPP socket...")
        sppSocket = try {
            device.connectSppSocket(headsetType)
        } catch (e: Throwable) {
            Log.w(TAG, "failed to connect SPP socket")
            return
        }
        Log.v(TAG, "connected.")
        val readBuffer = ByteArray(64)
        while (true) {
            val data = try {
                sppSocket!!.inputStream.readCopying(readBuffer)
            } catch (e: Throwable) {
                Log.w(TAG, "exception reading from spp socket", e)
                sppSocket?.close()
                sppSocket = null
                break
            }
            val str = String(data)

            Log.v(TAG,"Headset button event: $str?")

            val buttonEvent = str?.asPttButtonEvent()
            if (null == buttonEvent) {
                Log.w(TAG, "unknown button event: $str?")
            } else {
                Log.v(TAG, "EVENT: $buttonEvent")
                buttonEventSubject.onNext(buttonEvent)
            }
        }
        Log.v(TAG, "io thread terminating")
    }

    companion object {
        private const val TAG = "ResgridPlugHeadsetMgr"
    }
}

private fun String.asPttButtonEvent(): PttButtonEvent? {
    return when {
        contains("+PTT=")   -> PttButtonEvent(PttButton.PTT1, contains("=P"))
        //contains("+PTT=")   -> PttButtonEvent(PttButton.PTT1, last() == 'R')
        contains("+PTTS=")  -> PttButtonEvent(PttButton.PTT2, last() == 'P')
        contains("+PTTE=")  -> PttButtonEvent(PttButton.EMERGENCY, last() == 'P')
        contains("+PTTB1=") -> PttButtonEvent(PttButton.LEFT, last() == 'P')
        contains("+PTTB2=") -> PttButtonEvent(PttButton.RIGHT, last() == 'P')
        contains("+VGS=U")  -> PttButtonEvent(PttButton.VOL_UP, false)
        contains("+VGS=D")  -> PttButtonEvent(PttButton.VOL_DOWN, false)
        contains("C:VM*")  -> PttButtonEvent(PttButton.VOL_UP, false)   //B01 Headset (i.e. Inrico)
        contains("C:VP*")  -> PttButtonEvent(PttButton.VOL_DOWN, false) //B01 Headset (i.e. Inrico)
        contains("C:SOS*")  -> PttButtonEvent(PttButton.EMERGENCY, false) //B01 Headset (i.e. Inrico)
        first() == '1'           -> PttButtonEvent(PttButton.PTT1, true) //B01 headset (Non-branded)
        first() == '0'           -> PttButtonEvent(PttButton.PTT1, false) //B01 headset (Non-branded)
        else                -> null
    }
}

private fun InputStream.readCopying(buffer: ByteArray): ByteArray {
    return read(buffer).let { Arrays.copyOfRange(buffer, 0, it) }
}