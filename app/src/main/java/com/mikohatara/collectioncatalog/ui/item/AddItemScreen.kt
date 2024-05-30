package com.mikohatara.collectioncatalog.ui.item

import android.util.Log
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mikohatara.collectioncatalog.ui.components.AddItemScreenTopAppBar
import com.mikohatara.collectioncatalog.ui.components.pickItemImage
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
    modifier: Modifier = Modifier,
    onValueChange: (NewItemDetails) -> Unit = {},
    onAdd: () -> Unit
) {
    Column(
        modifier = modifier
            //.padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val imagePath: String? = pickItemImage()
        //Log.d("imagePath in AddItemScreen", imagePath.toString())

        val updateUiState: (NewItemDetails) -> Unit = {
            onValueChange(uiState.newItemDetails.copy(imagePath = imagePath))
        }

        updateUiState.invoke(uiState.newItemDetails)

        Log.d("imagePath in uiState", uiState.newItemDetails.imagePath.toString())
        
        OutlinedTextField(
            value = uiState.newItemDetails.country,
            onValueChange = { onValueChange(uiState.newItemDetails.copy(country = it)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            label = { Text("Country") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.newItemDetails.region ?: "",
            onValueChange = { onValueChange(uiState.newItemDetails.copy(region = it)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            label = { Text("Region") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.newItemDetails.area ?: "",
            onValueChange = { onValueChange(uiState.newItemDetails.copy(area = it)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            label = { Text("Area") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.newItemDetails.type,
            onValueChange = { onValueChange(uiState.newItemDetails.copy(type = it)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            label = { Text("Type") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.newItemDetails.number,
            onValueChange = { onValueChange(uiState.newItemDetails.copy(number = it)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.Next
            ),
            label = { Text("Number") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.newItemDetails.variant,
            onValueChange = { onValueChange(uiState.newItemDetails.copy(variant = it)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            label = { Text("Variant") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.newItemDetails.imagePath.toString(),
            onValueChange = { onValueChange(uiState.newItemDetails.copy(imagePath = it)) },
            //keyboardOptions = ,
            label = { Text("imagePath debug") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.newItemDetails.width.toString(),
            // The solution below essentially negates nullability for the width.
            // TODO come up with a better solution
            // Have to reassess whether to use Double or Int anyway...
            onValueChange = {
                onValueChange(uiState.newItemDetails.copy(width = it.toDoubleOrNull() ?: 0.0))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            label = { Text("Width") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        Button(
            onClick = onAdd,
            //enabled = uiState.hasValidEntry,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Add ${uiState.newItemDetails.number}")
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