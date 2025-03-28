package fr.isen.vincenti.androidsmartdevice.views

import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import fr.isen.vincenti.androidsmartdevice.Device

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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
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

            Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(24.dp))
        }
        if (!isConnected) {
            Text(
                text = "Connexion en cours...",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (ledsState[1] == true) Icons.Filled.Lightbulb else Icons.Outlined.Lightbulb,
                    contentDescription = "LED Bleue",
                    tint = Color.Blue,
                    modifier = Modifier
                        .size(72.dp)
                        .clickable {
                            ledsState[1] = !(ledsState[1] ?: false)
                            onLedToggle(1)
                            ledsState[2] = false
                            ledsState[3] = false
                        }
                )

                Icon(
                    imageVector = if (ledsState[2] == true) Icons.Filled.Lightbulb else Icons.Outlined.Lightbulb,
                    contentDescription = "LED Verte",
                    tint = Color.Green,
                    modifier = Modifier
                        .size(72.dp)
                        .clickable {
                            ledsState[2] = !(ledsState[2] ?: false)
                            onLedToggle(2)
                            ledsState[1] = false
                            ledsState[3] = false
                        }
                )

                Icon(
                    imageVector = if (ledsState[3] == true) Icons.Filled.Lightbulb else Icons.Outlined.Lightbulb,
                    contentDescription = "LED Rouge",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(72.dp)
                        .clickable {
                            ledsState[3] = !(ledsState[3] ?: false)
                            onLedToggle(3)
                            ledsState[1] = false
                            ledsState[2] = false
                        }
                )
            }
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isChecked1, onCheckedChange = onCheckedChange1)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Abonnez-vous au compteur du bouton 1")
            }
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isChecked3, onCheckedChange = onCheckedChange3)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Abonnez-vous au compteur du bouton 3")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Bouton 1", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(text = "$cptb1", color = Color.White, fontSize = 24.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Bouton 3", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(text = "$cptb3", color = Color.White, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}