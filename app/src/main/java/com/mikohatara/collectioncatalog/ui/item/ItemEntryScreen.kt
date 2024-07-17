package com.mikohatara.collectioncatalog.ui.item

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.mikohatara.collectioncatalog.ui.components.DiscardDialog
import com.mikohatara.collectioncatalog.ui.components.IconAbc123
import com.mikohatara.collectioncatalog.ui.components.IconBlank
import com.mikohatara.collectioncatalog.ui.components.ItemEntryTopAppBar
import com.mikohatara.collectioncatalog.ui.components.ItemEntryVerticalSpacer
import com.mikohatara.collectioncatalog.ui.components.ItemScreenModifiers
import com.mikohatara.collectioncatalog.ui.components.pickItemImage
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme
import com.mikohatara.collectioncatalog.util.isBlankOrZero
import com.mikohatara.collectioncatalog.util.isValidYear

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
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    val onBackBehavior = { if (uiState.hasUnsavedChanges) showDiscardDialog = true else onBack() }
    val topBarTitle: String = if (!uiState.isNew) {
        stringResource(R.string.edit_item_title, uiState.itemDetails.number)
    } else {
        stringResource(R.string.add_item_title)
    }
    BackHandler(enabled = true) {
        onBackBehavior()
    }

    Scaffold(
        topBar = { ItemEntryTopAppBar(title = topBarTitle, onBack = onBackBehavior) },
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
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        /*  TODO improve image logic
        *
        *   Using LazyColumn instead of Column, and the ensuing recompositions of
        *   EntryFormImage cause the selected image/filePath to reset mid-edit.
        *
        * */
        EntryFormImage(uiState, onValueChange)

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
                    onValueChange = { newValue ->
                        onValueChange(
                            uiState.itemDetails.copy(
                                year = if (newValue.isValidYear()) newValue.toInt() else null)
                        )
                    },
                    modifier = Modifier.weight(0.5f),
                    keyboardType = KeyboardType.Number
                )
            }
        }
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
                    onValueChange = { onValueChange( // TODO improve input logic
                        uiState.itemDetails.copy(cost = it.toDoubleOrNull() ?: 0.0))
                    },
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Number,
                )
                EntryFormField(
                    icon = null,
                    label = stringResource(R.string.value),
                    value = uiState.itemDetails.value?.toString() ?: "",
                    onValueChange = { newValue ->
                        onValueChange(uiState.itemDetails.copy(
                            value = if (newValue.isBlankOrZero()) null
                            else newValue.toDoubleOrNull())
                        )
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
                    onValueChange = { newValue ->
                        onValueChange(uiState.itemDetails.copy(
                            width = if (newValue.isBlankOrZero()) null
                            else newValue.toDoubleOrNull())
                        )
                    },
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Number
                )
                EntryFormField(
                    icon = null,
                    label = "Height",
                    value = uiState.itemDetails.height?.toString() ?: "",
                    onValueChange = { newValue ->
                        onValueChange(uiState.itemDetails.copy(
                            height = if (newValue.isBlankOrZero()) null
                            else newValue.toDoubleOrNull())
                        )
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
                onValueChange = { newValue ->
                    onValueChange(uiState.itemDetails.copy(
                        weight = if (newValue.isBlankOrZero()) null
                        else newValue.toDoubleOrNull())
                    )
                },
                keyboardType = KeyboardType.Number
            )
        }
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
        Column(modifier = Modifier.padding(top = 4.dp, bottom = 12.dp, end = 16.dp)) {
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
    val currentValue = remember(value) { value }

    Row(modifier = modifier.padding(vertical = 4.dp)) {
        if (icon != null) icon() else Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = currentValue,
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
