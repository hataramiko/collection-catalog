package com.mikohatara.collectioncatalog.ui.help

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.components.EndOfList
import com.mikohatara.collectioncatalog.ui.components.HelpTopAppBar
import com.mikohatara.collectioncatalog.ui.components.Loading
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogNavigationActions
import com.mikohatara.collectioncatalog.util.getDateExample
import com.mikohatara.collectioncatalog.util.getFileNameForExport

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
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

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
                helpPage = uiState.helpPage,
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
            HelpPage.DEFAULT -> item { LandingPage(navActions = navActions) }
            HelpPage.IMPORT -> item { ImportPage(viewModel = viewModel, context = context) }
        }
    }
}

@Composable
private fun LandingPage(
    navActions: CollectionCatalogNavigationActions,
    modifier: Modifier = Modifier
) {
    Spacer(modifier = Modifier.height(16.dp))
    LandingPageButton(
        text = stringResource(R.string.import_dialog_title),
        painter = painterResource(R.drawable.rounded_download_24),
        onClick = { navActions.navigateToHelpScreen(HelpPage.IMPORT) },
        modifier = modifier
    )
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

    HelpPageTitle(stringResource(R.string.import_dialog_title))
    HelpPageNotTranslated()
    HelpPageParagraph(stringResource(R.string.help_import_p1))
    HelpPageParagraph(stringResource(R.string.help_import_p2))
    HelpPageHeader(stringResource(R.string.help_import_first_row))
    HelpPageParagraph(stringResource(R.string.help_import_p3))
    HelpPageTextButton(
        text = stringResource(R.string.import_show_first_row),
        onClick = { /*TODO*/ }
    )
    HelpPageTextButton(
        text = stringResource(R.string.import_copy_first_row),
        onClick = { viewModel.copyImportFirstRowToClipboard(context) }
    )
    HelpPageHeader(stringResource(R.string.help_import_next_rows))
    HelpPageParagraph(stringResource(R.string.help_import_p4))
    HelpPageParagraph(stringResource(R.string.help_import_p5))
    HelpPageTextButton(
        text = stringResource(R.string.import_copy_empty_row),
        onClick = { viewModel.copyImportEmptyRowToClipboard(context) }
    )
    HelpPageHeader(stringResource(R.string.help_import_use_template))
    HelpPageParagraph(stringResource(R.string.help_import_p6))
    HelpPageTextButton(
        text = stringResource(R.string.import_download_template),
        onClick = {
            val fileName = getFileNameForExport("ImportTemplate") //TODO replace w/ localized string
            createImportTemplateCsvForExport.launch(fileName)
        }
    )
    HelpPageHeader(stringResource(R.string.help_import_use_spreadsheet))
    HelpPageParagraph(stringResource(R.string.help_import_p7))
    HelpPageHeader(stringResource(R.string.attention))
    HelpPageParagraph(stringResource(R.string.info_date_format, getDateExample()))
    HelpPageHorizontalDivider()
    HelpPageParagraph(stringResource(R.string.help_import_currency))
    HelpPageParagraph(stringResource(R.string.help_import_currency_with_decimal))
    HelpPageParagraph(stringResource(R.string.help_import_currency_non_decimal))
    HelpPageHorizontalDivider()
    HelpPageParagraph(stringResource(R.string.help_import_other_numerals))
    EndOfList()
}

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
private fun HelpPageParagraph(text: String) {//, color: Color = LocalContentColor.current) {
    Text(
        text = text,
        //color = color,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
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
private fun HelpPageNotTranslated() {
    val language = LocalContext.current.resources.configuration.locales[0].language

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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(colorScheme.secondaryContainer),
            modifier = Modifier.size(40.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    tint = colorScheme.onSecondaryContainer
                )
            }
        }
        Text(
            text = text,
            fontSize = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun HelpPageHorizontalDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        color = colorScheme.outlineVariant.copy(alpha = 0.5f),
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
