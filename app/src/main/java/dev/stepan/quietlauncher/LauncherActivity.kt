package dev.stepan.quietlauncher

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
                    primary = Color(0xFFA8C7FA),
                    onPrimary = Color(0xFF102F4A),
                    background = Color(0xFF111411),
                    surface = Color(0xFF1A1F1B),
                    onSurface = Color(0xFFE2E3DD),
                    secondary = Color(0xFFB9CCB5),
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
