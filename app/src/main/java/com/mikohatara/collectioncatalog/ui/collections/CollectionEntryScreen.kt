package com.mikohatara.collectioncatalog.ui.collections

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.components.DiscardDialog
import com.mikohatara.collectioncatalog.ui.components.ItemEntryTopAppBar

@Composable
fun CollectionEntryScreen(
    viewModel: CollectionEntryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectionEntryScreen(
        uiState = uiState,
        viewModel = viewModel,
        onValueChange = viewModel::updateUiState,
        onBack = onBack
    )
}

@Composable
private fun CollectionEntryScreen(
    uiState: CollectionEntryUiState,
    viewModel: CollectionEntryViewModel,
    onValueChange: (CollectionDetails) -> Unit,
    onBack: () -> Unit
) {
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    val onBackBehavior = { if (uiState.hasUnsavedChanges) showDiscardDialog = true else onBack() }
    val topBarTitle: String = if (!uiState.isNew) {
        stringResource(R.string.edit_item_title, uiState.collection?.name ?: "")
    } else {
        stringResource(R.string.create_collection)
    }
    BackHandler(enabled = true) {
        onBackBehavior()
    }

    Scaffold(
        topBar = { ItemEntryTopAppBar(title = topBarTitle, onBack = onBackBehavior) },
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
}

@Composable
private fun CollectionEntryScreenContent(
    uiState: CollectionEntryUiState,
    modifier: Modifier = Modifier,
    onValueChange: (CollectionDetails) -> Unit = {},
    onSave: () -> Unit
) {
    val (saveButtonIcon, saveButtonText) = when (uiState.isNew) {
        true -> painterResource(R.drawable.rounded_save) to
            stringResource(R.string.save_added_item, uiState.collection?.name ?: "")
        false -> painterResource(R.drawable.rounded_save_as) to
            stringResource(R.string.save_edited_item, uiState.collection?.name ?: "")
    }

    Column(
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.collectionDetails.emoji ?: "",
                    onValueChange = { onValueChange(uiState.collectionDetails.copy(emoji = it)) },
                    label = { Text(stringResource(R.string.emoji)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.weight(0.33f)
                )
                Spacer(
                    modifier = Modifier.width(8.dp)
                )
                OutlinedTextField(
                    value = uiState.collectionDetails.name ?: "",
                    onValueChange = { onValueChange(uiState.collectionDetails.copy(name = it)) },
                    label = { Text(stringResource(R.string.collection)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 4.dp)
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
