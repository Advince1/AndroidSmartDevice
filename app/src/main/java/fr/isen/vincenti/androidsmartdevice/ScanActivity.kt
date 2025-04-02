package fr.isen.vincenti.androidsmartdevice

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import fr.isen.vincenti.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme
import fr.isen.vincenti.androidsmartdevice.views.ScanScreen
import fr.isen.vincenti.androidsmartdevice.views.TopBar
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.isen.vincenti.androidsmartdevice.models.Device


class ScanActivity : ComponentActivity() {
    private val REQUEST_CODE_BLUETOOTH_PERMISSIONS = 1

    private val bluetoothLeScanner by lazy {
        (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter.bluetoothLeScanner
    }

    val devices = mutableStateListOf<Device>()
    var isScanning by mutableStateOf(false)
    private val handler = Handler(Looper.getMainLooper())

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val deviceName = result.device.name
            if (!deviceName.isNullOrBlank()) {
                val newDevice = Device(
                    signal = result.rssi,
                    name = deviceName,
                    macaddress = result.device.address
                )
                if (devices.none { it.macaddress == newDevice.macaddress }) {
                    devices.add(newDevice)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("BLE_SCAN", "Scan échoué: $errorCode")
        }
    }

    @SuppressLint("MissingPermission")
    fun toggleScan() {
        if (!hasAllPermissions()) {
            ActivityCompat.requestPermissions(this, checkAndRequestBluetoothPermissions(), REQUEST_CODE_BLUETOOTH_PERMISSIONS)
            Toast.makeText(this, "Permissions manquantes", Toast.LENGTH_SHORT).show()
            return
        }

        if (isScanning) {
            bluetoothLeScanner.stopScan(scanCallback)
            handler.removeCallbacksAndMessages(null)
            isScanning = false
        } else {
            devices.clear()
            bluetoothLeScanner.startScan(scanCallback)
            isScanning = true

            handler.postDelayed({
                bluetoothLeScanner.stopScan(scanCallback)
                isScanning = false
            }, 10000)
        }
    }

    fun navigateToDeviceConnection(device: Device) {
        val intent = Intent(this, DeviceActivity::class.java).apply {
            putExtra("device_mac_address", device.macaddress)
            putExtra("device_name", device.name)
            putExtra("device_signal", device.signal)
        }
        startActivity(intent)
    }

    @SuppressLint("MissingPermission")
    override fun onStop() {
        super.onStop()
        bluetoothLeScanner.stopScan(scanCallback)
        handler.removeCallbacksAndMessages(null)
        isScanning = false
    }

    private fun checkAndRequestBluetoothPermissions(): Array<String> {
        return if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            )
        }
        else if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            )
        }
        else{
            arrayOf(
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            )
        }
    }

    private fun hasAllPermissions(): Boolean {
        val permissions = checkAndRequestBluetoothPermissions()
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestBluetoothPermissions()
        enableEdgeToEdge()

        setContent {
            AndroidSmartDeviceTheme {
                Scaffold(
                    topBar = { TopBar() },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    ScanScreen(
                        modifier = Modifier.padding(innerPadding),
                        devices = devices,
                        isScanning = isScanning,
                        onScanToggle = { toggleScan() },
                        onDeviceClick = { device -> navigateToDeviceConnection(device) },
                        context = this
                    )
                }
            }
        }
    }
}


@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    AndroidSmartDeviceTheme {
        Greeting2("Android")
    }
}