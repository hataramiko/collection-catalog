package com.mikohatara.collectioncatalog.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mikohatara.collectioncatalog.ui.components.AddItemScreenTopAppBar
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme
import kotlinx.coroutines.launch

@Composable
fun AddItemScreen(
    viewModel: AddItemViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState = viewModel.uiState

    AddItemScreenContent(
        uiState,
        viewModel,
        onValueChange = viewModel::updateUiState,
        onBack
    )
}

@Composable
fun AddItemScreenContent(
    uiState: AddItemUiState,
    viewModel: AddItemViewModel,
    onValueChange: (NewItemDetails) -> Unit,
    onBack: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { AddItemScreenTopAppBar(onBack) },
        content = { innerPadding ->
            InputForm(
                uiState,
                //newItemDetails = uiState.newItemDetails,
                modifier = Modifier.padding(innerPadding),
                onValueChange,
                onAdd = {
                    coroutineScope.launch {
                        viewModel.addItem()
                        onBack()
                    }
                }
            )
        }
    )
}

@Composable
private fun InputForm(
    uiState: AddItemUiState,
    //newItemDetails: NewItemDetails,
    modifier: Modifier = Modifier,
    onValueChange: (NewItemDetails) -> Unit = {},
    onAdd: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = uiState.newItemDetails.country,
            onValueChange = { onValueChange(uiState.newItemDetails.copy(country = it)) },
            //keyboardActions = ,
            label = { Text("Country") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.newItemDetails.type,
            onValueChange = { onValueChange(uiState.newItemDetails.copy(type = it)) },
            //keyboardActions = ,
            label = { Text("Type") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.newItemDetails.number,
            onValueChange = { onValueChange(uiState.newItemDetails.copy(number = it)) },
            //keyboardActions = ,
            label = { Text("Number") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.newItemDetails.variant,
            onValueChange = { onValueChange(uiState.newItemDetails.copy(variant = it)) },
            //keyboardActions = ,
            label = { Text("Variant") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        Button(
            onClick = onAdd,
            //enabled = uiState.hasValidEntry,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Add ")
            Text(text = "ABC-012")
        }
    }
}

/*
@Composable
private fun TextField(
    value: String,
    onValueChange: (NewItemDetails) -> Unit = {},
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(uiState.newItemDetails.copy()) },
        //keyboardActions = ,
        label = label,
        modifier = Modifier.fillMaxWidth(),
        enabled = true,
        singleLine = true
    )
}*/

@Preview
@Composable
fun Preview() {
    CollectionCatalogTheme {
        //InputForm()
    }
}