package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogNavigationActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ModalMenuDrawer(
    drawerState: DrawerState,
    currentRoute: String,
    navActions: CollectionCatalogNavigationActions,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    screenContent: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuDrawerContent(
                navActions = navActions,
                currentRoute = currentRoute,
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
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet {
        Header()
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            NavigationDrawerItem(
                label = { Text(stringResource(R.string.plates)) },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.rounded_database),
                        contentDescription = null
                    )
                },
                selected = currentRoute == CollectionCatalogDestinations.HOME_ROUTE,
                onClick = {
                    navActions.navigateToHomeScreen()
                    onCloseDrawer()
                },
                modifier = modifier
            )
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                Text(
                    text = stringResource(R.string.collections),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = {},
                    modifier = Modifier.requiredHeight(35.dp),
                    enabled = false,
                ) {
                    Text(
                        stringResource(R.string.edit)
                    )
                }
            }
            NavigationDrawerItem(
                label = { Text(stringResource(R.string.create_collection)) },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null
                    )
                },
                selected = false,
                onClick = {
                    //onCloseDrawer()
                },
                modifier = modifier.alpha(0.5f)
            )
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
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
                selected = false,
                onClick = {
                    //onCloseDrawer()
                },
                modifier = modifier.alpha(0.5f)
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
                onClick = {
                    navActions.navigateToSettingsScreen()
                    onCloseDrawer()
                },
                modifier = modifier
            )
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
