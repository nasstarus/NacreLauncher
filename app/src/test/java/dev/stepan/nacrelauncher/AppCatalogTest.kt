package dev.stepan.nacrelauncher

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
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
    fun `dock ranks most launched apps first then sorts ties by label`() {
        val usageCounts = mapOf(
            "com.example.maps" to 2,
            "com.example.mail" to 8,
            "com.example.music" to 8,
        )

        assertEquals(
            listOf(apps[1], apps[2], apps[0]),
            AppCatalog.dockApps(apps, usageCounts),
        )
    }

    @Test
    fun `dock sorts equal usage and label ties by package name regardless of input order`() {
        val alphaPackageApp = app("Tools", "com.example.alpha")
        val zetaPackageApp = app("Tools", "com.example.zeta")
        val usageCounts = mapOf(
            alphaPackageApp.packageName to 4,
            zetaPackageApp.packageName to 4,
        )
        val expected = listOf(alphaPackageApp, zetaPackageApp)

        assertEquals(expected, AppCatalog.dockApps(listOf(zetaPackageApp, alphaPackageApp), usageCounts))
        assertEquals(expected, AppCatalog.dockApps(listOf(alphaPackageApp, zetaPackageApp), usageCounts))
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
    fun `dock limits app count`() {
        assertEquals(
            listOf(apps[1]),
            AppCatalog.dockApps(apps, mapOf("com.example.mail" to 1), limit = 1),
        )
    }

    @Test
    fun `negative dock limit is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            AppCatalog.dockApps(apps, emptyMap(), limit = -1)
        }
    }

    private fun app(label: String, packageName: String) =
        LauncherApp(label, packageName, icon = Unit)
}
