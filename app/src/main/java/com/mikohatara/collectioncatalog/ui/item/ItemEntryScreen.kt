package com.mikohatara.collectioncatalog.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.components.ItemEntryTopAppBar
import com.mikohatara.collectioncatalog.ui.components.pickItemImage
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme

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
    val topBarTitle: String = if (!uiState.isNew) { //.item?.uniqueDetails?.number.toString()
        stringResource(R.string.edit_item_title, uiState.itemDetails.number)
    } else {
        stringResource(R.string.add_item_title)
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
    val saveButtonText: String = if (!uiState.isNew) {
        stringResource(R.string.save_edited_item, uiState.itemDetails.number)
    } else {
        stringResource(R.string.save_added_item, uiState.itemDetails.number)
    }

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        val imagePath: String? = pickItemImage(uiState.item?.uniqueDetails?.imagePath)
        val updateUiState: (ItemDetails) -> Unit = {
            onValueChange(uiState.itemDetails.copy(imagePath = imagePath))
        }
        updateUiState.invoke(uiState.itemDetails)

        Card(
            modifier = EntryFormModifiers.card
        ) {
            Row(
                modifier = EntryFormModifiers.row
            ) {
                IconAbc123(modifier = EntryFormModifiers.icon)
                Column(
                    modifier = EntryFormModifiers.column
                ) {
                    Row {
                        OutlinedTextField(
                            value = uiState.itemDetails.number,
                            onValueChange = { onValueChange(uiState.itemDetails.copy(number = it)) },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Characters,
                                imeAction = ImeAction.Next
                            ),
                            label = { Text("Number") },
                            modifier = Modifier
                                .weight(1f),
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
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(0.7f),
                            enabled = uiState.isNew,
                            singleLine = true
                        )
                    }
                    OutlinedTextField(
                        value = uiState.itemDetails.imagePath.toString(),
                        onValueChange = {  },
                        label = { Text("imagePath debug") },
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(),
                        enabled = false,
                        singleLine = false
                    )
                }
            }
        }

        Card(
            modifier = EntryFormModifiers.card
        ) {
            Row(
                modifier = EntryFormModifiers.row
            ) {
                Icon(
                    painter = painterResource(R.drawable.rounded_globe),
                    contentDescription = null,
                    modifier = EntryFormModifiers.icon
                )
                Column(
                    modifier = EntryFormModifiers.column
                ) {
                    OutlinedTextField(
                        value = uiState.itemDetails.country,
                        onValueChange = { onValueChange(uiState.itemDetails.copy(country = it)) },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        label = { Text("Country") },
                        modifier = Modifier
                            .fillMaxWidth(),
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
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
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
                        modifier = Modifier
                            .fillMaxWidth(),
                        enabled = true,
                        singleLine = true
                    )
                }
            }
            Row(modifier = EntryFormModifiers.row) {
                Icon(
                    painter = painterResource(R.drawable.rounded_category),
                    contentDescription = null,
                    modifier = EntryFormModifiers.icon
                )
                Column(modifier = EntryFormModifiers.column) {
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
                    Row(modifier = Modifier.padding(top = 8.dp)) {
                        OutlinedTextField(
                            value = uiState.itemDetails.period ?: "",
                            onValueChange = { onValueChange(uiState.itemDetails.copy(period = it)) },
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            ),
                            label = { Text("Period") },
                            modifier = Modifier.weight(0.6f),
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
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(0.4f),
                            enabled = false, // TODO handle null values in Int/Double fields
                            singleLine = true
                        )
                    }
                }
            }
        }

        Card(modifier = EntryFormModifiers.card) {
            Row(modifier = EntryFormModifiers.row) {
                Icon(
                    painter = painterResource(R.drawable.rounded_ruler),
                    contentDescription = null,
                    modifier = EntryFormModifiers.icon
                )
                Column(modifier = EntryFormModifiers.column) {
                    Row {
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
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .weight(1f),
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
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .weight(1f),
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
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .weight(1f),
                            enabled = false, // TODO handle null values in Int/Double fields
                            singleLine = true
                        )
                    }
                }
            }
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
        Button(
            onClick = onSave,
            //enabled = uiState.hasValidEntry,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Icon(painter = if (!uiState.isNew) {
                painterResource(R.drawable.rounded_save_as)
            } else {
                painterResource(R.drawable.rounded_save)
            },
                contentDescription = null
            )
            Text(saveButtonText)
        }
    }
}

@Composable
private fun IconAbc123(modifier: Modifier) {
    Box(modifier = modifier) {
        Icon(
            painter = painterResource(R.drawable.rounded_abc),
            contentDescription = null,
            modifier = Modifier.absoluteOffset(y = (-5).dp)
        )
        Icon(
            painter = painterResource(R.drawable.rounded_123),
            contentDescription = null,
            modifier = Modifier.absoluteOffset(y = 6.dp)
        )
    }
}

private data object EntryFormModifiers {
    val card: Modifier = Modifier.padding(16.dp)
    val row: Modifier = Modifier.padding(vertical = 18.dp)
    val icon: Modifier = Modifier.padding(18.dp)
    val column: Modifier = Modifier.padding(end = 16.dp)
}

@Preview
@Composable
fun EntryFormPreview() {
    CollectionCatalogTheme {
        EntryForm(uiState = ItemEntryUiState(), modifier = Modifier.background(Color.LightGray)) {
            
        }
    }
}
