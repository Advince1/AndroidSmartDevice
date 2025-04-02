package fr.isen.vincenti.androidsmartdevice.views

import android.bluetooth.BluetoothAdapter
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.isen.vincenti.androidsmartdevice.models.Device

@Composable
fun ScanScreen(
    modifier: Modifier,
    devices: List<Device>,
    isScanning: Boolean,
    onScanToggle: () -> Unit,
    onDeviceClick: (Device) -> Unit,
    context: Context
) {
    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

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
            Text(text = if (isScanning) "Scan en cours..." else "Lancer le scan BLE")
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(50)
                    )
                    .clickable {
                        when {
                            bluetoothAdapter == null -> Toast
                                .makeText(context, "Bluetooth non disponible", Toast.LENGTH_SHORT)
                                .show()
                            !bluetoothAdapter.isEnabled -> Toast
                                .makeText(context, "Bluetooth non activé", Toast.LENGTH_SHORT)
                                .show()
                            else -> onScanToggle()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isScanning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isScanning) "Pause" else "Démarrer",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(42.dp)
                )
            }
        }
        if (isScanning) {
            androidx.compose.material3.LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        LazyColumn {
            items(devices.size) { index ->
                DeviceItem(
                    device = devices[index],
                    modifier = Modifier.clickable { onDeviceClick(devices[index]) }
                )
                HorizontalDivider(modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun DeviceItem(device: Device, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)),
        ) {
            Text(
                text = "${device.signal}",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
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