package fr.isen.vincenti.androidsmartdevice

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.isen.vincenti.androidsmartdevice.models.Device
import fr.isen.vincenti.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme
import fr.isen.vincenti.androidsmartdevice.views.DeviceScreen
import fr.isen.vincenti.androidsmartdevice.views.TopBar

class DeviceActivity : ComponentActivity() {
    private var bluetoothGatt: BluetoothGatt? = null
    private val isLedOn = mutableStateMapOf<Int, Boolean>()
    private val cptb1 = mutableStateOf(0)
    private val cptb3 = mutableStateOf(0)
    private var notifCharButton1: BluetoothGattCharacteristic? = null
    private var notifCharButton3: BluetoothGattCharacteristic? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var device = Device()

        device.name = intent.getStringExtra("device_name").toString()
        device.macaddress = intent.getStringExtra("device_mac_address").toString()
        device.signal = intent.getIntExtra("device_signal", 0)

        val isConnected = mutableStateOf(false)
        val isChecked1 = mutableStateOf(false)
        val isChecked3 = mutableStateOf(false)


        enableEdgeToEdge()
        setContent {
            AndroidSmartDeviceTheme {
                Scaffold(
                    topBar = { TopBar() },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    DeviceScreen(
                        Modifier.padding(innerPadding),
                        device = device,
                        isConnected = isConnected.value,
                        onLedToggle = { ledId -> toggleLed(ledId) },
                        isChecked1 = isChecked1.value,
                        isChecked3 = isChecked3.value,
                        onCheckedChange1 = {
                            toggleNotificationsFor(
                                notifCharButton1,
                                it
                            ); isChecked1.value = it
                        },
                        onCheckedChange3 = {
                            toggleNotificationsFor(
                                notifCharButton3,
                                it
                            ); isChecked3.value = it
                        },
                        cptb1 = cptb1.value,
                        cptb3 = cptb3.value
                    )
                }
            }
        }
        connectToDevice(device.macaddress, isConnected)
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(
        macAddress: String,
        isConnected: androidx.compose.runtime.MutableState<Boolean>
    ) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val device: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(macAddress)

        bluetoothGatt = device?.connectGatt(this, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.i("BLE", "Connected to GATT server.")
                    isConnected.value = true
                    gatt?.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.i("BLE", "Disconnected from GATT server.")
                    isConnected.value = false
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)
                notifCharButton1 = gatt?.services?.getOrNull(3)?.characteristics?.getOrNull(0)
                notifCharButton3 = gatt?.services?.getOrNull(2)?.characteristics?.getOrNull(1)
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?
            ) {
                super.onCharacteristicChanged(gatt, characteristic)
                characteristic?.let {
                    when (it.uuid) {
                        notifCharButton1?.uuid -> {
                            val value = it.value.firstOrNull()?.toInt() ?: return
                            cptb1.value = value
                            Log.d("BLE", "Bouton 1 = $value")
                        }

                        notifCharButton3?.uuid -> {
                            val value = it.value.firstOrNull()?.toInt() ?: return
                            cptb3.value = value
                            Log.d("BLE", "Bouton 3 = $value")
                        }

                        else -> {
                            Log.w(
                                "BLE",
                                "Notification reçue d'une caractéristique inconnue : ${it.uuid}"
                            )
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    @SuppressLint("MissingPermission")
    private fun toggleLed(ledId: Int) {
        bluetoothGatt?.let { gatt ->
            val characteristic = gatt.services[2].characteristics[0]
            val currentlyOn = isLedOn[ledId] ?: false
            val command = if (currentlyOn) {
                0x00
            } else {
                when (ledId) {
                    1 -> 0x01
                    2 -> 0x02
                    3 -> 0x03
                    else -> 0x00
                }
            }
            isLedOn[ledId] = !currentlyOn
            characteristic.value = byteArrayOf(command.toByte())
            gatt.writeCharacteristic(characteristic)
            if (!currentlyOn) {
                isLedOn.keys.forEach { key ->
                    if (key != ledId) isLedOn[key] = false
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun toggleNotificationsFor(
        characteristic: BluetoothGattCharacteristic?,
        enable: Boolean
    ) {
        if (characteristic == null) return

        bluetoothGatt?.setCharacteristicNotification(characteristic, enable)

        val descriptor = characteristic.getDescriptor(
            characteristic.descriptors.firstOrNull()?.uuid ?: return
        )

        descriptor.value = if (enable)
            BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        else
            BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE

        bluetoothGatt?.writeDescriptor(descriptor)

    }

}


@Composable
fun Greeting3(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    AndroidSmartDeviceTheme {
        Greeting3("Android")
    }
}