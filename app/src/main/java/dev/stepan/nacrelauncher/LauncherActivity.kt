package dev.stepan.nacrelauncher

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class LauncherActivity : ComponentActivity() {
    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFFD6CEC0),
                    onPrimary = Color(0xFF302C25),
                    background = Color(0xFF151511),
                    surface = Color(0xFF201F1B),
                    onSurface = Color(0xFFE8E2D8),
                    secondary = Color(0xFFC8BDAA),
                ),
            ) {
                val apps = viewModel.apps.collectAsStateWithLifecycle().value
                val usageCounts = viewModel.usageCounts.collectAsStateWithLifecycle().value
                LauncherHome(
                    apps = apps,
                    usageCounts = usageCounts,
                    onLaunch = { app ->
                        if (!viewModel.launchApp(app.packageName)) {
                            Toast.makeText(
                                this,
                                getString(R.string.launch_error, app.label),
                                Toast.LENGTH_LONG,
                            ).show()
                            viewModel.refreshApps()
                        }
                    },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshApps()
    }
}
