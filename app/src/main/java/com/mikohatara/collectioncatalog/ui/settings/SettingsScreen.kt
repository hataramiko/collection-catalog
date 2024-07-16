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
    SettingsScreenContent(
        onBack = onBack
    )
}

@Composable
fun SettingsScreenContent(
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
            SettingsBody(
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
}

@Composable
private fun SettingsBody(
    modifier: Modifier = Modifier
) {

}
