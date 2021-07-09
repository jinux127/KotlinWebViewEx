package com.example.kotlinwebviewex.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.le.ScanCallback
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.RxBleDeviceServices
import com.polidea.rxandroidble2.exceptions.BleScanException
import com.polidea.rxandroidble2.internal.connection.RxBleConnectionImpl_Factory
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.charset.Charset
import java.util.*


object BluetoothHelper {
    private val TAG = BluetoothHelper::class.java.simpleName
    const val SERVICE_STRING = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E"
    const val CHARACTERISTIC_COMMAND_STRING = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E"
    const val CHARACTERISTIC_RESPONSE_STRING = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E"

    var scanSubscription:Disposable?=null
    var connectionStateDisposable: Disposable?=null //ex
    private var mConnectSubscription: Disposable? = null //ex
    var rxBleConnection: RxBleConnection? = null
    var rxBleDeviceServices: RxBleDeviceServices? = null
    var bleGattServices: List<BluetoothGattService>? = null

    private var scanResults: ArrayList<ScanResult>? = ArrayList()

    var isRead = false

    private lateinit var rxBleClient: RxBleClient
    fun init(context: Context) {
        rxBleClient = RxBleClient.create(context)
    }


    fun startScan(name: String) {
        if (scanSubscription != null) scanSubscription = null
        if (mConnectSubscription != null) mConnectSubscription = null
        if (connectionStateDisposable != null) connectionStateDisposable = null

        when (rxBleClient.state) {
            RxBleClient.State.BLUETOOTH_NOT_AVAILABLE -> Log.i(TAG, "Bluetooth Not Available")
            RxBleClient.State.LOCATION_SERVICES_NOT_ENABLED -> Log.i(TAG, "GPS 꺼짐")
            RxBleClient.State.BLUETOOTH_NOT_ENABLED -> Log.i(TAG, "블루투스 꺼짐")
            else -> scanSubscription = rxBleClient.scanBleDevices(
                ScanSettings.Builder().run {
                    setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    build()
                },
                ScanFilter.Builder().run {
                    build()
                }
            ).subscribe({ scanResult ->
//                scanResult.bleDevice.name.matches("찾을이름*") //블루투스 이름으로 특정 기기 찾기
                Log.i(
                    TAG,
                    "mac - ${scanResult.bleDevice.macAddress}, RSSI - ${scanResult.rssi}, name - ${scanResult.bleDevice.name}"
                )
                addScanResult(scanResult) // TODO: 2021-07-07 addScanResult 스캔한결과 리스트에 추가하는방법으로 사용해보는중
                if (name == scanResult.bleDevice.name) {
                    Log.d(TAG,"같은 이름 발견 스캔 종료")
                    scanSubscription?.dispose()
                    Handler(Looper.getMainLooper()).postDelayed({ // TODO: 2021-07-07 status:133 error 해결방법?
                        Log.d(TAG,"500ms 뒤 연결")
                        connection(scanResult.bleDevice)
                    }, 500)

                }

            }, { throwable ->
                if (throwable is BleScanException)
                    Log.e(TAG, "bluetooth scan error - ${throwable.reason}")
                else
                    Log.e(TAG, "bluetooth scan Unknown Error")
            })
        }


    }
    @SuppressLint("CheckResult")
    fun connection(device: RxBleDevice){
        Log.d(TAG,"connection ")
        connectionStateDisposable = device.observeConnectionStateChanges()
            .subscribe({connectionState ->
                        connectionStateListener(device,connectionState)
            }){
                it.printStackTrace()
            }
        mConnectSubscription = bleConnectObservable(device)
    }

    fun stopScan(){
        scanSubscription?.dispose()
    }

    fun connectionStateListener(
        device: RxBleDevice,
        connectionState: RxBleConnection.RxBleConnectionState
    ){
        when(connectionState){
            RxBleConnection.RxBleConnectionState.CONNECTED -> {
                Log.d(TAG,"CONNECTED")
            }
            RxBleConnection.RxBleConnectionState.CONNECTING -> {
                Log.d(TAG,"CONNECTING")
            }
            RxBleConnection.RxBleConnectionState.DISCONNECTED -> {
                Log.d(TAG,"DISCONNECTED")
            }
            RxBleConnection.RxBleConnectionState.DISCONNECTING -> {
                Log.d(TAG,"DISCONNECTING")
            }
        }
    }

    private fun addScanResult(result: ScanResult) {//스캔 결과 추가
        // get scanned device
        val device = result.bleDevice
        // get scanned device MAC address
        val deviceAddress = device.macAddress//스캔된 장치의 MAC 주소
        // add the device to the result list
        for (dev in scanResults!!) { //이미 추가돼있다면 리턴
            if (dev.bleDevice.macAddress == deviceAddress) return
        }
        scanResults?.add(result) //스캔 결과리스트에 추가
        // log
        Log.d(TAG,"add scanned device: $deviceAddress")
    }
    /**
     * Connect & Discover Services
     * @Saved rxBleConnection, rxBleDeviceServices, bleGattServices
     */
    fun bleConnectObservable(device: RxBleDevice): Disposable =
        device.establishConnection(true) // <-- autoConnect flag
            .subscribe({ _rxBleConnection->
                // All GATT operations are done through the rxBleConnection.

                rxBleConnection = _rxBleConnection

                // Discover services
                rxBleConnection?.discoverServices()?.subscribe ({ _rxBleDeviceServices ->
                    rxBleDeviceServices = _rxBleDeviceServices
                    bleGattServices = _rxBleDeviceServices.bluetoothGattServices
                },{ discoverServicesThrowable->
                    discoverServicesThrowable.printStackTrace()
                })

            },{ connectionThrowable->
                connectionThrowable.printStackTrace()
            })

    /**
     * Notification
     */
    fun bleNotification() = rxBleConnection
        ?.setupNotification(UUID.fromString(CHARACTERISTIC_RESPONSE_STRING))
        ?.doOnNext { notificationObservable->
            // Notification has been set up
        }
        ?.flatMap { notificationObservable -> notificationObservable }


    /**
     * Write Data
     */
    fun writeData(sendByteData: ByteArray) = rxBleConnection?.writeCharacteristic(
        UUID.fromString(CHARACTERISTIC_COMMAND_STRING),
        sendByteData
    )
}

