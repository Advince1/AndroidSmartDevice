package fr.isen.vincenti.androidsmartdevice.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.vincenti.androidsmartdevice.models.Device

@Composable
fun DeviceScreen(
    modifier: Modifier = Modifier,
    device: Device,
    isConnected: Boolean,
    onLedToggle: (Int) -> Unit,
    isChecked1: Boolean,
    isChecked3: Boolean,
    onCheckedChange1: (Boolean) -> Unit,
    onCheckedChange3: (Boolean) -> Unit,
    cptb1: Int,
    cptb3: Int
) {
    val ledsState = remember { mutableStateMapOf(1 to false, 2 to false, 3 to false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = device.name.ifBlank { "Nom inconnu" },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Adresse MAC :",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = device.macaddress,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Signal :",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${device.signal} dBm",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (!isConnected) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Connexion en cours...",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Text(
                text = "Contrôle des LEDs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LedIcon(1, ledsState[1] == true, Color.Blue) {
                    ledsState[1] = !ledsState[1]!!
                    onLedToggle(1)
                    ledsState[2] = false
                    ledsState[3] = false
                }
                LedIcon(2, ledsState[2] == true, Color.Green) {
                    ledsState[2] = !ledsState[2]!!
                    onLedToggle(2)
                    ledsState[1] = false
                    ledsState[3] = false
                }
                LedIcon(3, ledsState[3] == true, Color.Red) {
                    ledsState[3] = !ledsState[3]!!
                    onLedToggle(3)
                    ledsState[1] = false
                    ledsState[2] = false
                }
            }

            SubscriptionCheckbox(
                label = "Compteur bouton 1",
                checked = isChecked1,
                onCheckedChange = onCheckedChange1
            )

            SubscriptionCheckbox(
                label = "Compteur bouton 3",
                checked = isChecked3,
                onCheckedChange = onCheckedChange3
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CounterCard("Bouton 1", cptb1)
                CounterCard("Bouton 3", cptb3)
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = value)
    }
}

@Composable
fun LedIcon(id: Int, isOn: Boolean, color: Color, onClick: () -> Unit) {
    Icon(
        imageVector = if (isOn) Icons.Filled.Lightbulb else Icons.Outlined.Lightbulb,
        contentDescription = "LED $id",
        tint = color,
        modifier = Modifier
            .size(64.dp)
            .clickable { onClick() }
    )
}

@Composable
fun SubscriptionCheckbox(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label)
    }
}

@Composable
fun CounterCard(label: String, value: Int) {
    Box(
        modifier = Modifier
            .size(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, color = Color.White, fontWeight = FontWeight.Bold)
            Text(text = "$value", color = Color.White, fontSize = 52.sp)
        }
    }
}
