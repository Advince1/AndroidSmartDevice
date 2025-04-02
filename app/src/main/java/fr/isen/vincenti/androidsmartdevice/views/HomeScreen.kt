package fr.isen.vincenti.androidsmartdevice.views

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "Bienvenue dans votre application Android Smart Device",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Pour démarrer vos interactions avec les appareils BLE environnants, cliquer sur commencer",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Icon(
            imageVector = Icons.Default.Bluetooth,
            contentDescription = "Bluetooth Icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(128.dp)
        )
    }
}