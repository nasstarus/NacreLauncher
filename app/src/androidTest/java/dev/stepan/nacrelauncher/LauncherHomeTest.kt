package dev.stepan.nacrelauncher

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.ColorDrawable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
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

        composeRule.onNodeWithText("Maps").assertExists()
        composeRule.onAllNodesWithText("Your dock is empty. Browse all apps to pin favorites.")
            .assertCountEquals(0)
    }

    private fun setLauncherHome(
        onLaunch: (LauncherApp<Drawable>) -> Unit = {},
    ) {
        composeRule.setContent {
            var favorites by remember { mutableStateOf(emptySet<String>()) }
            MaterialTheme {
                LauncherHome(
                    apps = apps,
                    favoritePackageNames = favorites,
                    onFavoriteToggle = { packageName, isFavorite ->
                        favorites = AppCatalog.updateFavoritePackageNames(
                            favorites,
                            packageName,
                            isFavorite,
                        )
                    },
                    onLaunch = onLaunch,
                )
            }
        }
    }
}
