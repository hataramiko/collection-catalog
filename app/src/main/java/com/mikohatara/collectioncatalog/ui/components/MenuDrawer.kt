package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Collection
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.COLLECTION_ID
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.ARCHIVE_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.HOME_COLLECTION_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.HOME_DEFAULT_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.STATS_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.WISHLIST_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogNavigationActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ModalMenuDrawer(
    drawerState: DrawerState,
    currentRoute: String,
    navActions: CollectionCatalogNavigationActions,
    collectionList: List<Collection>,
    onEditCollections: () -> Unit,
    onAddCollection: () -> Unit,
    navBackStackEntry: NavBackStackEntry? = null,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    screenContent: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuDrawerContent(
                navActions = navActions,
                currentRoute = currentRoute,
                collectionList = collectionList,
                onEditCollections = onEditCollections,
                onAddCollection = onAddCollection,
                onCloseDrawer = { coroutineScope.launch { drawerState.close() } },
                navBackStackEntry = navBackStackEntry
            )
        }
    ) {
        screenContent()
    }
}

@Composable
private fun MenuDrawerContent(
    navActions: CollectionCatalogNavigationActions,
    currentRoute: String,
    collectionList: List<Collection>,
    onEditCollections: () -> Unit,
    onAddCollection: () -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    navBackStackEntry: NavBackStackEntry? = null
) {
    val collectionId = navBackStackEntry?.arguments?.getInt(COLLECTION_ID)

    ModalDrawerSheet {
        LazyColumn {
            item {
                DrawerHeader()
                DrawerDivider()
                DrawerColumn {
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.all_plates)) },
                        icon = { Icon(
                            painter = painterResource(R.drawable.rounded_newsstand),
                            contentDescription = null
                        ) },
                        selected = currentRoute == HOME_DEFAULT_ROUTE,
                        onClick = {
                            navActions.navigateToHomeScreen()
                            onCloseDrawer()
                        }
                    )
                }
                DrawerDivider(hasHorizontalPadding = true)
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.collections),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 28.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = { onEditCollections() },
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .requiredHeight(36.dp)
                    ) {
                        Text(stringResource(R.string.edit))
                    }
                }
            }
            items(items = collectionList, key = { it.id }) { collection ->
                val selected = currentRoute == HOME_COLLECTION_ROUTE &&
                        collectionId == collection.id

                NavigationDrawerItem(
                    label = { Text(
                        text = collection.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    ) },
                    icon = {
                        if (collection.emoji != null) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Text(
                                    text = collection.emoji,
                                    modifier = Modifier.offset(y = (-1.5).dp)
                                )
                            }
                        } else {
                            IconCollectionLabel(color = collection.color.color)
                        }
                    },
                    selected = selected,
                    onClick = {
                        navActions.navigateToHomeScreen(collection.id)
                        onCloseDrawer()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
            item {
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.create_collection)) },
                    icon = { Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null
                    ) },
                    selected = false,
                    onClick = { onAddCollection() },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                DrawerDivider(hasHorizontalPadding = true)
            }
            item {
                DrawerColumn {
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.wishlist)) },
                        icon = { Icon(
                            painter = painterResource(R.drawable.rounded_heart),
                            contentDescription = null
                        ) },
                        selected = currentRoute == WISHLIST_ROUTE,
                        onClick = {
                            navActions.navigateToWishlistScreen()
                            onCloseDrawer()
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.archive)) },
                        icon = { Icon(
                            painter = painterResource(R.drawable.rounded_archive),
                            contentDescription = null
                        ) },
                        selected = currentRoute == ARCHIVE_ROUTE,
                        onClick = {
                            navActions.navigateToArchiveScreen()
                            onCloseDrawer()
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.statistics)) },
                        icon = { Icon(
                            painter = painterResource(R.drawable.rounded_bar_chart),
                            contentDescription = null
                        ) },
                        selected = currentRoute == STATS_ROUTE,
                        onClick = {
                            navActions.navigateToStatsScreen()
                            onCloseDrawer()
                        }
                    )
                }
                DrawerDivider()
                DrawerColumn {
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.settings)) },
                        icon = { Icon(
                            painter = painterResource(R.drawable.rounded_settings),
                            contentDescription = null
                        ) },
                        selected = false,
                        onClick = { navActions.navigateToSettingsScreen() }
                    )
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.help)) },
                        icon = { Icon(
                            painter = painterResource(R.drawable.rounded_help),
                            contentDescription = null
                        ) },
                        selected = false,
                        onClick = { navActions.navigateToHelpScreen() }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun DrawerHeader() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp)) {
            Image(
                painter = painterResource(R.drawable.rekkary_logo_512_slim),
                contentDescription = null,
                colorFilter = androidx.compose.ui.graphics
                    .ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.weight(1.5f))
        }
        /*Text(
            text = stringResource(R.string.app_name),
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp)
        )*/
    }
}

@Composable
private fun DrawerColumn(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        content()
    }
}

@Composable
private fun DrawerDivider(hasHorizontalPadding: Boolean = false) {
    if (hasHorizontalPadding) {
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    } else {
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}
