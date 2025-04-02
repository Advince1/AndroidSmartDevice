package fr.isen.vincenti.androidsmartdevice

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
import fr.isen.vincenti.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme
import fr.isen.vincenti.androidsmartdevice.views.BottomBar
import fr.isen.vincenti.androidsmartdevice.views.HomeScreen
import fr.isen.vincenti.androidsmartdevice.views.TopBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this@MainActivity
        enableEdgeToEdge()
        setContent {
            AndroidSmartDeviceTheme {
                Scaffold(
                    topBar = { TopBar() },
                    bottomBar = { BottomBar(context) },
                    modifier =
                        Modifier.fillMaxSize()) { innerPadding ->
                        HomeScreen(modifier = Modifier.padding(innerPadding), context = context)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidSmartDeviceTheme {
        Greeting("Android")
    }
}