package com.mikohatara.collectioncatalog

import android.icu.text.CaseMap.Title
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mikohatara.collectioncatalog.ui.home.HomeScreen
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionCatalogApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home" /*, graph =*/ ) {
        composable("home") { HomeScreen() }
        // more destinations
        // navGraph?
    }

    //CollectionCatalogNavHost(navController = navController)

    /*CollectionCatalogTheme {
        //CenterAlignedTopAppBar(title = "Collection Catalog", canNavigateBack = true)
        HomeScreen()
    }*/

    //HomeScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterAlignedTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    //scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    CenterAlignedTopAppBar(
        title = { Text(title) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = { navigateUp }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null /*stringResource(string.back_)*/
                    )
                }
            }
        }
    )
}



/*
@Composable
fun NavigationBar() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Collection", "Stats")

    NavigationBar{
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = item) },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { selectedItem = index }
            )
        }
    }
}*/