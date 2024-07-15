package com.mikohatara.collectioncatalog.ui.stats

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import com.mikohatara.collectioncatalog.ui.components.StatsTopAppBar

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit
) {
    StatsScreenContent(
        onOpenDrawer = onOpenDrawer
    )
}

@Composable
fun StatsScreenContent(
    onOpenDrawer: () -> Unit
) {
    Scaffold(
        modifier = Modifier,//.nestedScroll(),
        topBar = {
            StatsTopAppBar(
                onOpenDrawer = onOpenDrawer
            )
        },
        content = { innerPadding ->
            StatsBody(
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
}

@Composable
private fun StatsBody(
    modifier: Modifier = Modifier
) {

}
