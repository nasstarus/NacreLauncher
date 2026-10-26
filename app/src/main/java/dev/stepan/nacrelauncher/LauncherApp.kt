package dev.stepan.nacrelauncher

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LauncherApp<T>(
    val label: String,
    val packageName: String,
    val icon: T,
)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = application.getSharedPreferences(PREFERENCES_NAME, 0)
    private val _apps = MutableStateFlow<List<LauncherApp<Drawable>>>(emptyList())
    val apps: StateFlow<List<LauncherApp<Drawable>>> = _apps.asStateFlow()

    private val _usageCounts = MutableStateFlow(readUsageCounts())
    val usageCounts: StateFlow<Map<String, Int>> = _usageCounts.asStateFlow()

    fun refreshApps() {
        viewModelScope.launch {
            _apps.value = withContext(Dispatchers.IO) {
                loadLaunchableApps()
            }
        }
    }

    fun launchApp(packageName: String): Boolean {
        val context = getApplication<Application>()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ?: return false
        return try {
            context.startActivity(intent)
            val updatedCount = (_usageCounts.value[packageName] ?: 0) + 1
            preferences.edit().putInt(usageKey(packageName), updatedCount).apply()
            _usageCounts.value = _usageCounts.value + (packageName to updatedCount)
            true
        } catch (_: android.content.ActivityNotFoundException) {
            false
        } catch (_: SecurityException) {
            false
        }
    }

    private fun loadLaunchableApps(): List<LauncherApp<Drawable>> {
        val context = getApplication<Application>()
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            .asSequence()
            .filter { it.activityInfo.packageName != context.packageName }
            .distinctBy { it.activityInfo.packageName }
            .map { resolveInfo ->
                LauncherApp(
                    label = resolveInfo.loadLabel(context.packageManager).toString(),
                    packageName = resolveInfo.activityInfo.packageName,
                    icon = resolveInfo.loadIcon(context.packageManager),
                )
            }
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.label }.thenBy { it.packageName })
            .toList()
    }

    private fun readUsageCounts(): Map<String, Int> =
        preferences.all.mapNotNull { (key, value) ->
            if (key.startsWith(USAGE_PREFIX) && value is Int) {
                key.removePrefix(USAGE_PREFIX) to value
            } else {
                null
            }
        }.toMap()

    private fun usageKey(packageName: String) = "$USAGE_PREFIX$packageName"

    private companion object {
        const val PREFERENCES_NAME = "launcher_usage"
        const val USAGE_PREFIX = "launch_count:"
    }
}
