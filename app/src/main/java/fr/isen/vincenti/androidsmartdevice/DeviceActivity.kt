package fr.isen.vincenti.androidsmartdevice

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
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
import fr.isen.vincenti.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme
import fr.isen.vincenti.androidsmartdevice.views.DeviceScreen
import fr.isen.vincenti.androidsmartdevice.views.TopBar

class DeviceActivity : ComponentActivity() {
    private var bluetoothGatt: BluetoothGatt? = null
    private val isLedOn = mutableStateMapOf<Int, Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var device = Device()

        device.name = intent.getStringExtra("device_name").toString()
        device.macaddress = intent.getStringExtra("device_mac_address").toString()
        device.signal = intent.getIntExtra("device_signal", 0)

        val isConnected = mutableStateOf(false)

        enableEdgeToEdge()
        setContent {
            AndroidSmartDeviceTheme {
                Scaffold(
                    topBar = { TopBar() },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    DeviceScreen(
                        Modifier.padding(innerPadding), context = this, device = device, isConnected = isConnected.value, onLedToggle = { ledId -> toggleLed(ledId) }
                    )
                }
            }
        }
        connectToDevice(device.macaddress, isConnected)
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(macAddress: String, isConnected: androidx.compose.runtime.MutableState<Boolean>) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val device: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(macAddress)

        bluetoothGatt = device?.connectGatt(this, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                if (newState == android.bluetooth.BluetoothProfile.STATE_CONNECTED) {
                    Log.i("BLE", "Connected to GATT server.")
                    isConnected.value = true
                    gatt?.discoverServices()
                } else if (newState == android.bluetooth.BluetoothProfile.STATE_DISCONNECTED) {
                    Log.i("BLE", "Disconnected from GATT server.")
                    isConnected.value = false
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
            val command = when (ledId) {
                1 -> 0x01
                2 -> 0x02
                3 -> 0x03
                else -> 0x00
            }

            if (isLedOn[ledId] == true) {
                characteristic.value = byteArrayOf(0x00)
                isLedOn[ledId] = false
            } else {
                characteristic.value = byteArrayOf(command.toByte())
                isLedOn[ledId] = true
            }

            gatt.writeCharacteristic(characteristic)
        }
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