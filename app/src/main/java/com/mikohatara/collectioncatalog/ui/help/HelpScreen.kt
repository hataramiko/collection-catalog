package com.mikohatara.collectioncatalog.ui.help

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.components.CardButton
import com.mikohatara.collectioncatalog.ui.components.CardGroup
import com.mikohatara.collectioncatalog.ui.components.CardGroupSpacer
import com.mikohatara.collectioncatalog.ui.components.EndOfList
import com.mikohatara.collectioncatalog.ui.components.HelpTopAppBar
import com.mikohatara.collectioncatalog.ui.components.Loading
import com.mikohatara.collectioncatalog.ui.components.OpenUrlDialog
import com.mikohatara.collectioncatalog.ui.components.RedirectDialog
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogNavigationActions
import com.mikohatara.collectioncatalog.util.getDateExample
import com.mikohatara.collectioncatalog.util.getFileNameForExport

private object Url {
    const val PRIVACY_POLICY = "https://hataramiko.github.io/rekkary/privacy-policy.html"
    //const val SEND_FEEDBACK = "https://forms.gle/wfK6dfLMHQxnEw169"
    const val SEND_FEEDBACK = "https://docs.google.com/forms/d/e/1FAIpQLScRNaiJSxO-83v3Y4SU0zZdU5ldrl0spurOfLdrYs2rz7oXIA/viewform?usp=dialog"
    const val RATE = "market://details?id=com.mikohatara.collectioncatalog"
}

