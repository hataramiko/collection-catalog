package com.mikohatara.collectioncatalog.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mikohatara.collectioncatalog.ui.components.ItemEntryTopAppBar
import com.mikohatara.collectioncatalog.ui.components.pickItemImage

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
        "Edit ${uiState.item?.uniqueDetails?.number}"
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
        val imagePath: String? = pickItemImage(uiState.item?.uniqueDetails?.imagePath)
        val updateUiState: (ItemDetails) -> Unit = {
            onValueChange(uiState.itemDetails.copy(imagePath = imagePath))
        }
        updateUiState.invoke(uiState.itemDetails)

        Row {
            OutlinedTextField(
                value = uiState.itemDetails.number,
                onValueChange = { onValueChange(uiState.itemDetails.copy(number = it)) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Next
                ),
                label = { Text("Number") },
                modifier = Modifier.weight(1f),
                enabled = uiState.isNew,
                singleLine = true
            )
            OutlinedTextField(
                value = uiState.itemDetails.variant,
                onValueChange = { onValueChange(uiState.itemDetails.copy(variant = it)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                label = { Text("Variant") },
                modifier = Modifier.weight(0.5f),
                enabled = uiState.isNew,
                singleLine = true
            )
        }
        OutlinedTextField(
            value = uiState.itemDetails.imagePath.toString(),
            onValueChange = {  },
            label = { Text("imagePath debug") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            singleLine = false
        )
        OutlinedTextField(
            value = uiState.itemDetails.country,
            onValueChange = { onValueChange(uiState.itemDetails.copy(country = it)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            label = { Text("Country") },
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
        OutlinedTextField(
            value = uiState.itemDetails.area ?: "",
            onValueChange = { onValueChange(uiState.itemDetails.copy(area = it)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            label = { Text("Area") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.itemDetails.type,
            onValueChange = { onValueChange(uiState.itemDetails.copy(type = it)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            label = { Text("Type") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        Row {
            OutlinedTextField(
                value = uiState.itemDetails.period ?: "",
                onValueChange = { onValueChange(uiState.itemDetails.copy(period = it)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                label = { Text("Period") },
                modifier = Modifier.weight(1f),
                enabled = true,
                singleLine = true
            )
            OutlinedTextField(
                value = uiState.itemDetails.year.toString(),
                onValueChange = { onValueChange(uiState.itemDetails.copy(year = it.toIntOrNull())) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                label = { Text("Year") },
                modifier = Modifier.weight(1f),
                enabled = false, // TODO handle null values in Int/Double fields
                singleLine = true
            )
        }
        /*
        vehicle
        notes
        date
        cost
        value
        status
        */
        /*
        isKeeper
        isForTrade
        condition
        */
        /*
        sourceName
        sourceAlias
        sourceDetails
        sourceType
        sourceCountry
        */
        Row(modifier = Modifier) {
            OutlinedTextField(
                value = uiState.itemDetails.width.toString(),
                onValueChange = {
                    onValueChange(uiState.itemDetails.copy(width = it.toDoubleOrNull() ?: 0.0))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                label = { Text("Width") },
                modifier = Modifier.weight(1f),
                enabled = true,
                singleLine = true
            )
            OutlinedTextField(
                value = uiState.itemDetails.height.toString(),
                onValueChange = {
                    onValueChange(uiState.itemDetails.copy(height = it.toDoubleOrNull() ?: 0.0))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                label = { Text("Height") },
                modifier = Modifier.weight(1f),
                enabled = false, // TODO handle null values in Int/Double fields
                singleLine = true
            )
            OutlinedTextField(
                value = uiState.itemDetails.weight.toString(),
                onValueChange = {
                    onValueChange(uiState.itemDetails.copy(weight = it.toDoubleOrNull() ?: 0.0))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                label = { Text("Weight") },
                modifier = Modifier.weight(1f),
                enabled = false, // TODO handle null values in Int/Double fields
                singleLine = true
            )
        }
        Button(
            onClick = onSave,
            //enabled = uiState.hasValidEntry,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Save ${uiState.itemDetails.number}")
        }
    }
}