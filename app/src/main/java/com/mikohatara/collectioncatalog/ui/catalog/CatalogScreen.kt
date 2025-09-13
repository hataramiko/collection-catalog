package com.mikohatara.collectioncatalog.ui.catalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CatalogScreen(
    //viewModel: CatalogViewModel = hiltViewModel(),
    onAddItem: () -> Unit,
    onItemClick: (/**/) -> Unit,
    onOpenDrawer: () -> Unit,
    onImportHelp: () -> Unit
) {
    /*val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()*/
    val context = LocalContext.current
}

@Composable
private fun CatalogScreen(

) {

}

@Composable
private fun CatalogScreenContent(

) {

}
