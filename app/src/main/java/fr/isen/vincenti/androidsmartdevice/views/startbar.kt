package fr.isen.vincenti.androidsmartdevice.views

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.isen.vincenti.androidsmartdevice.ScanActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBar(context: Context) {
    Button(
        onClick = {
            val intent = Intent(context, ScanActivity::class.java)
            context.startActivity(intent)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(
            text = "Commencer",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}