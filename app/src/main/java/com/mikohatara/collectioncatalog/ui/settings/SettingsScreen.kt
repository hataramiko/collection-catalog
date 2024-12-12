package com.mikohatara.collectioncatalog.ui.settings

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.components.ItemScreenModifiers
import com.mikohatara.collectioncatalog.ui.components.RedirectDialog
import com.mikohatara.collectioncatalog.ui.components.SettingsTopAppBar
import com.mikohatara.collectioncatalog.util.getSortByText
import java.util.Locale

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        viewModel = viewModel,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    uiState: SettingsUiState,
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SettingsTopAppBar(
                onBack = onBack,
                scrollBehavior
            )
        },
        content = { innerPadding ->
            SettingsScreenContent(
                uiState = uiState,
                viewModel = viewModel,
                onClickSettings = { showDialog = true },
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
    if (showDialog) {
        RedirectDialog(
            message = stringResource(R.string.language_redirect_text),
            onConfirm = {
                showDialog = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    viewModel.redirectToLanguageSettings(context)
                }
            },
            onCancel = { showDialog = false }
        )
    }
}

@Composable
private fun SettingsScreenContent(
    uiState: SettingsUiState,
    viewModel: SettingsViewModel,
    onClickSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SettingsButton(
            label = stringResource(R.string.default_sort_by),
            onClick = { /*TODO*/ },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.rounded_swap_vert),
                    contentDescription = null,
                    modifier = ItemScreenModifiers.icon
                )
            },
            text = getSortByText(uiState.defaultSortBy)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            SettingsDivider()
            SettingsButton(
                label = stringResource(R.string.language),
                onClick = onClickSettings,
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.rounded_language),
                        contentDescription = null,
                        modifier = ItemScreenModifiers.icon
                    )
                },
                text = Locale.getDefault().displayLanguage.takeIf { !it.isNullOrEmpty() }
            )
        }
    }
}

@Composable
private fun SettingsButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    text: String? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        if (icon != null) icon() else Spacer(modifier = Modifier.size(64.dp))
        Column {
            Text(
                label,
                fontSize = 20.sp
            )
            text?.let {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun SettingsDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier = modifier.padding(horizontal = 24.dp))
}
