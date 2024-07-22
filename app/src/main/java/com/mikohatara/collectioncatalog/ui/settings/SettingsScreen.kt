package com.mikohatara.collectioncatalog.ui.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mikohatara.collectioncatalog.ui.components.SettingsTopAppBar

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    SettingsScreen(
        onBack = onBack
    )
}

@Composable
private fun SettingsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier,
        topBar = {
            SettingsTopAppBar(
                onBack = onBack
            )
        },
        content = { innerPadding ->
            SettingsScreenContent(
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
}

@Composable
private fun SettingsScreenContent(
    modifier: Modifier = Modifier
) {

}
