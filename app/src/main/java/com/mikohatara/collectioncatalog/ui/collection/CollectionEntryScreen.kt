package com.mikohatara.collectioncatalog.ui.collection

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.components.CollectionEntryTopAppBar
import com.mikohatara.collectioncatalog.ui.components.CollectionListTopAppBar
import com.mikohatara.collectioncatalog.ui.components.DeletionDialog
import com.mikohatara.collectioncatalog.ui.components.DiscardDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CollectionEntryScreen(
    viewModel: CollectionEntryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    CollectionEntryScreen(
        uiState = uiState,
        viewModel = viewModel,
        coroutineScope = coroutineScope,
        onValueChange = viewModel::updateUiState,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectionEntryScreen(
    uiState: CollectionEntryUiState,
    viewModel: CollectionEntryViewModel,
    coroutineScope: CoroutineScope,
    onValueChange: (CollectionDetails) -> Unit,
    onBack: () -> Unit
) {
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    var showDeletionDialog by rememberSaveable { mutableStateOf(false) }
    val onBackBehavior = { if (uiState.hasUnsavedChanges) showDiscardDialog = true else onBack() }
    val topAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
    val topBarTitle: String = if (!uiState.isNew) { // Could be .collectionDetails.name below
        stringResource(R.string.edit_item_title, uiState.collection?.name ?: "")
    } else {
        stringResource(R.string.create_collection)
    }
    BackHandler(enabled = true) {
        onBackBehavior()
    }

    Scaffold(
        topBar = {
            if (uiState.isNew) {
                CollectionListTopAppBar(
                    title = topBarTitle,
                    onBack = onBackBehavior,
                    colors = topAppBarColors
                )
            } else {
                CollectionEntryTopAppBar(
                    title = topBarTitle,
                    onBack = onBackBehavior,
                    onDelete = { showDeletionDialog = true },
                    colors = topAppBarColors
                )
            }
        },
        content = { innerPadding ->
            CollectionEntryScreenContent(
                uiState = uiState,
                modifier = Modifier.padding(innerPadding),
                onValueChange = onValueChange,
                onSave = {
                    viewModel.saveEntry()
                    onBack()
                }
            )
        }
    )
    if (showDiscardDialog) {
        DiscardDialog(
            onConfirm = {
                showDiscardDialog = false
                onBack()
            },
            onCancel = { showDiscardDialog = false }
        )
    }
    if (showDeletionDialog) {
        DeletionDialog(
            onConfirm = {
                showDeletionDialog = false
                coroutineScope.launch {
                    viewModel.deleteCollection()
                    onBack()
                }
            },
            onCancel = { showDeletionDialog = false }
        )
    }
}

@Composable
private fun CollectionEntryScreenContent(
    uiState: CollectionEntryUiState,
    modifier: Modifier = Modifier,
    onValueChange: (CollectionDetails) -> Unit = {},
    onSave: () -> Unit
) {
    val (saveButtonIcon, saveButtonText) = when (uiState.isNew) {
        true -> painterResource(R.drawable.rounded_save) to stringResource(
            R.string.save_added_item, uiState.collectionDetails.name ?: ""
        )
        false -> painterResource(R.drawable.rounded_save_as) to stringResource(
            R.string.save_edited_item, uiState.collectionDetails.name ?: ""
        )
    }

    Column(
        modifier = modifier
    ) {
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 24.dp,
                bottomEnd = 24.dp
            )
        ) {
            Row(
                modifier = Modifier.padding(24.dp)
            ) {
                OutlinedTextField(
                    value = uiState.collectionDetails.emoji ?: "",
                    onValueChange = {
                        onValueChange(uiState.collectionDetails.copy(emoji = it))
                    },
                    label = {
                        Text(
                            stringResource(R.string.emoji),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    singleLine = true,
                    modifier = Modifier.weight(0.33f)
                )
                Spacer(
                    modifier = Modifier.width(8.dp)
                )
                OutlinedTextField(
                    value = uiState.collectionDetails.name ?: "",
                    onValueChange = { onValueChange(uiState.collectionDetails.copy(name = it)) },
                    label = {
                        Text(
                            stringResource(R.string.collection),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            //Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onSave,
                enabled = uiState.isValidEntry,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Icon(
                    painter = saveButtonIcon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(saveButtonText)
            }
        }
    }
}
