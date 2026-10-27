package dev.stepan.nacrelauncher

object AppCatalog {
    fun <T> filterApps(apps: List<LauncherApp<T>>, query: String): List<LauncherApp<T>> {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isEmpty()) return apps

        return apps.filter { app ->
            app.label.contains(normalizedQuery, ignoreCase = true) ||
                app.packageName.contains(normalizedQuery, ignoreCase = true)
        }
    }

    fun <T> dockApps(
        apps: List<LauncherApp<T>>,
        usageCounts: Map<String, Int>,
        limit: Int = 5,
    ): List<LauncherApp<T>> {
        require(limit >= 0) { "Dock limit must not be negative." }
        return apps.sortedWith(
            compareByDescending<LauncherApp<T>> { usageCounts[it.packageName] ?: 0 }
                .thenBy(String.CASE_INSENSITIVE_ORDER) { it.label }
                .thenBy { it.packageName },
        ).take(limit)
    }

    fun <T> favoriteApps(
        apps: List<LauncherApp<T>>,
        favoritePackageNames: Set<String>,
    ): List<LauncherApp<T>> =
        apps.asSequence()
            .filter { it.packageName in favoritePackageNames }
            .distinctBy { it.packageName }
            .sortedWith(
                compareBy<LauncherApp<T>, String>(String.CASE_INSENSITIVE_ORDER) { it.label }
                    .thenBy { it.packageName },
            )
            .toList()

    fun updateFavoritePackageNames(
        favoritePackageNames: Set<String>,
        packageName: String,
        isFavorite: Boolean,
    ): Set<String> =
        if (isFavorite) {
            favoritePackageNames + packageName
        } else {
            favoritePackageNames - packageName
        }
}
