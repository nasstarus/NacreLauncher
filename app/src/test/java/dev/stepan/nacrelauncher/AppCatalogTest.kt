package dev.stepan.nacrelauncher

import org.junit.Assert.assertEquals
import org.junit.Test

class AppCatalogTest {
    private val apps = listOf(
        app("Maps", "com.example.maps"),
        app("Mail", "com.example.mail"),
        app("Music", "com.example.music"),
    )

    @Test
    fun `filter matches label regardless of case and surrounding spaces`() {
        assertEquals(
            listOf(apps[0]),
            AppCatalog.filterApps(apps, "  mApS "),
        )
    }

    @Test
    fun `filter matches package name`() {
        assertEquals(
            listOf(apps[1]),
            AppCatalog.filterApps(apps, "EXAMPLE.MAIL"),
        )
    }

    @Test
    fun `blank filter preserves app list and its order`() {
        assertEquals(apps, AppCatalog.filterApps(apps, "  "))
    }

    @Test
    fun `favorite dock contains only installed favorites once in deterministic order`() {
        val zeta = app("Tools", "com.example.zeta")
        val alpha = app("Tools", "com.example.alpha")
        val mail = app("Mail", "com.example.mail")
        val favorites = setOf(zeta.packageName, alpha.packageName, "com.example.uninstalled")
        val expected = listOf(alpha, zeta)

        assertEquals(expected, AppCatalog.favoriteApps(listOf(zeta, mail, alpha), favorites))
        assertEquals(expected, AppCatalog.favoriteApps(listOf(alpha, zeta, mail), favorites))
        assertEquals(expected, AppCatalog.favoriteApps(listOf(zeta, zeta, alpha, mail), favorites))
    }

    @Test
    fun `pinning and unpinning favorites is idempotent`() {
        val initiallyEmpty = emptySet<String>()
        val pinned = AppCatalog.updateFavoritePackageNames(initiallyEmpty, "com.example.mail", true)

        assertEquals(setOf("com.example.mail"), pinned)
        assertEquals(pinned, AppCatalog.updateFavoritePackageNames(pinned, "com.example.mail", true))
        assertEquals(emptySet<String>(), AppCatalog.updateFavoritePackageNames(pinned, "com.example.mail", false))
        assertEquals(initiallyEmpty, AppCatalog.updateFavoritePackageNames(initiallyEmpty, "com.example.mail", false))
    }

    private fun app(label: String, packageName: String) =
        LauncherApp(label, packageName, icon = Unit)
}
