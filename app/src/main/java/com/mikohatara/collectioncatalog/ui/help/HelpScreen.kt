package com.mikohatara.collectioncatalog.ui.help

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.components.HelpTopAppBar
import com.mikohatara.collectioncatalog.ui.components.Loading
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogNavigationActions

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

    Scaffold(
        modifier = Modifier,
        topBar = {
            HelpTopAppBar(
                helpPage = uiState.helpPage,
                onBack = onBack,
                scrollBehavior = scrollBehavior
            )
        },
        content = { innerPadding ->
            HelpScreenContent(
                uiState = uiState,
                navActions = navActions,
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
}

@Composable
private fun HelpScreenContent(
    uiState: HelpUiState,
    navActions: CollectionCatalogNavigationActions,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        when (uiState.helpPage) {
            HelpPage.DEFAULT -> LandingPage(navActions = navActions)
            HelpPage.IMPORT -> ImportPage()
        }
    }
}

@Composable
private fun LandingPage(
    navActions: CollectionCatalogNavigationActions,
    modifier: Modifier = Modifier
) {
    Spacer(modifier = Modifier.height(16.dp))
    HelpPageButton(
        text = stringResource(R.string.import_text),
        painter = painterResource(R.drawable.rounded_download_24),
        onClick = { navActions.navigateToHelpScreen(HelpPage.IMPORT) },
        modifier = modifier
    )
}

@Composable
private fun ImportPage() {
    ArticleHeader(stringResource(R.string.import_text))
}

@Composable
private fun ArticleHeader(text: String) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun HelpPageButton(
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
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
            modifier = Modifier.size(40.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
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
