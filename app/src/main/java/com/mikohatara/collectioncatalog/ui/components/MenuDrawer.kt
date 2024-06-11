package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogNavigationActions
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme
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
    currentRoute: String,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet {
        Header()
        NavigationDrawerItem(
            label = { Text("Collection") },
            icon = { Icon(
                painter = painterResource(R.drawable.rounded_database),
                contentDescription = null
            ) },
            selected = currentRoute == CollectionCatalogDestinations.HOME_ROUTE,
            onClick = {
                // TODO Navigation to Wishlist and Former and such
                onCloseDrawer()
            },
            modifier = modifier
        )
        NavigationDrawerItem(
            label = { Text("Statistics") },
            icon = { Icon(
                painter = painterResource(R.drawable.rounded_analytics),
                contentDescription = null
            ) },
            selected = false,
            onClick = {
                // TODO Navigation to Statistics
                onCloseDrawer()
            },
            modifier = modifier
        )
        Spacer(modifier = modifier.weight(1f))
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.settings)) },
            icon = { Icon(
                painter = painterResource(R.drawable.rounded_settings),
                contentDescription = null
            ) },
            selected = false,
            onClick = {
                // TODO Navigation to Settings
                onCloseDrawer()
            },
            modifier = modifier
        )
    }
}

@Composable
private fun Header() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            //.background(Color(0, 0, 0, 13))
    ) {
        Text(
            text = "Collection Catalog",
            modifier = Modifier.padding(32.dp)
        )
    }
}

@Preview
@Composable
fun MenuDrawerPreview() {
    CollectionCatalogTheme {
        //ModalMenuDrawer()
    }
}