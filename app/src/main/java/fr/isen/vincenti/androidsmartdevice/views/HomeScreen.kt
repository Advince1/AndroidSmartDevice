package fr.isen.vincenti.androidsmartdevice.views

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.vincenti.androidsmartdevice.ScanActivity

@Composable
fun HomeScreen(modifier: Modifier, context : Context) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenue dans votre application Smart Device",
            modifier = Modifier
                .padding(top = 128.dp, bottom=48.dp),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Pour démarrer vos interactions avec les appareils BLE environnants, cliquer sur commencer",
            modifier = Modifier
                .padding(bottom=16.dp),
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

        Button (
            onClick = {
                val intent = Intent(context, ScanActivity::class.java).apply {}
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 200.dp)
        ) {
            Text(
                text = "Commencer",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}