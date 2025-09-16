package com.mikohatara.collectioncatalog.ui.settings

import android.content.Context
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.components.ItemScreenModifiers
import com.mikohatara.collectioncatalog.ui.components.Loading
import com.mikohatara.collectioncatalog.ui.components.RedirectDialog
import com.mikohatara.collectioncatalog.ui.components.SettingsBottomSheet
import com.mikohatara.collectioncatalog.ui.components.SettingsTopAppBar
import com.mikohatara.collectioncatalog.util.getLocale
import com.mikohatara.collectioncatalog.util.toDisplayCountry
import java.util.Locale

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    uiState?.let {
        SettingsScreen(
            viewModel = viewModel,
            uiState = it,
            context = context,
            onBack = onBack
        )
    } ?: run {
        Loading()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    viewModel: SettingsViewModel,
    uiState: SettingsUiState,
    context: Context,
    onBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var showCountryDialog by rememberSaveable { mutableStateOf(false) }
    var showRedirectDialog by rememberSaveable { mutableStateOf(false) }

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
                onClickCountry = { showCountryDialog = true },
                onClickLanguage = { showRedirectDialog = true },
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
    if (showCountryDialog) {
        /*SettingsDialog(
            label = stringResource(R.string.user_country),
            options = Locale.getAvailableLocales()
                .map { it.displayCountry }
                .distinct()
                .filter { it.isNotEmpty() }
                .sorted(),
            selectedOption = uiState.userCountry.toDisplayCountry(),
            onToggleSelection = { viewModel.setUserCountry(it) },
            onDismiss = { showCountryDialog = false },
            infoText = stringResource(R.string.user_country_info)
        )*/
        SettingsBottomSheet(
            label = stringResource(R.string.user_country),
            context = context,
            options = Locale.getAvailableLocales()
                .map { it.displayCountry }
                .distinct()
                .filter { it.isNotEmpty() }
                .sorted(),
            selectedOption = uiState.userCountry.toDisplayCountry(),
            onToggleSelection = { viewModel.setUserCountry(it) },
            onDismiss = { showCountryDialog = false },
            infoText = stringResource(R.string.user_country_info)
        )
    }
    if (showRedirectDialog) {
        RedirectDialog(
            message = stringResource(R.string.language_redirect_text),
            onConfirm = {
                showRedirectDialog = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    viewModel.redirectToLanguageSettings(context)
                }
            },
            onCancel = { showRedirectDialog = false }
        )
    }
}

@Composable
private fun SettingsScreenContent(
    uiState: SettingsUiState,
    onClickCountry: () -> Unit,
    onClickLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userCountry = uiState.userCountry
    val currentLocale = getLocale(userCountry)
    val displayCountry = currentLocale.getDisplayCountry(Locale.getDefault())

    Column(modifier = modifier) {
        SettingsButton(
            label = stringResource(R.string.user_country),
            onClick = onClickCountry,
            painter = painterResource(R.drawable.rounded_globe),
            text = displayCountry
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            SettingsButton(
                label = stringResource(R.string.language),
                onClick = onClickLanguage,
                painter = painterResource(R.drawable.rounded_language),
                text = Locale.getDefault().displayLanguage.takeIf { !it.isNullOrEmpty() }
            )
        }
        Version()
    }
}

@Composable
private fun SettingsButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    painter: Painter? = null,
    text: String? = null
) {
    val (labelColor, valueColor) = if (enabled) {
        MaterialTheme.colorScheme.onBackground to MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant to MaterialTheme.colorScheme.outline
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() }
            .padding(8.dp)
    ) {
        if (painter != null) {
            Icon(
                painter = painter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = ItemScreenModifiers.icon
            )
        } else Spacer(modifier = Modifier.size(64.dp))
        Column {
            Text(
                text = label,
                color = labelColor,
                fontSize = 20.sp
            )
            text?.let {
                Text(
                    text = text,
                    color = valueColor
                )
            }
        }
    }
}

@Composable
private fun Version() {
    val packageManager = LocalContext.current.packageManager
    val packageName = LocalContext.current.packageName
    val versionName = packageManager.getPackageInfo(packageName, 0).versionName
    //val versionCode = getLongVersionCode(packageManager.getPackageInfo(packageName, 0))

    SettingsDivider()
    SettingsButton(
        label = stringResource(R.string.version),
        onClick = {},
        enabled = false,
        text = "$versionName"// ($versionCode)"
    )
}

@Composable
private fun SettingsDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier = modifier.padding(horizontal = 24.dp, vertical = 12.dp))
}
