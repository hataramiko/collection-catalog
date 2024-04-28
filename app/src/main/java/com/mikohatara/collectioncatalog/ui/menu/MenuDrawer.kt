package com.mikohatara.collectioncatalog.ui.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme

@Composable
fun MenuDrawer() {
    ModalMenuDrawer()
}

@Composable
private fun ModalMenuDrawer() {
    ModalDrawerSheet {
        Logo(
            /*TODO*/
        )
        NavigationDrawerItem(
            label = { Text(text = "Collection") },
            selected = false,
            onClick = { /*TODO*/ }
        )
        NavigationDrawerItem(
            label = { Text(text = "Wishlist") },
            selected = false,
            onClick = { /*TODO*/ }
        )
        NavigationDrawerItem(
            label = { Text(text = "Former") },
            selected = false,
            onClick = { /*TODO*/ }
        )
        NavigationDrawerItem(
            label = { Text(text = "Statistics") },
            selected = false,
            onClick = { /*TODO*/ }
        )
        /*NavigationDrawerItem(
            label = { Text(text = "New Collection") },
            icon = { Icon(Icons.Default.Add, null) },
            selected = false,
            onClick = { /*TODO*/ }
        )*/
        NavigationDrawerItem(
            label = { Text(text = "Settings") },
            icon = { Icon(Icons.Default.Settings, null) },
            selected = false,
            onClick = { /*TODO*/ }
        )
    }
}

private val tables = listOf(
    //R.string.title_wishlist etc.
    "Collection",
    "Wishlist",
    "Former",
    "Statistics"
)

@Composable
private fun DrawerTest(modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxSize()
            .padding(start = 24.dp, top = 48.dp)
    ) {
        /*Image(
            painter = painterResource(/*R.drawable.LOGO*/),
            contentDescription = null
        )*/
        /*for (everyMenuDrawerItem in tables) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(id = )
            )
        }*/
    }
}

@Composable
private fun Logo() {

}

@Preview
@Composable
fun MenuDrawerPreview() {
    CollectionCatalogTheme {
        MenuDrawer()
    }
}