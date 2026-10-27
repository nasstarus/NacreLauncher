package dev.stepan.nacrelauncher

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun LauncherHome(
    apps: List<LauncherApp<Drawable>>,
    favoritePackageNames: Set<String>,
    onFavoriteToggle: (String, Boolean) -> Unit,
    onLaunch: (LauncherApp<Drawable>) -> Unit,
    showDockLabels: Boolean = false,
    onShowDockLabelsChange: (Boolean) -> Unit = {},
) {
    var query by rememberSaveable { mutableStateOf("") }
    var screen by rememberSaveable { mutableStateOf(HomeScreen.Desktop) }
    val dockApps = AppCatalog.favoriteApps(apps, favoritePackageNames)
    val dateLabel = LocalDate.now().format(
        DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault()),
    )

    when (screen) {
        HomeScreen.Desktop -> DesktopHome(
            dateLabel = dateLabel,
            dockApps = dockApps,
            showDockLabels = showDockLabels,
            onLaunch = onLaunch,
            onOpenLibrary = { screen = HomeScreen.Library },
            onOpenSettings = { screen = HomeScreen.Settings },
        )
        HomeScreen.Library -> AppLibrary(
            apps = apps,
            query = query,
            onQueryChange = { query = it },
            favoritePackageNames = favoritePackageNames,
            onFavoriteToggle = onFavoriteToggle,
            onLaunch = onLaunch,
            onBack = { screen = HomeScreen.Desktop },
        )
        HomeScreen.Settings -> SettingsScreen(
            showDockLabels = showDockLabels,
            onShowDockLabelsChange = onShowDockLabelsChange,
            onBack = { screen = HomeScreen.Desktop },
        )
    }
}

private enum class HomeScreen {
    Desktop,
    Library,
    Settings,
}

@Composable
private fun DesktopHome(
    dateLabel: String,
    dockApps: List<LauncherApp<Drawable>>,
    showDockLabels: Boolean,
    onLaunch: (LauncherApp<Drawable>) -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = dateLabel.uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.testTag("home-date"),
            )
            TextButton(onClick = onOpenSettings) {
                Text(stringResource(R.string.settings))
            }
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd,
        ) {
            if (dockApps.isEmpty()) {
                Text(
                    text = stringResource(R.string.empty_favorites),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary,
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
                    modifier = Modifier
                        .widthIn(min = 88.dp, max = 88.dp)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .testTag("favorite-dock"),
                ) {
                    items(dockApps.size, key = { dockApps[it].packageName }) { index ->
                        AppShortcut(
                            app = dockApps[index],
                            showLabel = showDockLabels,
                            onClick = { onLaunch(dockApps[index]) },
                        )
                    }
                }
            }
        }

        Button(
            onClick = onOpenLibrary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
        ) {
            Text(stringResource(R.string.open_app_library))
        }
    }
}

@Composable
private fun SettingsScreen(
    showDockLabels: Boolean,
    onShowDockLabelsChange: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onBack) {
                Text(stringResource(R.string.return_to_desktop))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onShowDockLabelsChange(!showDockLabels) }
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.show_dock_app_names),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = showDockLabels,
                onCheckedChange = onShowDockLabelsChange,
                modifier = Modifier.testTag("show-dock-labels-switch"),
            )
        }
    }
}

@Composable
private fun AppLibrary(
    apps: List<LauncherApp<Drawable>>,
    query: String,
    onQueryChange: (String) -> Unit,
    favoritePackageNames: Set<String>,
    onFavoriteToggle: (String, Boolean) -> Unit,
    onLaunch: (LauncherApp<Drawable>) -> Unit,
    onBack: () -> Unit,
) {
    val filteredApps = AppCatalog.filterApps(apps, query)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.app_library),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onBack) {
                Text(stringResource(R.string.return_to_desktop))
            }
        }

        SearchBox(
            query = query,
            onQueryChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.all_apps),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = filteredApps.size.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        if (filteredApps.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (apps.isEmpty()) {
                        stringResource(R.string.no_launchable_apps)
                    } else {
                        stringResource(R.string.no_matching_apps, query)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 112.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(filteredApps, key = { it.packageName }) { app ->
                    AppGridItem(
                        app = app,
                        isFavorite = app.packageName in favoritePackageNames,
                        onFavoriteToggle = { onFavoriteToggle(app.packageName, it) },
                        onClick = { onLaunch(app) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBox(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.search_label),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelSmall,
        )
        Spacer(Modifier.width(10.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = stringResource(R.string.search_apps),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            )
        }
    }
}

@Composable
private fun AppShortcut(
    app: LauncherApp<Drawable>,
    showLabel: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(72.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = app.label
                role = Role.Button
            }
            .testTag("dock-app-${app.packageName}")
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppIcon(app = app, size = 52.dp)
        if (showLabel) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = app.label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun AppGridItem(
    app: LauncherApp<Drawable>,
    isFavorite: Boolean,
    onFavoriteToggle: (Boolean) -> Unit,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .clickable(onClick = onClick)
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AppIcon(app = app, size = 48.dp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = app.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        TextButton(onClick = { onFavoriteToggle(!isFavorite) }) {
            Text(
                stringResource(
                    if (isFavorite) R.string.unpin_app else R.string.pin_app,
                    app.label,
                ),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun AppIcon(app: LauncherApp<Drawable>, size: androidx.compose.ui.unit.Dp) {
    val icon = remember(app.icon) {
        app.icon.toBitmap(width = 96, height = 96).asImageBitmap()
    }
    Surface(
        modifier = Modifier.size(size),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Image(
            bitmap = icon,
            contentDescription = null,
            modifier = Modifier.padding(4.dp),
        )
    }
}
