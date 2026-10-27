package dev.stepan.nacrelauncher

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.ColorDrawable
import android.app.Application
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LauncherHomeTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val apps: List<LauncherApp<Drawable>> = listOf(
        LauncherApp("Maps", "com.example.maps", ColorDrawable(Color.RED)),
        LauncherApp("Music", "com.example.music", ColorDrawable(Color.BLUE)),
    )

    @Test
    fun desktopOpensLibraryAndReturnRestoresDesktop() {
        setLauncherHome()

        composeRule.onNodeWithText("Browse all apps").assertIsDisplayed().performClick()
        composeRule.onNodeWithText("App library").assertIsDisplayed()
        composeRule.onNodeWithText("Back to desktop").performClick()
        composeRule.onNodeWithText("Browse all apps").assertIsDisplayed()
        composeRule.onAllNodesWithText("App library").assertCountEquals(0)
    }

    @Test
    fun librarySearchFiltersAppsAndAppClickLaunchesSelection() {
        var launchedPackage: String? = null
        setLauncherHome(onLaunch = { launchedPackage = it.packageName })

        composeRule.onNodeWithText("Browse all apps").performClick()
        composeRule.onNode(hasSetTextAction()).performTextInput("Maps")
        composeRule.onNodeWithText("Maps").assertIsDisplayed().performClick()

        composeRule.runOnIdle {
            assertTrue(launchedPackage == "com.example.maps")
        }
        composeRule.onAllNodesWithText("Music").assertCountEquals(0)
    }

    @Test
    fun pinningInLibraryAddsAppToDesktopDock() {
        setLauncherHome()

        composeRule.onNodeWithText("Browse all apps").performClick()
        composeRule.onNodeWithText("Pin Maps").performClick()
        composeRule.onNodeWithText("Back to desktop").performClick()

        composeRule.onNodeWithText("Maps").assertIsDisplayed()
        composeRule.onAllNodesWithText("Your dock is empty. Browse all apps to pin favorites.")
            .assertCountEquals(0)
    }

    @Test
    fun favoriteDockIsVerticalAtRightAndShortcutsLaunch() {
        var launchedPackage: String? = null
        setLauncherHome(
            favorites = apps.map { it.packageName }.toSet(),
            onLaunch = { launchedPackage = it.packageName },
        )

        val dockBounds = composeRule.onNodeWithTag("favorite-dock").fetchSemanticsNode().boundsInRoot
        val mapsBounds = composeRule.onNodeWithTag("dock-app-com.example.maps").fetchSemanticsNode().boundsInRoot
        val musicBounds = composeRule.onNodeWithTag("dock-app-com.example.music").fetchSemanticsNode().boundsInRoot
        val rootBounds = composeRule.onRoot().fetchSemanticsNode().boundsInRoot

        assertTrue("Dock should be taller than it is wide", dockBounds.height > dockBounds.width)
        assertTrue("Dock should be on the right half of the desktop", dockBounds.center.x > rootBounds.center.x)
        assertEquals(mapsBounds.center.x, musicBounds.center.x, 1f)
        assertTrue(mapsBounds.center.y < musicBounds.center.y)
        composeRule.onAllNodesWithText("Maps").assertCountEquals(0)

        composeRule.onNodeWithTag("dock-app-com.example.maps").performClick()
        composeRule.runOnIdle {
            assertEquals("com.example.maps", launchedPackage)
        }
    }

    @Test
    fun desktopKeepsDateButOmitsHeadlineAndSubtitle() {
        setLauncherHome()

        composeRule.onNodeWithTag("home-date").assertIsDisplayed()
        composeRule.onAllNodesWithText("Make room for what matters.").assertCountEquals(0)
        composeRule.onAllNodesWithText("Your apps, close at hand.").assertCountEquals(0)
    }

    @Test
    fun dockCanScrollToFavoritesBeyondVisibleHeight() {
        val manyApps: List<LauncherApp<Drawable>> = (1..20).map { index ->
            LauncherApp(
                label = "App $index",
                packageName = "com.example.app$index",
                icon = ColorDrawable(Color.RED),
            )
        }
        setLauncherHome(
            apps = manyApps,
            favorites = manyApps.map { it.packageName }.toSet(),
        )

        composeRule.onNodeWithTag("favorite-dock")
            .performScrollToNode(hasTestTag("dock-app-com.example.app20"))
        composeRule.onNodeWithTag("dock-app-com.example.app20").assertIsDisplayed()
    }

    @Test
    fun settingsToggleShowsDockLabels() {
        setLauncherHome(favorites = setOf("com.example.maps"))

        composeRule.onAllNodesWithText("Maps").assertCountEquals(0)
        composeRule.onNodeWithText("Settings").performClick()
        composeRule.onNodeWithText("Show app names in dock").assertIsDisplayed()
        composeRule.onNodeWithTag("show-dock-labels-switch").performClick()
        composeRule.onNodeWithText("Back to desktop").performClick()
        composeRule.onNodeWithText("Maps").assertIsDisplayed()
    }

    @Test
    fun dockLabelSettingIsPersistedAcrossViewModelRecreation() {
        val application = InstrumentationRegistry.getInstrumentation()
            .targetContext.applicationContext as Application
        val preferences = application.getSharedPreferences("launcher_usage", 0)
        preferences.edit().remove("show_dock_labels").commit()

        val initialViewModel = LauncherViewModel(application)
        assertFalse(initialViewModel.showDockLabels.value)
        initialViewModel.setShowDockLabels(true)

        val recreatedViewModel = LauncherViewModel(application)
        assertTrue(recreatedViewModel.showDockLabels.value)
        preferences.edit().remove("show_dock_labels").commit()
    }

    private fun setLauncherHome(
        apps: List<LauncherApp<Drawable>> = this.apps,
        favorites: Set<String> = emptySet(),
        onLaunch: (LauncherApp<Drawable>) -> Unit = {},
    ) {
        composeRule.setContent {
            var favoritePackageNames by remember { mutableStateOf(favorites) }
            var showDockLabels by remember { mutableStateOf(false) }
            MaterialTheme {
                LauncherHome(
                    apps = apps,
                    favoritePackageNames = favoritePackageNames,
                    onFavoriteToggle = { packageName, isFavorite ->
                        favoritePackageNames = AppCatalog.updateFavoritePackageNames(
                            favoritePackageNames,
                            packageName,
                            isFavorite,
                        )
                    },
                    showDockLabels = showDockLabels,
                    onShowDockLabelsChange = { showDockLabels = it },
                    onLaunch = onLaunch,
                )
            }
        }
    }
}
