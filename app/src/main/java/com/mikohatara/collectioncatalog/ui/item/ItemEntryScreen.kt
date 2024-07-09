package com.mikohatara.collectioncatalog.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
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
import com.mikohatara.collectioncatalog.ui.components.IconBlank
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

    LazyColumn(
        modifier = modifier
    ) {
        item { EntryFormImage(uiState, onValueChange) }

        item {
            EntryFormCard {
                Row {
                    EntryFormField(
                        icon = { IconAbc123() },
                        label = stringResource(R.string.reg_no),
                        value = uiState.itemDetails.number,
                        onValueChange = { onValueChange(uiState.itemDetails.copy(number = it)) },
                        modifier = Modifier.weight(1f),
                        enabled = uiState.isNew,
                        capitalization = KeyboardCapitalization.Characters
                    )
                    EntryFormField(
                        icon = null,
                        label = "Variant",
                        value = uiState.itemDetails.variant,
                        onValueChange = { onValueChange(uiState.itemDetails.copy(variant = it)) },
                        modifier = Modifier.weight(0.5f),
                        enabled = uiState.isNew
                    )
                }
                EntryFormField(
                    icon = { IconBlank() },
                    label = "imagePath debug",
                    value = uiState.itemDetails.imagePath.toString(),
                    onValueChange = { /*NULL*/ },
                    enabled = false,
                    singleLine = false
                )
            }
        }

        item {
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
                    icon = { IconBlank() },
                    label = stringResource(R.string.region),
                    value = uiState.itemDetails.region ?: "",
                    onValueChange = { onValueChange(uiState.itemDetails.copy(region = it)) }
                )
                EntryFormField(
                    icon = { IconBlank() },
                    label = stringResource(R.string.area),
                    value = uiState.itemDetails.area ?: "",
                    onValueChange = { onValueChange(uiState.itemDetails.copy(area = it)) }
                )
                ItemEntryVerticalSpacer()

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
                Row {
                    EntryFormField(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.rounded_date_range),
                                contentDescription = null,
                                modifier = ItemScreenModifiers.icon
                            )
                        },
                        label = stringResource(R.string.period),
                        value = uiState.itemDetails.period ?: "",
                        onValueChange = { onValueChange(uiState.itemDetails.copy(period = it)) },
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    )
                    EntryFormField(
                        icon = null,
                        label = stringResource(R.string.year),
                        value = uiState.itemDetails.year?.toString() ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(year = it.toIntOrNull() ?: 0))
                        },
                        modifier = Modifier.weight(0.5f),
                        keyboardType = KeyboardType.Number
                    )
                }
            }
        }

        item {
            EntryFormCard {
                EntryFormField(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.rounded_note_stack),
                            contentDescription = null,
                            modifier = ItemScreenModifiers.icon
                        )
                    },
                    label = "Notes",
                    value = uiState.itemDetails.notes ?: "",
                    onValueChange = { onValueChange(uiState.itemDetails.copy(notes = it)) },
                    singleLine = false
                )
                EntryFormField(
                    icon = { IconBlank() },
                    label = "Vehicle",
                    value = uiState.itemDetails.vehicle ?: "",
                    onValueChange = { onValueChange(uiState.itemDetails.copy(vehicle = it)) },
                    singleLine = false
                )
                ItemEntryVerticalSpacer()

                EntryFormField(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.rounded_event),
                            contentDescription = null,
                            modifier = ItemScreenModifiers.icon
                        )
                    },
                    label = stringResource(R.string.date),
                    //placeholder = "1922-05-18",
                    value = uiState.itemDetails.date ?: "",
                    onValueChange = { onValueChange(uiState.itemDetails.copy(date = it)) },
                    keyboardType = KeyboardType.Number,
                )
                Row {
                    EntryFormField(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.rounded_payments),
                                contentDescription = null,
                                modifier = ItemScreenModifiers.icon
                            )
                        },
                        label = stringResource(R.string.cost),
                        value = uiState.itemDetails.cost?.toString() ?: "",
                        onValueChange = { onValueChange(
                            uiState.itemDetails.copy(cost = it.toDoubleOrNull() ?: 0.0))
                        },
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number,
                    )
                    EntryFormField(
                        icon = null,
                        label = stringResource(R.string.value),
                        value = uiState.itemDetails.value?.toString() ?: "",
                        onValueChange = { onValueChange(
                            uiState.itemDetails.copy(value = it.toDoubleOrNull() ?: 0.0))
                        },
                        modifier = Modifier.weight(0.725f),
                        keyboardType = KeyboardType.Number,
                    )
                }
                ItemEntryVerticalSpacer()

                EntryFormField(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.rounded_pin_dist),
                            contentDescription = null,
                            modifier = ItemScreenModifiers.icon
                        )
                    },
                    label = "Status",
                    value = uiState.itemDetails.status ?: "",
                    onValueChange = { onValueChange(uiState.itemDetails.copy(status = it)) }
                )
            }
        }

        item {
            EntryFormCard {
                EntryFormField(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.rounded_stars_layered),
                            contentDescription = null,
                            modifier = ItemScreenModifiers.icon
                        )
                    },
                    label = "Condition",
                    value = uiState.itemDetails.condition ?: "",
                    onValueChange = { onValueChange(uiState.itemDetails.copy(condition = it)) }
                )
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

        item {
            EntryFormCard {
                Row {
                    EntryFormField(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.rounded_ruler),
                                contentDescription = null,
                                modifier = ItemScreenModifiers.icon
                            )
                        },
                        label = "Width",
                        value = uiState.itemDetails.width?.toString() ?: "",
                        onValueChange = { onValueChange(
                            uiState.itemDetails.copy(width = it.toDoubleOrNull() ?: 0.0))
                        },
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    )
                    EntryFormField(
                        icon = null,
                        label = "Height",
                        value = uiState.itemDetails.height?.toString() ?: "",
                        onValueChange = { onValueChange(
                            uiState.itemDetails.copy(height = it.toDoubleOrNull() ?: 0.0))
                        },
                        modifier = Modifier.weight(0.725f),
                        keyboardType = KeyboardType.Number
                    )
                }
                EntryFormField(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.rounded_weight),
                            contentDescription = null,
                            modifier = ItemScreenModifiers.icon
                        )
                    },
                    label = "Weight",
                    value = uiState.itemDetails.weight?.toString() ?: "",
                    onValueChange = { onValueChange(
                        uiState.itemDetails.copy(weight = it.toDoubleOrNull() ?: 0.0))
                    },
                    keyboardType = KeyboardType.Number
                )
            }
        }
        
        item {
            EntryFormCard {
                EntryFormField(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.rounded_person_pin_circle),
                            contentDescription = null,
                            modifier = ItemScreenModifiers.icon
                        )
                    },
                    label = "Source Name",
                    value = uiState.itemDetails.sourceName ?: "",
                    onValueChange = { onValueChange(uiState.itemDetails.copy(sourceName = it)) }
                )
                EntryFormField(
                    icon = { IconBlank() },
                    label = "Source Alias",
                    value = uiState.itemDetails.sourceAlias ?: "",
                    onValueChange = { onValueChange(uiState.itemDetails.copy(sourceAlias = it)) }
                )
                EntryFormField(
                    icon = { IconBlank() },
                    label = "Source Type",
                    value = uiState.itemDetails.sourceType ?: "",
                    onValueChange = { onValueChange(uiState.itemDetails.copy(sourceType = it)) }
                )
                EntryFormField(
                    icon = { IconBlank() },
                    label = "Source Details",
                    value = uiState.itemDetails.sourceDetails ?: "",
                    onValueChange = { onValueChange(uiState.itemDetails.copy(sourceDetails = it)) }
                )
                EntryFormField(
                    icon = { IconBlank() },
                    label = "Source Country",
                    value = uiState.itemDetails.sourceCountry ?: "",
                    onValueChange = { onValueChange(uiState.itemDetails.copy(sourceCountry = it)) },
                    imeAction = ImeAction.Done
                )
            }
        }

        item {
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
}

@Composable
private fun EntryFormImage(
    uiState: ItemEntryUiState,
    onValueChange: (ItemDetails) -> Unit
) {
    val imagePath: String? = pickItemImage(uiState.item?.uniqueDetails?.imagePath)
    val updateUiState: (ItemDetails) -> Unit = {
        onValueChange(uiState.itemDetails.copy(imagePath = imagePath))
    }
    updateUiState.invoke(uiState.itemDetails)
}

@Composable
private fun EntryFormCard(
    content: @Composable () -> Unit
) {
    Card(modifier = Modifier.padding(16.dp)) {
        Column(modifier = Modifier.padding(top = 10.dp, bottom = 18.dp, end = 16.dp)) {
            content()
        }
    }
}

@Composable
private fun EntryFormField(
    icon: @Composable (() -> Unit)?,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next
) {
    Row(modifier = modifier.padding(vertical = 4.dp)) {
        if (icon != null) {
            icon()
        } else {
            Spacer(modifier = Modifier.width(8.dp))
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(
                capitalization = capitalization,
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
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
