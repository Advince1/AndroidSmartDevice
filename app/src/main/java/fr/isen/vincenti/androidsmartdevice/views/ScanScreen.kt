package fr.isen.vincenti.androidsmartdevice.views

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.isen.vincenti.androidsmartdevice.Device

@Composable
fun ScanScreen(modifier: Modifier, context: Context) {

    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner

    var isScanning by remember { mutableStateOf(false) }
    val devices = remember { mutableStateListOf<Device>() }

    val scanCallback = remember {
        object : ScanCallback() {
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

            override fun onBatchScanResults(results: List<ScanResult>) {
                results.forEach { result ->
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
            }

            override fun onScanFailed(errorCode: Int) {
                Toast.makeText(context, "Scan échoué: $errorCode", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun toggleScan() {
        if (isScanning) {
            bluetoothLeScanner?.stopScan(scanCallback)
        } else {
            devices.clear()
            bluetoothLeScanner?.startScan(scanCallback)
        }
        isScanning = !isScanning
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isScanning) "Scan en cours..." else "Lancer le scan BLE",
            )
            Icon(
                imageVector = if (isScanning) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp)
                    .clickable {
                        when {
                            bluetoothAdapter == null -> Toast.makeText(context, "Bluetooth non disponible", Toast.LENGTH_SHORT).show()
                            !bluetoothAdapter.isEnabled -> Toast.makeText(context, "Bluetooth non activé", Toast.LENGTH_SHORT).show()
                            else -> toggleScan()
                        }
                    }
            )
        }
        LazyColumn {
            items(devices.size) { index ->
                DeviceItem(devices[index])
                HorizontalDivider(modifier = Modifier.padding(8.dp))
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            bluetoothLeScanner?.stopScan(scanCallback)
        }
    }
}

@Composable
fun DeviceItem(device: Device) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .background(MaterialTheme.colorScheme.primary,shape = RoundedCornerShape(8.dp)),
        ) {
            Text(
                text = "${device.signal}",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White ,
                modifier = Modifier.padding(16.dp)
            )
        }
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = device.name.ifBlank { "Nom inconnu" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Adresse MAC : ${device.macaddress}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}