@Composable
fun HelpScreen(
    viewModel: HelpViewModel = hiltViewModel(),
    navActions: CollectionCatalogNavigationActions,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    uiState?.let {
        HelpScreen(
            viewModel = viewModel,
            uiState = it,
            context = context,
            navActions = navActions,
            onBack = onBack
        )
    } ?: run {
        Loading()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HelpScreen(
    viewModel: HelpViewModel,
    uiState: HelpUiState,
    context: Context,
    navActions: CollectionCatalogNavigationActions,
    onBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(key1 = uiState.downloadResult) {
        uiState.downloadResult?.let { result ->
            when (result) {
                is DownloadResult.Success -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
                is DownloadResult.Failure -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
            viewModel.clearDownloadResult()
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HelpTopAppBar(
                title = viewModel.getTopAppBarTitle(context),
                onBack = onBack,
                scrollBehavior = scrollBehavior
            )
        },
        content = { innerPadding ->
            HelpScreenContent(
                viewModel = viewModel,
                uiState = uiState,
                context = context,
                navActions = navActions,
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
}

@Composable
private fun HelpScreenContent(
    viewModel: HelpViewModel,
    uiState: HelpUiState,
    context: Context,
    navActions: CollectionCatalogNavigationActions,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        when (uiState.helpPage) {
            HelpPage.DEFAULT -> item {
                LandingPage(viewModel = viewModel, navActions = navActions, context = context)
            }
            HelpPage.IMPORT -> item {
                ImportPage(viewModel = viewModel, context = context)
            }
        }
    }
}

@Composable
private fun LandingPage(
    viewModel: HelpViewModel,
    navActions: CollectionCatalogNavigationActions,
    context: Context,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        CardGroup(modifier = Modifier.padding(bottom = 16.dp)) {
            LandingPageButton(
                text = stringResource(R.string.import_dialog_title),
                painter = painterResource(R.drawable.rounded_download_24),
                onClick = { navActions.navigateToHelpScreen(HelpPage.IMPORT) }
            )
        }
        CardGroup(modifier = Modifier.padding(bottom = 16.dp)) {
            LandingPageLink(
                text = stringResource(R.string.send_feedback),
                dialogMessage = stringResource(R.string.url_redirect_to_feedback),
                painter = painterResource(R.drawable.rounded_forward_to_inbox_24),
                url = Url.SEND_FEEDBACK,
                context = context,
                viewModel = viewModel
            )
            CardGroupSpacer()
            LandingPageLink(
                text = stringResource(R.string.rate),
                dialogMessage = stringResource(R.string.url_redirect_to_rating),
                painter = painterResource(R.drawable.rounded_reviews_24),
                url = Url.RATE,
                context = context,
                viewModel = viewModel
            )
        }
        CardGroup(modifier = Modifier.padding(bottom = 16.dp)) {
            LandingPageLink(
                text = stringResource(R.string.privacy_policy),
                painter = painterResource(R.drawable.rounded_privacy_tip_24),
                url = Url.PRIVACY_POLICY,
                context = context,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun ImportPage(
    viewModel: HelpViewModel,
    context: Context
) {
    val createImportTemplateCsvForExport = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
        onResult = { uri: Uri? ->
            uri?.let {
                viewModel.downloadImportTemplate(context, it)
            }
        }
    )

    HelpPageNotTranslated()
    HelpPageParagraph(stringResource(R.string.help_import_a_p1))
    HelpPageParagraph(stringResource(R.string.help_import_a_p2))
    HelpPageParagraph(stringResource(R.string.help_import_a_p3))
    HelpPageParagraph(stringResource(R.string.help_import_a_p4))
    HelpPageHeader(stringResource(R.string.help_import_first_row))
    HelpPageParagraph(stringResource(R.string.help_import_first_row_p1))
    HelpPageValueList(viewModel.getImportFirstRowExample())
    HelpPageTextButton(
        text = stringResource(R.string.import_copy_first_row),
        onClick = { viewModel.copyImportFirstRowToClipboard(context) }
    )
    HelpPageHeader(stringResource(R.string.help_import_next_rows))
    HelpPageParagraph(stringResource(R.string.help_import_p4))
    HelpPageTextButton(
        text = stringResource(R.string.import_copy_empty_row),
        onClick = { viewModel.copyImportEmptyRowToClipboard(context) }
    )
    HelpPageHeader(stringResource(R.string.help_import_unused_values))
    HelpPageParagraph(stringResource(R.string.help_import_unused_values_p1))
    HelpPageParagraph(stringResource(R.string.help_import_unused_values_p2))
    HelpPageParagraph(stringResource(R.string.help_import_unused_values_p4))
    HelpPageValueList(
        values = viewModel.getAllPlatesUnusedValues(),
        buttonTextShow = stringResource(R.string.show_values_all_plates),
        buttonTextHide = stringResource(R.string.hide_values_all_plates)
    )
    HelpPageValueList(
        values = viewModel.getArchiveUnusedValues(),
        buttonTextShow = stringResource(R.string.show_values_archive),
        buttonTextHide = stringResource(R.string.hide_values_archive)
    )
    Spacer(modifier = Modifier.height(4.dp))
    HelpPageParagraph(stringResource(R.string.help_import_unused_values_p6))
    HelpPageValueList(
        values = viewModel.getWishlistUsedValues(),
        buttonTextShow = stringResource(R.string.show_values_wishlist),
        buttonTextHide = stringResource(R.string.hide_values_wishlist)
    )
    HelpPageHeader(stringResource(R.string.help_import_use_template))
    HelpPageParagraph(stringResource(R.string.help_import_p6))
    HelpPageTextButton(
        text = stringResource(R.string.import_download_template),
        onClick = { //TODO replace w/ localized string?
            val fileName = getFileNameForExport("ImportTemplate")
            createImportTemplateCsvForExport.launch(fileName)
        }
    )
    HelpPageHeader(stringResource(R.string.help_import_use_spreadsheet))
    HelpPageParagraph(stringResource(R.string.help_import_p7))
    HelpPageHeader("Pay attention")//stringResource(R.string.attention))
    HelpPageSubheader(stringResource(R.string.help_import_dates))
    HelpPageParagraph(stringResource(R.string.info_date_format, getDateExample()))
    HelpPageSubheader(stringResource(R.string.help_import_currencies))
    HelpPageParagraph(stringResource(R.string.help_import_currency))
    HelpPageParagraph(stringResource(R.string.help_import_currency_p2))
    HelpPageValueList(
        values = stringResource(R.string.help_import_currency_two_decimals),
        buttonTextShow = stringResource(R.string.show_examples, "2 decimals"),
        buttonTextHide = stringResource(R.string.hide_examples, "2 decimals")
    )
    HelpPageParagraph(stringResource(R.string.help_import_currency_p3))
    //HelpPageParagraph(stringResource(R.string.help_import_currency_current))
    HelpPageValueList(
        values = stringResource(R.string.help_import_currency_no_decimals),
        buttonTextShow = stringResource(R.string.show_examples, "0 decimals"),
        buttonTextHide = stringResource(R.string.hide_examples, "0 decimals")
    )
    HelpPageValueList(
        values = stringResource(R.string.help_import_currency_three_decimals),
        buttonTextShow = stringResource(R.string.show_examples, "3 decimals"),
        buttonTextHide = stringResource(R.string.hide_examples, "3 decimals")
    )
    HelpPageParagraph(stringResource(R.string.help_import_currencies_warning))
    /*HelpPageValueList(
        values = stringResource(R.string.help_import_measurements_mm_p2),
        buttonTextShow = stringResource(R.string.show_examples, "2 decimals"),
        buttonTextHide = stringResource(R.string.hide_examples, "2 decimals")
    )
    HelpPageValueList(
        values = stringResource(R.string.help_import_measurements_mm_p2),
        buttonTextShow = stringResource(R.string.show_examples, "3 decimals"),
        buttonTextHide = stringResource(R.string.hide_examples, "3 decimals")
    )*/
    //HelpPageParagraph(stringResource(R.string.help_import_currency_with_decimal))
    //HelpPageParagraph(stringResource(R.string.help_import_currency_non_decimal))
    HelpPageSubheader(stringResource(R.string.help_import_measurements))
    //HelpPageParagraph(stringResource(R.string.help_import_measurements_p1))
    HelpPageParagraph(stringResource(R.string.help_import_measurements_mm_p1))
    HelpPageValueList(
        values = stringResource(R.string.help_import_measurements_mm_p2),
        buttonTextShow = stringResource(R.string.show_examples, "mm, g"),
        buttonTextHide = stringResource(R.string.hide_examples, "mm, g")
    )
    HelpPageParagraph(stringResource(R.string.help_import_measurements_in_p1))
    HelpPageParagraph(stringResource(R.string.help_import_measurements_in_warning))
    HelpPageValueList(
        values = stringResource(R.string.help_import_measurements_in_p2),
        buttonTextShow = stringResource(R.string.show_examples, "in, oz"),
        buttonTextHide = stringResource(R.string.hide_examples, "in, oz")
    )
    EndOfList()
}

// This was used before modifying the TopAppBar
// TODO remove?
@Composable
private fun HelpPageTitle(text: String) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun HelpPageHeader(text: String) {
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun HelpPageSubheader(text: String) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun HelpPageParagraph(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current
) {
    Text(
        text = text,
        color = color,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun HelpPageTextButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(64.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            color = colorScheme.primary,
            fontWeight = FontWeight.Medium,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .padding(12.dp)
        )
    }
}

@Composable
private fun HelpPageValueList(
    values: String,
    buttonTextShow: String = stringResource(R.string.show_values),
    buttonTextHide: String = stringResource(R.string.hide_values)
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val buttonText = if (!isExpanded) buttonTextShow else buttonTextHide
    val onClick = remember { Modifier.clickable { isExpanded = !isExpanded } }

    Column(modifier = Modifier.animateContentSize()) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .then(onClick)
        ) {
            Text(
                text = buttonText,
                color = colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(16.dp)
            )
            Icon(
                painter = if (isExpanded) painterResource(R.drawable.rounded_keyboard_arrow_up_24)
                else painterResource(R.drawable.rounded_keyboard_arrow_down_24),
                contentDescription = null,
                tint = colorScheme.secondary,
                modifier = Modifier.padding(16.dp)
            )
        }
        if (isExpanded) {
            HelpPageParagraph(
                text = values,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun HelpPageNotTranslated() {
    val language = LocalConfiguration.current.locales[0].language

    if (language != "en") {
        val color = colorScheme.secondary

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.rounded_error_24),
                contentDescription = null,
                tint = color,
                modifier = Modifier.padding(end = 12.dp)
            )
            Text(
                text = stringResource(R.string.help_page_not_translated),
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun LandingPageButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.rounded_help)
) {
    CardButton(
        label = text,
        onClick = onClick,
        modifier = modifier,
        mainIconPainter = painter,
        trailingIconPainter = painterResource(R.drawable.rounded_chevron_forward)
    )
}

@Composable
private fun LandingPageLink(
    viewModel: HelpViewModel,
    text: String,
    context: Context,
    url: String,
    modifier: Modifier = Modifier,
    dialogMessage: String? = null,
    painter: Painter = painterResource(R.drawable.rounded_help)
) {
    var showRedirectDialog by rememberSaveable { mutableStateOf(false) }
    val onDismissRedirectDialog = { showRedirectDialog = false }
    val openUrlErrorMessage = stringResource(R.string.open_url_error)
    val openUrl: (Context, String) -> Unit = { context, url ->
        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            viewModel.showToast(context, openUrlErrorMessage)
            e.printStackTrace()
        }
    }

    CardButton(
        label = text,
        onClick = { showRedirectDialog = true },
        modifier = modifier,
        mainIconPainter = painter,
        trailingIconPainter = painterResource(R.drawable.rounded_open_in_new_24),
        isMainIconColorTertiary = (url == Url.PRIVACY_POLICY),
        isMainIconColorSecondary = true,
    )

    if (showRedirectDialog) {
        if (dialogMessage != null) {
            RedirectDialog(
                message = dialogMessage,
                onConfirm = {
                    onDismissRedirectDialog()
                    openUrl(context, url)
                },
                onCancel = onDismissRedirectDialog
            )
        } else {
            OpenUrlDialog(
                title = text,
                onConfirm = {
                    onDismissRedirectDialog()
                    openUrl(context, url)
                },
                onCancel = onDismissRedirectDialog
            )
        }
    }
}
