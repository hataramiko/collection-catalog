package com.mikohatara.collectioncatalog.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mikohatara.collectioncatalog.ui.components.ItemEntryTopAppBar

@Composable
fun ItemEntryScreen(
    viewModel: ItemEntryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState = viewModel.uiState

    ItemEntryScreenContent(
        uiState,
        viewModel,
        onValueChange = viewModel::updateUiState,
        onBack
    )
}

@Composable
fun ItemEntryScreenContent(
    uiState: ItemEntryUiState,
    viewModel: ItemEntryViewModel,
    onValueChange: (ItemDetails) -> Unit,
    onBack: () -> Unit
) {
    val topBarTitle: String = if (!uiState.isNew) {
        "Edit " + uiState.item?.uniqueDetails?.number
    } else {
        "Add new"
    }

    Scaffold(
        topBar = { ItemEntryTopAppBar(topBarTitle, onBack) },
        content = { innerPadding ->
            EntryForm(
                uiState,
                modifier = Modifier.padding(innerPadding),
                onValueChange,
                onSave = {
                    viewModel.saveEntry()
                    onBack()
                }
            )
        }
    )
}

@Composable
private fun EntryForm(
    uiState: ItemEntryUiState,
    modifier: Modifier,
    onValueChange: (ItemDetails) -> Unit = {},
    onSave: () -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = uiState.itemDetails.number,
            onValueChange = { onValueChange(uiState.itemDetails.copy(number = it)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            label = { Text("Number") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.itemDetails.region ?: "",
            onValueChange = { onValueChange(uiState.itemDetails.copy(region = it)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            label = { Text("Region") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        Button(
            onClick = onSave,
            //enabled = uiState.hasValidEntry,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Save")
        }
    }
}