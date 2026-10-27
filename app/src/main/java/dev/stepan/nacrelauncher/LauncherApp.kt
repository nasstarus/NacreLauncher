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

    private val _favoritePackageNames = MutableStateFlow(
        preferences.getStringSet(FAVORITES_KEY, emptySet()).orEmpty().toSet(),
    )
    val favoritePackageNames: StateFlow<Set<String>> = _favoritePackageNames.asStateFlow()

    private val _showDockLabels = MutableStateFlow(
        preferences.getBoolean(SHOW_DOCK_LABELS_KEY, false),
    )
    val showDockLabels: StateFlow<Boolean> = _showDockLabels.asStateFlow()

    init {
        val preferencesEditor = preferences.edit()
        preferences.all.keys
            .filter { it.startsWith(USAGE_PREFIX) }
            .forEach(preferencesEditor::remove)
        preferencesEditor.apply()
    }

    fun refreshApps() {
        viewModelScope.launch {
            _apps.value = withContext(Dispatchers.IO) {
                loadLaunchableApps()
            }
        }
    }

    fun pinApp(packageName: String) {
        updateFavorite(packageName, isFavorite = true)
    }

    fun unpinApp(packageName: String) {
        updateFavorite(packageName, isFavorite = false)
    }

    fun setShowDockLabels(show: Boolean) {
        if (_showDockLabels.value == show) return
        preferences.edit().putBoolean(SHOW_DOCK_LABELS_KEY, show).apply()
        _showDockLabels.value = show
    }

    fun launchApp(packageName: String): Boolean {
        val context = getApplication<Application>()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ?: return false
        return try {
            context.startActivity(intent)
            true
        } catch (_: android.content.ActivityNotFoundException) {
            false
        } catch (_: SecurityException) {
            false
        }
    }

    private fun updateFavorite(packageName: String, isFavorite: Boolean) {
        val updatedFavorites = AppCatalog.updateFavoritePackageNames(
            _favoritePackageNames.value,
            packageName,
            isFavorite,
        )
        if (updatedFavorites == _favoritePackageNames.value) return

        preferences.edit().putStringSet(FAVORITES_KEY, updatedFavorites.toSet()).apply()
        _favoritePackageNames.value = updatedFavorites.toSet()
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
            .sortedWith(
                compareBy<LauncherApp<Drawable>, String>(String.CASE_INSENSITIVE_ORDER) { it.label }
                    .thenBy { it.packageName },
            )
            .toList()
    }

    private companion object {
        const val PREFERENCES_NAME = "launcher_usage"
        const val FAVORITES_KEY = "favorite_package_names"
        const val SHOW_DOCK_LABELS_KEY = "show_dock_labels"
        const val USAGE_PREFIX = "launch_count:"
    }
}
