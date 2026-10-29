package org.change360.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.change360.app.ui.navigation.RootNavHost
import org.change360.app.ui.theme.Change360Theme

/**
 * Host Activity for CHANGE360.
 *
 * Configures edge-to-edge system window insets handling and hosts the root Compose UI container.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            Change360Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Change360App()
                }
            }
        }
    }
}

/**
 * Root Composable entry point for CHANGE360.
 * Hosts the root navigation hierarchy (RootNavHost).
 */
@Composable
fun Change360App() {
    RootNavHost()
}
