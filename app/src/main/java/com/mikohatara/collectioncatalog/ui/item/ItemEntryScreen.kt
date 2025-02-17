package com.mikohatara.collectioncatalog.ui.item

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.ItemDetails
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.data.UserPreferences
import com.mikohatara.collectioncatalog.ui.components.DiscardDialog
import com.mikohatara.collectioncatalog.ui.components.IconQuotationMark
import com.mikohatara.collectioncatalog.ui.components.ItemEntryTopAppBar
import com.mikohatara.collectioncatalog.ui.components.ItemEntryVerticalSpacer
import com.mikohatara.collectioncatalog.ui.components.pickItemImage
import com.mikohatara.collectioncatalog.util.isBlankOrZero
import com.mikohatara.collectioncatalog.util.isValidYear

@Composable
fun ItemEntryScreen(
    viewModel: ItemEntryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ItemEntryScreen(
        viewModel,
        userPreferences,
        uiState,
        onValueChange = viewModel::updateUiState,
        onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemEntryScreen(
    viewModel: ItemEntryViewModel,
    userPreferences: UserPreferences,
    uiState: ItemEntryUiState,
    onValueChange: (ItemDetails) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    val onBackBehavior = { if (uiState.hasUnsavedChanges) showDiscardDialog = true else onBack() }
    val onSaveBehavior = { viewModel.saveEntry(); onBack() }
    val (saveButtonText, saveButtonIcon) = when (uiState.isNew) {
        true -> stringResource(R.string.save_added_item, uiState.itemDetails.regNo ?: "") to
                painterResource(R.drawable.rounded_save)
        false -> stringResource(R.string.save_edited_item, uiState.itemDetails.regNo ?: "") to
                painterResource(R.drawable.rounded_save_as)
    }
    val topBarTitle: String = if (!uiState.isNew) {
        stringResource(R.string.edit_item_title, uiState.itemDetails.regNo ?: "")
    } else {
        stringResource(R.string.add_item_title)
    }
    BackHandler(enabled = true) {
        onBackBehavior()
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ItemEntryTopAppBar(
                title = topBarTitle,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
                scrollBehavior = scrollBehavior,
                onBack = onBackBehavior,
                onSave = onSaveBehavior,
                saveIcon = saveButtonIcon,
                isSaveEnabled = uiState.isValidEntry,
                onCopy = { viewModel.copyItemDetailsToClipboard(context, uiState.itemDetails) },
                onPaste = { viewModel.pasteItemDetailsFromClipboard(context) }
            )
        },
        content = { innerPadding ->
            ItemEntryScreenContent(
                viewModel,
                userPreferences,
                uiState,
                saveButtonText,
                saveButtonIcon,
                modifier = Modifier.padding(innerPadding),
                onValueChange,
                onSave = onSaveBehavior
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
private fun ItemEntryScreenContent(
    viewModel: ItemEntryViewModel,
    userPreferences: UserPreferences,
    uiState: ItemEntryUiState,
    saveButtonText: String,
    saveButtonIcon: Painter,
    modifier: Modifier,
    onValueChange: (ItemDetails) -> Unit = {},
    onSave: () -> Unit
) {
    val collections = viewModel.getCollections()
    val localeCode = userPreferences.userCountry //TODO implement units based on preferences

    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        EntrySection(
            type = EntrySectionType.COMMON_DETAILS,
            image = { EntryFormImage(uiState, onValueChange, Modifier.padding(10.dp)) }
        ) {
            Row {
                EntryField(
                    label = stringResource(R.string.reg_no),
                    value = uiState.itemDetails.regNo ?: "",
                    onValueChange = { onValueChange(uiState.itemDetails.copy(regNo = it)) },
                    modifier = Modifier.weight(1f),
                    capitalization = KeyboardCapitalization.Characters
                )
                Spacer(modifier = Modifier.width(10.dp))
                EntryField(
                    label = "id debug",
                    value = uiState.itemDetails.id.toString(),
                    onValueChange = { /*NULL*/ },
                    modifier = Modifier.weight(0.5f),
                    enabled = false
                )
            }
            EntryField(
                label = "imagePath debug",
                value = uiState.itemDetails.imagePath.toString(),
                onValueChange = { /*NULL*/ },
                enabled = false,
                singleLine = false
            )
            ItemEntryVerticalSpacer()

            EntryField(
                label = stringResource(R.string.country),
                value = uiState.itemDetails.country ?: "",
                onValueChange = { onValueChange(uiState.itemDetails.copy(country = it)) }
            )
            EntryField(
                label = stringResource(R.string.subdivision),
                value = uiState.itemDetails.region1st ?: "",
                onValueChange = { onValueChange(uiState.itemDetails.copy(region1st = it)) }
            )
            EntryField(
                label = stringResource(R.string.region),
                value = uiState.itemDetails.region2nd ?: "",
                onValueChange = { onValueChange(uiState.itemDetails.copy(region2nd = it)) }
            )
            EntryField(
                label = stringResource(R.string.region_second),
                value = uiState.itemDetails.region3rd ?: "",
                onValueChange = { onValueChange(uiState.itemDetails.copy(region3rd = it)) }
            )
            ItemEntryVerticalSpacer()

            EntryField(
                label = stringResource(R.string.type),
                value = uiState.itemDetails.type ?: "",
                onValueChange = { onValueChange(uiState.itemDetails.copy(type = it)) }
            )
            Row {
                EntryField(
                    label = stringResource(R.string.period_start),
                    value = uiState.itemDetails.periodStart?.toString() ?: "",
                    onValueChange = { newValue ->
                        onValueChange(
                            uiState.itemDetails.copy(
                                periodStart = if (newValue.isValidYear()) newValue.toInt()
                                else null)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.width(10.dp))
                EntryField(
                    label = stringResource(R.string.period_end),
                    value = uiState.itemDetails.periodEnd?.toString() ?: "",
                    onValueChange = { newValue ->
                        onValueChange(
                            uiState.itemDetails.copy(
                                periodEnd = if (newValue.isValidYear()) newValue.toInt()
                                else null)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Number
                )
            }
            EntryField(
                label = stringResource(R.string.year),
                value = uiState.itemDetails.year?.toString() ?: "",
                onValueChange = { newValue ->
                    onValueChange(
                        uiState.itemDetails.copy(
                            year = if (newValue.isValidYear()) newValue.toInt() else null)
                    )
                },
                keyboardType = KeyboardType.Number
            )
            ItemEntryVerticalSpacer()
        }
        if (uiState.itemType == ItemType.PLATE && collections.isNotEmpty()) {
            EntrySection(
                type = EntrySectionType.COLLECTIONS,
            ) {
                collections.forEach { collection ->
                    FilterChip(
                        selected = uiState.selectedCollections.any { it == collection },
                        onClick = { viewModel.toggleCollectionSelection(collection) },
                        label = { Text(collection.name) },
                        leadingIcon = {
                            if (!collection.emoji.isNullOrBlank()) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Text(collection.emoji)
                                }
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.rounded_bookmark),
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }
        EntrySection(
            type = EntrySectionType.UNIQUE_DETAILS
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)) {
                EntryFieldBackground {
                    EntryField(
                        label = stringResource(R.string.notes),
                        value = uiState.itemDetails.notes ?: "",
                        onValueChange = { onValueChange(uiState.itemDetails.copy(notes = it)) },
                        singleLine = false
                    )
                    if (uiState.itemType != ItemType.WANTED_PLATE) {
                        EntryField(
                            label = stringResource(R.string.vehicle),
                            value = uiState.itemDetails.vehicle ?: "",
                            onValueChange = { onValueChange(uiState.itemDetails.copy(vehicle = it)) },
                            singleLine = false
                        )
                    }
                }
                if (uiState.itemType != ItemType.WANTED_PLATE) {
                    EntryFieldBackground {
                        EntryField(
                            label = stringResource(R.string.date),
                            //placeholder = "1922-05-18",
                            value = uiState.itemDetails.date ?: "",
                            onValueChange = { onValueChange(uiState.itemDetails.copy(date = it)) },
                            keyboardType = KeyboardType.Number,
                        )
                    }
                    Row {
                        EntryFieldBackground(modifier = Modifier.weight(1f)) {
                            EntryField(
                                label = stringResource(R.string.cost),
                                value = uiState.itemDetails.cost?.toString() ?: "",
                                onValueChange = { onValueChange( // TODO improve input logic
                                    uiState.itemDetails.copy(cost = it.toLongOrNull() ?: 0L))
                                },
                                keyboardType = KeyboardType.Number
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        EntryFieldBackground(modifier = Modifier.weight(1f)) {
                            EntryField(
                                label = stringResource(R.string.value),
                                value = uiState.itemDetails.value?.toString() ?: "",
                                onValueChange = { newValue ->
                                    onValueChange(uiState.itemDetails.copy(
                                        value = if (newValue.isBlankOrZero()) null
                                        else newValue.toLongOrNull())
                                    )
                                },
                                keyboardType = KeyboardType.Number
                            )
                        }
                    }
                    if (uiState.itemType == ItemType.PLATE) {
                        EntryFieldBackground {
                            EntryField(
                                label = stringResource(R.string.location),
                                value = uiState.itemDetails.status ?: "",
                                onValueChange = {
                                    onValueChange(uiState.itemDetails.copy(status = it))
                                }
                            )
                        }
                    }
                }
            }
        }
        EntrySection(
            label = stringResource(R.string.physical_attributes)
        ) {
            if (uiState.itemType != ItemType.WANTED_PLATE) {
                Row {
                    EntryFieldBackground(modifier = Modifier.weight(1f)) {
                        EntryField(
                            label = stringResource(R.string.width),
                            value = uiState.itemDetails.width?.toString() ?: "",
                            onValueChange = { newValue ->
                                onValueChange(uiState.itemDetails.copy(
                                    width = if (newValue.isBlankOrZero()) null
                                    else newValue.toIntOrNull())
                                )
                            },
                            keyboardType = KeyboardType.Number
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    EntryFieldBackground(modifier = Modifier.weight(1f)) {
                        EntryField(
                            label = stringResource(R.string.height),
                            value = uiState.itemDetails.height?.toString() ?: "",
                            onValueChange = { newValue ->
                                onValueChange(uiState.itemDetails.copy(
                                    height = if (newValue.isBlankOrZero()) null
                                    else newValue.toIntOrNull())
                                )
                            },
                            keyboardType = KeyboardType.Number
                        )
                    }
                }
                EntryFieldBackground {
                    EntryField(
                        label = stringResource(R.string.weight),
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
            }
            EntryFieldBackground {
                EntryField(
                    label = stringResource(R.string.color_main),
                    value = uiState.itemDetails.colorMain ?: "",
                    onValueChange = { onValueChange(uiState.itemDetails.copy(colorMain = it)) }
                )
                EntryField(
                    label = stringResource(R.string.color_secondary),
                    value = uiState.itemDetails.colorSecondary ?: "",
                    onValueChange = { onValueChange(uiState.itemDetails.copy(colorSecondary = it)) }
                )
            }
        }
        if (uiState.itemType != ItemType.WANTED_PLATE) {
            EntrySection(
                label = stringResource(R.string.source)
            ) {
                EntryFieldBackground {
                    EntryField(
                        label = stringResource(R.string.source_name),
                        value = uiState.itemDetails.sourceName ?: "",
                        onValueChange = { onValueChange(uiState.itemDetails.copy(sourceName = it)) }
                    )
                    EntryField(
                        label = stringResource(R.string.source_alias),
                        value = uiState.itemDetails.sourceAlias ?: "",
                        onValueChange = { onValueChange(uiState.itemDetails.copy(sourceAlias = it)) }
                    )
                }
                EntryFieldBackground {
                    EntryField(
                        label = stringResource(R.string.source_type),
                        value = uiState.itemDetails.sourceType ?: "",
                        onValueChange = { onValueChange(uiState.itemDetails.copy(sourceType = it)) }
                    )
                }
                EntryFieldBackground {
                    EntryField(
                        label = stringResource(R.string.source_country),
                        value = uiState.itemDetails.sourceCountry ?: "",
                        onValueChange = { onValueChange(uiState.itemDetails.copy(sourceCountry = it)) }
                    )
                }
                EntryFieldBackground {
                    EntryField(
                        label = stringResource(R.string.source_details),
                        value = uiState.itemDetails.sourceDetails ?: "",
                        onValueChange = { onValueChange(uiState.itemDetails.copy(sourceDetails = it)) },
                        imeAction = if (uiState.itemType != ItemType.FORMER_PLATE) ImeAction.Done
                        else ImeAction.Next
                    )
                }
            }
        }
        if (uiState.itemType == ItemType.FORMER_PLATE) {
            EntrySection(
                label = stringResource(R.string.archival)
            ) {
                EntryFieldBackground {
                    EntryField(
                        label = stringResource(R.string.archival_date),
                        value = uiState.itemDetails.archivalDate ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(archivalDate = it))
                        },
                        keyboardType = KeyboardType.Number
                    )
                }
                EntryFieldBackground {
                    EntryField(
                        label = stringResource(R.string.archival_reason),
                        value = uiState.itemDetails.archivalType ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(archivalType = it))
                        }
                    )
                    EntryField(
                        label = stringResource(R.string.sold_price),
                        value = uiState.itemDetails.price?.toString() ?: "",
                        onValueChange = { newValue ->
                            onValueChange(uiState.itemDetails.copy(
                                price = if (newValue.isBlankOrZero()) null
                                else newValue.toLongOrNull())
                            )
                        },
                        keyboardType = KeyboardType.Number,
                    )
                }
                EntryFieldBackground {
                    EntryField(
                        label = stringResource(R.string.recipient_name),
                        value = uiState.itemDetails.recipientName ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(recipientName = it))
                        }
                    )
                    EntryField(
                        label = stringResource(R.string.recipient_alias),
                        value = uiState.itemDetails.recipientAlias ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(recipientAlias = it))
                        }
                    )
                    EntryField(
                        label = stringResource(R.string.recipient_country),
                        value = uiState.itemDetails.recipientCountry ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(recipientCountry = it))
                        }
                    )
                }
                EntryFieldBackground {
                    EntryField(
                        label = stringResource(R.string.archival_details),
                        value = uiState.itemDetails.archivalDetails ?: "",
                        onValueChange = {
                            onValueChange(uiState.itemDetails.copy(archivalDetails = it))
                        },
                        imeAction = ImeAction.Done
                    )
                }
            }
        }
        Button(
            onClick = onSave,
            enabled = uiState.isValidEntry,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 32.dp)
        ) {
            Icon(
                painter = saveButtonIcon,
                contentDescription = null,
                modifier = Modifier.padding(2.dp)
            )
            Text(
                text = saveButtonText,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun EntryFormImage(
    uiState: ItemEntryUiState,
    onValueChange: (ItemDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    val imagePath: String? = pickItemImage(uiState.itemDetails.imagePath, modifier)
    val updateUiState: (ItemDetails) -> Unit = {
        onValueChange(uiState.itemDetails.copy(imagePath = imagePath))
    }
    updateUiState.invoke(uiState.itemDetails)
}

@Composable
private fun EntrySection(
    type: EntrySectionType = EntrySectionType.GENERAL,
    label: String? = null,
    image: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    when (type) {
        EntrySectionType.COMMON_DETAILS ->
            EntryCardCommonDetails(image = image!!, content = content)
        EntrySectionType.UNIQUE_DETAILS ->
            EntryCardUniqueDetails(content = content)
        EntrySectionType.COLLECTIONS ->
            EntryCardCollections(content = content)
        EntrySectionType.GENERAL ->
            EntryCardGeneral(label = label, content = content)
    }
}

@Composable
private fun EntryCardCommonDetails(
    image: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 24.dp,
            bottomEnd = 24.dp
        ),
        modifier = Modifier.padding(bottom = 0.dp)
    ) {
        image()
        Column(modifier = Modifier.padding(horizontal = 28.dp)) {
            content()
        }
    }
}

@Composable
private fun EntryCardUniqueDetails(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 16.dp)
    ) {
        Card(
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(20.dp)
        ) {
            content()
        }
        IconQuotationMark(
            size = 56.dp,
            isFlipped = true,
            modifier = Modifier.offset(x = 24.dp, y = (-32).dp)
        )
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.matchParentSize()
        ) {
            IconQuotationMark(
                size = 56.dp,
                modifier = Modifier.offset(x = (-24).dp, y = 32.dp)
            )
        }
    }
}

@Composable
private fun EntryCardCollections(
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
private fun EntryCardGeneral(
    label: String? = null,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            label?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            content()
        }
    }
}

@Composable
private fun EntryFieldBackground(
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
    content: @Composable () -> Unit
) {
    Card(
        colors = colors,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun EntryField(
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
    /*  TODO modify EntryFormField
    *
    *   - Pre- and Suffix for currencies etc. units?
    *   - Fix singleLine
    *   - Get rid of Row, separate icon and the TextField, place inside a Column?
    *       (utilize Spacer or Divider???)
    *
    * */
    val currentValue = remember(value) { value }

    Row(modifier = modifier.padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = currentValue,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(
                capitalization = capitalization,
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            label = {
                Text(
                    text = label,
                    maxLines = if (singleLine) 1 else Int.MAX_VALUE,
                    overflow = TextOverflow.Ellipsis
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = singleLine
        )
    }
}

private enum class EntrySectionType {
    COMMON_DETAILS,
    UNIQUE_DETAILS,
    COLLECTIONS,
    GENERAL,
}
