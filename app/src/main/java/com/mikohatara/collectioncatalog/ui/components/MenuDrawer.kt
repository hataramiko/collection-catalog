package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Collection
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations
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
                onCloseDrawer = { coroutineScope.launch { drawerState.close() } }
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
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet {
        LazyColumn {
            item {
                Header()
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.plates)) },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.rounded_newsstand),
                                contentDescription = null,
                            )
                        },
                        selected = currentRoute == CollectionCatalogDestinations.HOME_ROUTE,
                        onClick = {
                            navActions.navigateToHomeScreen()
                            onCloseDrawer()
                        },
                        modifier = modifier.padding(top = 8.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 8.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.collections),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = { onEditCollections() },
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .requiredHeight(36.dp)
                        ) {
                            Text(
                                stringResource(R.string.edit)
                            )
                        }
                    }
                }
            }
            items(items = collectionList, key = { it.id }) { collection ->
                NavigationDrawerItem(
                    label = { Text(collection.name) },
                    icon = {
                        if (collection.emoji != null) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Text(collection.emoji)
                            }
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.rounded_bookmark),
                                contentDescription = null
                            )
                        }
                    },
                    selected = false,
                    onClick = {},
                    modifier = modifier.padding(horizontal = 12.dp)
                )
            }
            item {
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.create_collection)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null
                        )
                    },
                    selected = false,
                    onClick = { onAddCollection() },
                    modifier = modifier.padding(horizontal = 12.dp)
                )
            }
            item {
                HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            }
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.wishlist)) },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.rounded_list_alt),
                                contentDescription = null
                            )
                        },
                        selected = currentRoute == CollectionCatalogDestinations.WISHLIST_ROUTE,
                        onClick = {
                            navActions.navigateToWishlistScreen()
                            onCloseDrawer()
                        },
                        modifier = modifier
                    )
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.archive)) },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.rounded_history),
                                contentDescription = null
                            )
                        },
                        selected = currentRoute == CollectionCatalogDestinations.ARCHIVE_ROUTE,
                        onClick = {
                            navActions.navigateToArchiveScreen()
                            onCloseDrawer()
                        },
                        modifier = modifier
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 8.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.statistics)) },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.rounded_analytics),
                                contentDescription = null
                            )
                        },
                        selected = currentRoute == CollectionCatalogDestinations.STATS_ROUTE,
                        onClick = {
                            navActions.navigateToStatsScreen()
                            onCloseDrawer()
                        },
                        modifier = modifier
                    )
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.settings)) },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.rounded_settings),
                                contentDescription = null
                            )
                        },
                        selected = false,
                        onClick = { navActions.navigateToSettingsScreen() },
                        modifier = modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Header() {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Collection Catalog",
            modifier = Modifier.padding(32.dp)
        )
    }
    HorizontalDivider(modifier = Modifier.fillMaxWidth())
}
