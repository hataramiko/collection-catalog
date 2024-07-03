package com.mikohatara.collectioncatalog.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import com.mikohatara.collectioncatalog.ui.components.IconAbc123
import com.mikohatara.collectioncatalog.ui.components.ItemEntryHorizontalSpacer
import com.mikohatara.collectioncatalog.ui.components.ItemEntryTopAppBar
import com.mikohatara.collectioncatalog.ui.components.ItemEntryVerticalSpacer
import com.mikohatara.collectioncatalog.ui.components.ItemScreenModifiers
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
    val topBarTitle: String = if (!uiState.isNew) {
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

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        val imagePath: String? = pickItemImage(uiState.item?.uniqueDetails?.imagePath)
        val updateUiState: (ItemDetails) -> Unit = {
            onValueChange(uiState.itemDetails.copy(imagePath = imagePath))
        }
        updateUiState.invoke(uiState.itemDetails)

        /*
        EntryFormCard/*(uiState.itemDetails)*/ { //itemDetails ->
            EntryFormField(
                icon = { IconAbc123() },
                label = stringResource(R.string.reg_no),
                value = uiState.itemDetails.number,
                onValueChange = { onValueChange(uiState.itemDetails.copy(number = it)) }
            )
            EntryFormField(
                icon = { /*NULL*/ },
                label = "Variant",
                value = uiState.itemDetails.variant,
                onValueChange = { onValueChange(uiState.itemDetails.copy(variant = it)) }
            )
            EntryFormField(
                icon = { /*NULL*/ },
                label = "imagePath debug",
                value = uiState.itemDetails.imagePath.toString(),
                onValueChange = { /*NULL*/ },
                enabled = false,
                singleLine = false
            )
        }

        EntryFormCard {
            EntryFormField(
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.rounded_globe),
                        contentDescription = null,
                        modifier = ItemScreenModifiers.icon
                    )
                },
                label = stringResource(R.string.country),
                value = uiState.itemDetails.country,
                onValueChange = { onValueChange(uiState.itemDetails.copy(country = it)) }
            )
            EntryFormField(
                icon = { /*NULL*/ },
                label = stringResource(R.string.region),
                value = uiState.itemDetails.region ?: "",
                onValueChange = { onValueChange(uiState.itemDetails.copy(region = it)) }
            )
            EntryFormField(
                icon = { /*NULL*/ },
                label = stringResource(R.string.area),
                value = uiState.itemDetails.area ?: "",
                onValueChange = { onValueChange(uiState.itemDetails.copy(area = it)) }
            )
            EntryFormField(
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.rounded_category),
                        contentDescription = null,
                        modifier = ItemScreenModifiers.icon
                    )
                },
                label = stringResource(R.string.type),
                value = uiState.itemDetails.type,
                onValueChange = { onValueChange(uiState.itemDetails.copy(type = it)) }
            )
            EntryFormField(
                icon = { /*NULL*/ },
                label = "",
                value = "" ?: "",
                onValueChange = {  }
            )
            EntryFormField(
                icon = { /*NULL*/ },
                label = "",
                value = "" ?: "",
                onValueChange = {  }
            )
        }

        EntryFormCard {
            EntryFormField(
                icon = { /*NULL*/ },
                label = "",
                value = "" ?: "",
                onValueChange = {  }
            )
        }*/

        Card(ItemScreenModifiers.card) {
            Column(ItemScreenModifiers.column) {
                Row(ItemScreenModifiers.rowWithIcon) {
                    IconAbc123()
                    OutlinedTextField(
                        value = uiState.itemDetails.number,
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(number = it))
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Next
                        ),
                        label = { Text(stringResource(R.string.reg_no)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = uiState.isNew,
                        singleLine = true
                    )
                    ItemEntryHorizontalSpacer()
                    OutlinedTextField(
                        value = uiState.itemDetails.variant,
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(variant = it))
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        label = { Text("Variant") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.66f),
                        enabled = uiState.isNew,
                        singleLine = true
                    )
                }
                Row(ItemScreenModifiers.rowNoIcon) {
                    OutlinedTextField(
                        value = uiState.itemDetails.imagePath.toString(),
                        onValueChange = {  },
                        label = { Text("imagePath debug") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = false,
                        singleLine = false
                    )
                }
            }
        }

        Card(ItemScreenModifiers.card) {
            Column(ItemScreenModifiers.column) {
                Row(ItemScreenModifiers.rowWithIcon) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_globe),
                        contentDescription = null,
                        modifier = ItemScreenModifiers.icon
                    )
                    OutlinedTextField(
                        value = uiState.itemDetails.country,
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(country = it))
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        label = { Text(stringResource(R.string.country)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = true,
                        singleLine = true
                    )
                }
                Row(ItemScreenModifiers.rowNoIcon) {
                    OutlinedTextField(
                        value = uiState.itemDetails.region ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(region = it))
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        label = { Text(stringResource(R.string.region)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = true,
                        singleLine = true
                    )
                }
                Row(ItemScreenModifiers.rowNoIcon) {
                    OutlinedTextField(
                        value = uiState.itemDetails.area ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(area = it))
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        label = { Text(stringResource(R.string.area)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = true,
                        singleLine = true
                    )
                }
                ItemEntryVerticalSpacer()
                Row(ItemScreenModifiers.rowWithIcon) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_category),
                        contentDescription = null,
                        modifier = ItemScreenModifiers.icon
                    )
                    OutlinedTextField(
                        value = uiState.itemDetails.type,
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(type = it))
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        label = { Text(stringResource(R.string.type)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = true,
                        singleLine = true
                    )
                }
                Row(ItemScreenModifiers.rowWithIcon) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_date_range),
                        contentDescription = null,
                        modifier = ItemScreenModifiers.icon
                    )
                    OutlinedTextField(
                        value = uiState.itemDetails.period ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(period = it))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        label = { Text(stringResource(R.string.period)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = true,
                        singleLine = true
                    )
                    ItemEntryHorizontalSpacer()
                    OutlinedTextField(
                        value = uiState.itemDetails.year?.toString() ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(year = it.toIntOrNull() ?: 0))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        label = { Text(stringResource(R.string.year)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f),
                        enabled = true,
                        singleLine = true
                    )
                }
            }
        }

        Card(ItemScreenModifiers.card) {
            Column(ItemScreenModifiers.column) {
                Row(ItemScreenModifiers.rowWithIcon) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_note_stack),
                        contentDescription = null,
                        modifier = ItemScreenModifiers.icon
                    )
                    OutlinedTextField(
                        value = uiState.itemDetails.notes ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(notes = it))
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        label = { Text("Notes") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = true,
                        singleLine = false
                    )
                }
                Row(ItemScreenModifiers.rowNoIcon) {
                    OutlinedTextField(
                        value = uiState.itemDetails.vehicle ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(vehicle = it))
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        label = { Text("Vehicle") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = true,
                        singleLine = true
                    )
                }
                ItemEntryVerticalSpacer()
                Row(ItemScreenModifiers.rowWithIcon) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_event),
                        contentDescription = null,
                        modifier = ItemScreenModifiers.icon
                    )
                    OutlinedTextField(
                        value = uiState.itemDetails.date ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(date = it))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        label = { Text(stringResource(R.string.date)) },
                        placeholder = { Text("1922-05-18") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = true,
                        singleLine = true
                    )
                }
                Row(ItemScreenModifiers.rowWithIcon) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_payments),
                        contentDescription = null,
                        modifier = ItemScreenModifiers.icon
                    )
                    OutlinedTextField(
                        value = uiState.itemDetails.cost?.toString() ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(
                                cost = it.toDoubleOrNull() ?: 0.0))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        label = { Text(stringResource(R.string.cost)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = true,
                        singleLine = true
                    )
                    ItemEntryHorizontalSpacer()
                    OutlinedTextField(
                        value = uiState.itemDetails.value?.toString() ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(
                                value = it.toDoubleOrNull() ?: 0.0))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        label = { Text(stringResource(R.string.value)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = true,
                        singleLine = true
                    )
                }
                ItemEntryVerticalSpacer()
                Row(ItemScreenModifiers.rowWithIcon) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_info),
                        contentDescription = null,
                        modifier = ItemScreenModifiers.icon
                    )
                    OutlinedTextField(
                        value = uiState.itemDetails.status ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(status = it))
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        label = { Text("Status") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = true,
                        singleLine = true
                    )
                }
            }
        }

        Card(ItemScreenModifiers.card) {
            Column(ItemScreenModifiers.column) {
                Row(ItemScreenModifiers.rowWithIcon) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_stars_layered),
                        contentDescription = null,
                        modifier = ItemScreenModifiers.icon
                    )
                    OutlinedTextField(
                        value = uiState.itemDetails.condition ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(condition = it))
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        label = { Text("Condition") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = true,
                        singleLine = true
                    )
                }
                Row(ItemScreenModifiers.rowNoIcon) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    ) {
                        Text("Keeper")
                        Switch(
                            checked = uiState.itemDetails.isKeeper,
                            onCheckedChange = {
                                onValueChange(uiState.itemDetails.copy(isKeeper = it))
                            }
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    ) {
                        Text("For Trade")
                        Switch(
                            checked = uiState.itemDetails.isForTrade,
                            onCheckedChange = {
                                onValueChange(uiState.itemDetails.copy(isForTrade = it))
                            }
                        )
                    }
                }
            }
        }

        Card(ItemScreenModifiers.card) {
            Column(ItemScreenModifiers.column) {
                Row(ItemScreenModifiers.rowWithIcon) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_ruler),
                        contentDescription = null,
                        modifier = ItemScreenModifiers.icon
                    )
                    OutlinedTextField(
                        value = uiState.itemDetails.width?.toString() ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(
                                width = it.toDoubleOrNull() ?: 0.0))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        label = { Text("Width") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = true,
                        singleLine = true
                    )
                    ItemEntryHorizontalSpacer()
                    OutlinedTextField(
                        value = uiState.itemDetails.height?.toString() ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(
                                height = it.toDoubleOrNull() ?: 0.0))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        label = { Text("Height") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = true,
                        singleLine = true
                    )
                    ItemEntryHorizontalSpacer()
                    OutlinedTextField(
                        value = uiState.itemDetails.weight?.toString() ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(
                                weight = it.toDoubleOrNull() ?: 0.0))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        label = { Text("Weight") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = true,
                        singleLine = true
                    )
                }
            }
        }
        
        Card(ItemScreenModifiers.card) {
            Column(ItemScreenModifiers.column) {
                Row(ItemScreenModifiers.rowWithIcon) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_person_pin_circle),
                        contentDescription = null,
                        modifier = ItemScreenModifiers.icon
                    )
                    OutlinedTextField(
                        value = uiState.itemDetails.sourceName ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(sourceName = it))
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        label = { Text("Source Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = true,
                        singleLine = true
                    )
                }
                Row(ItemScreenModifiers.rowNoIcon) {
                    OutlinedTextField(
                        value = uiState.itemDetails.sourceAlias ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(sourceAlias = it))
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        label = { Text("Source Alias") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = true,
                        singleLine = true
                    )
                }
                Row(ItemScreenModifiers.rowNoIcon) {
                    OutlinedTextField(
                        value = uiState.itemDetails.sourceType ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(sourceType = it))
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        label = { Text("Source Type") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = true,
                        singleLine = true
                    )
                }
                Row(ItemScreenModifiers.rowNoIcon) {
                    OutlinedTextField(
                        value = uiState.itemDetails.sourceDetails ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(sourceDetails = it))
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        label = { Text("Source Details") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = true,
                        singleLine = true
                    )
                }
                Row(ItemScreenModifiers.rowNoIcon) {
                    OutlinedTextField(
                        value = uiState.itemDetails.sourceCountry ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(sourceCountry = it))
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        label = { Text("Source Country") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = true,
                        singleLine = true
                    )
                }
            }
        }

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
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(saveButtonText)
        }
    }
}

@Composable
private fun EntryFormCard(
    //itemDetails: ItemDetails,
    content: @Composable (/*ItemDetails*/) -> Unit
) {
    Card(ItemScreenModifiers.card) {
        Column(ItemScreenModifiers.column) {
            content(/*itemDetails*/)
        }
    }
}

@Composable
private fun EntryFormField(
    icon: @Composable /*BoxScope.*/() -> Unit,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    singleLine: Boolean = true
) {
    Row(

    ) {
        icon()
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            enabled = enabled,
            singleLine = singleLine
        )
    }
}

@Preview
@Composable
fun EntryFormPreview() {
    CollectionCatalogTheme {
        EntryForm(uiState = ItemEntryUiState(), modifier = Modifier.background(Color.LightGray)) {
            
        }
    }
}
