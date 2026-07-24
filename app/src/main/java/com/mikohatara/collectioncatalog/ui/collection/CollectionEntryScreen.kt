package com.mikohatara.collectioncatalog.ui.collection

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.CollectionColor
import com.mikohatara.collectioncatalog.ui.components.CollectionEntryTopAppBar
import com.mikohatara.collectioncatalog.ui.components.CollectionListTopAppBar
import com.mikohatara.collectioncatalog.ui.components.DeletionDialog
import com.mikohatara.collectioncatalog.ui.components.DiscardDialog
import com.mikohatara.collectioncatalog.ui.components.IconCollectionColor
import com.mikohatara.collectioncatalog.ui.components.SettingsBottomSheet
import com.mikohatara.collectioncatalog.util.getCollectionColor
import com.mikohatara.collectioncatalog.util.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CollectionEntryScreen(
    viewModel: CollectionEntryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    CollectionEntryScreen(
        coroutineScope = coroutineScope,
        context = context,
        collectionDetails = uiState.collectionDetails,
        collectionName = uiState.collection?.name ?: "",
        isNew = uiState.isNew,
        isValidEntry = uiState.isValidEntry,
        hasUnsavedChanges = uiState.hasUnsavedChanges,
        onValueChange = viewModel::updateUiState,
        onCollectionColorUpdate = viewModel::updateCollectionColor,
        onBack = onBack,
        onSave = viewModel::saveEntry,
        onDelete = viewModel::deleteCollection
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectionEntryScreen(
    coroutineScope: CoroutineScope,
    context: Context,
    collectionDetails: CollectionDetails,
    // collectionDetails.name is dynamic, hence the need for a separate static name
    collectionName: String,
    isNew: Boolean,
    isValidEntry: Boolean,
    hasUnsavedChanges: Boolean,
    onValueChange: (CollectionDetails) -> Unit,
    onCollectionColorUpdate: (String, Context) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onDelete: suspend () -> Unit,
) {
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    var showDeletionDialog by rememberSaveable { mutableStateOf(false) }
    var showColorDialog by rememberSaveable { mutableStateOf(false) }
    val onDismissDiscardDialog = { showDiscardDialog = false }
    val onDismissDeletionDialog = { showDeletionDialog = false }
    val onDismissColorDialog = { showColorDialog = false }
    val topAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = colorScheme.surfaceContainerHigh
    )
    val (topBarTitle, saveToast) = if (!isNew) {
        stringResource(R.string.edit_item_title, collectionName) to
        stringResource(R.string.saved_old_collection, collectionDetails.name ?: "")
    } else {
        stringResource(R.string.create_collection) to
        stringResource(R.string.saved_new_collection, collectionDetails.name ?: "")
    }
    val deletionToast = stringResource(
        R.string.deletion_message_plate, collectionName
    )

    val onBackBehavior = { if (hasUnsavedChanges) showDiscardDialog = true else onBack() }
    BackHandler { onBackBehavior() }

    Scaffold(
        topBar = {
            if (isNew) {
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
                collectionDetails = collectionDetails,
                isNew = isNew,
                isValidEntry = isValidEntry,
                onValueChange = onValueChange,
                onPickColor = { showColorDialog = true },
                onSave = {
                    onSave()
                    context.toast(text = saveToast)
                    onBack()
                },
                innerPadding = innerPadding
            )
        }
    )
    if (showColorDialog) {
        SettingsBottomSheet(
            label = stringResource(R.string.collection_color),
            context = context,
            options = CollectionColor.entries.map { getCollectionColor(it, context) },
            selectedOption = getCollectionColor(collectionDetails.color, context),
            onToggleSelection = { onCollectionColorUpdate(it, context) },
            onDismiss = onDismissColorDialog,
            skipPartiallyExpanded = true
        )
    }
    if (showDiscardDialog) {
        DiscardDialog(
            onConfirm = {
                onDismissDiscardDialog()
                onBack()
            },
            onCancel = onDismissDiscardDialog
        )
    }
    if (showDeletionDialog) {
        DeletionDialog(
            message = stringResource(R.string.deletion_dialog_collection),
            onConfirm = {
                onDismissDeletionDialog()
                coroutineScope.launch {
                    onDelete()
                    context.toast(text = deletionToast)
                    onBack()
                }
            },
            onCancel = onDismissDeletionDialog
        )
    }
}

@Composable
private fun CollectionEntryScreenContent(
    collectionDetails: CollectionDetails,
    isNew: Boolean,
    isValidEntry: Boolean,
    onValueChange: (CollectionDetails) -> Unit,
    onPickColor: () -> Unit,
    onSave: () -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val (saveButtonIcon, saveButtonText) = when (isNew) {
        true -> painterResource(R.drawable.rounded_save) to stringResource(
            R.string.save_new_collection, collectionDetails.name ?: ""
        )
        false -> painterResource(R.drawable.rounded_save_as) to stringResource(
            R.string.save_edited_item, collectionDetails.name ?: ""
        )
    }
    val tint = if (collectionDetails.color == CollectionColor.DEFAULT) {
        colorScheme.primary
    } else {
        collectionDetails.color.color
    }

    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        Card(
            colors = CardDefaults.cardColors(colorScheme.surfaceContainerHigh),
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 24.dp,
                bottomEnd = 24.dp
            )
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = collectionDetails.name ?: "",
                onValueChange = { onValueChange(collectionDetails.copy(name = it)) },
                label = {
                    Text(
                        stringResource(R.string.collection),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
            ) {
                OutlinedTextField(
                    value = collectionDetails.emoji ?: "",
                    onValueChange = {
                        onValueChange(collectionDetails.copy(emoji = it))
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
                FilledTonalButton(
                    onClick = onPickColor,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f)
                        .height(48.dp)
                ) {
                    IconCollectionColor(tint, Modifier.offset(x = (-8).dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.collection_color),
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onSave,
                enabled = isValidEntry,
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
        Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
    }
}
