package com.mikohatara.collectioncatalog.ui.settings

import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.components.CardButton
import com.mikohatara.collectioncatalog.ui.components.CardGroup
import com.mikohatara.collectioncatalog.ui.components.CardGroupSpacer
import com.mikohatara.collectioncatalog.ui.components.Loading
import com.mikohatara.collectioncatalog.ui.components.RedirectDialog
import com.mikohatara.collectioncatalog.ui.components.SettingsBottomSheet
import com.mikohatara.collectioncatalog.ui.components.SettingsTopAppBar
import com.mikohatara.collectioncatalog.util.getLocale
import com.mikohatara.collectioncatalog.util.getMeasurementUnitSymbol
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
    var showLengthUnitDialog by rememberSaveable { mutableStateOf(false) }
    var showWeightUnitDialog by rememberSaveable { mutableStateOf(false) }
    val onDismissCountryDialog = { showCountryDialog = false }
    val onDismissRedirectDialog = { showRedirectDialog = false }
    val onDismissLengthUnitDialog = { showLengthUnitDialog = false }
    val onDismissWeightUnitDialog = { showWeightUnitDialog = false }
    val excludedCountryCodes = setOf("XA", "XB", "001", "150", "419")

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SettingsTopAppBar(
                onBack = onBack,
                scrollBehavior = scrollBehavior
            )
        },
        content = { innerPadding ->
            SettingsScreenContent(
                uiState = uiState,
                onClickCountry = { showCountryDialog = true },
                onClickLanguage = { showRedirectDialog = true },
                onClickLengthUnit = { showLengthUnitDialog = true },
                onClickWeightUnit = { showWeightUnitDialog = true },
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
    if (showCountryDialog) {
        SettingsBottomSheet(
            label = stringResource(R.string.user_country),
            context = context,
            options = Locale.getAvailableLocales()
                .filter { it.country.isNotEmpty() && it.country !in excludedCountryCodes }
                .map { it.displayCountry }
                .distinct()
                .sorted(),
            selectedOption = uiState.userCountry.toDisplayCountry(),
            onToggleSelection = { viewModel.setUserCountry(it) },
            onDismiss = onDismissCountryDialog,
            infoText = stringResource(R.string.user_country_info)
        )
    }
    if (showRedirectDialog) {
        RedirectDialog(
            message = stringResource(R.string.language_redirect_text),
            onConfirm = {
                onDismissRedirectDialog()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    viewModel.redirectToLanguageSettings(context)
                }
            },
            onCancel = onDismissRedirectDialog
        )
    }
    if (showLengthUnitDialog) {
        SettingsBottomSheet(
            label = stringResource(R.string.measurement_length),
            context = context,
            options = listOf("mm", "in"),
            selectedOption = getMeasurementUnitSymbol(uiState.lengthUnit),
            onToggleSelection = { viewModel.setLengthUnit(it) },
            onDismiss = onDismissLengthUnitDialog
        )
    }
    if (showWeightUnitDialog) {
        SettingsBottomSheet(
            label = stringResource(R.string.measurement_weight),
            context = context,
            options = listOf("g", "oz"),
            selectedOption = getMeasurementUnitSymbol(uiState.weightUnit),
            onToggleSelection = { viewModel.setWeightUnit(it) },
            onDismiss = onDismissWeightUnitDialog
        )
    }
}

@Composable
private fun SettingsScreenContent(
    uiState: SettingsUiState,
    onClickCountry: () -> Unit,
    onClickLanguage: () -> Unit,
    onClickLengthUnit: () -> Unit,
    onClickWeightUnit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userCountry = uiState.userCountry
    val currentLocale = getLocale(userCountry)
    val displayCountry = currentLocale.getDisplayCountry(Locale.getDefault())

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            CardGroup(
                label = stringResource(R.string.settings_general),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                CardButton(
                    label = stringResource(R.string.user_country),
                    onClick = onClickCountry,
                    value = displayCountry,
                    mainIconPainter = painterResource(R.drawable.rounded_globe)
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    CardGroupSpacer()
                    CardButton(
                        label = stringResource(R.string.language),
                        onClick = onClickLanguage,
                        value = Locale.getDefault().displayLanguage.takeIf { !it.isNullOrEmpty() },
                        mainIconPainter = painterResource(R.drawable.rounded_language)
                    )
                }
            }
        }
        item {
            CardGroup(
                label = stringResource(R.string.measurement_units),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                CardButton(
                    label = stringResource(R.string.measurement_length),
                    onClick = onClickLengthUnit,
                    value = getMeasurementUnitSymbol(uiState.lengthUnit),
                    mainIconPainter = painterResource(R.drawable.rounded_ruler),
                    isMainIconColorSecondary = true
                )
                CardGroupSpacer()
                CardButton(
                    label = stringResource(R.string.measurement_weight),
                    onClick = onClickWeightUnit,
                    value = getMeasurementUnitSymbol(uiState.weightUnit),
                    mainIconPainter = painterResource(R.drawable.rounded_weight),
                    isMainIconColorSecondary = true
                )
            }
        }
        item {
            CardGroup(modifier = Modifier.padding(bottom = 16.dp)) {
                Version()
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

    CardButton(
        label = stringResource(R.string.version),
        onClick = {},
        enabled = false,
        value = "$versionName"// ($versionCode)"
    )
}
