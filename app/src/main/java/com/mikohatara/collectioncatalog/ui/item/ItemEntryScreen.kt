package com.mikohatara.collectioncatalog.ui.item

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.ItemDetails
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.data.UserPreferences
import com.mikohatara.collectioncatalog.ui.components.DiscardDialog
import com.mikohatara.collectioncatalog.ui.components.EntryDialog
import com.mikohatara.collectioncatalog.ui.components.IconCollectionLabel
import com.mikohatara.collectioncatalog.ui.components.IconQuotationMark
import com.mikohatara.collectioncatalog.ui.components.ItemEntryTopAppBar
import com.mikohatara.collectioncatalog.ui.components.ItemEntryVerticalSpacer
import com.mikohatara.collectioncatalog.ui.components.pickItemImage
import com.mikohatara.collectioncatalog.util.getCalendarLocale
import com.mikohatara.collectioncatalog.util.getCurrencySymbol
import com.mikohatara.collectioncatalog.util.getMeasurementUnitSymbol
import com.mikohatara.collectioncatalog.util.isBlankOrZero
import com.mikohatara.collectioncatalog.util.isValidYear
import com.mikohatara.collectioncatalog.util.rememberCurrencyVisualTransformation
import com.mikohatara.collectioncatalog.util.rememberMeasurementVisualTransformation
import com.mikohatara.collectioncatalog.util.toFormattedDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@Composable
fun ItemEntryScreen(
    viewModel: ItemEntryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.deleteUnusedImages(context)
    }

    ItemEntryScreen(
        viewModel,
        userPreferences,
        uiState,
        context,
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
    context: Context,
    onValueChange: (ItemDetails) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    val (saveButtonText, saveButtonIcon) = when (uiState.isNew) {
        true -> stringResource(R.string.save_added_item, uiState.itemDetails.regNo ?: "") to
                painterResource(R.drawable.rounded_save)
        false -> stringResource(R.string.save_edited_item, uiState.itemDetails.regNo ?: "") to
                painterResource(R.drawable.rounded_save_as)
    }
    val topBarTitle = if (!uiState.isNew) {
        stringResource(R.string.edit_item_title, uiState.itemDetails.regNo ?: "")
    } else {
        stringResource(R.string.add_item_title)
    }
    val saveToast = if (uiState.itemType == ItemType.WANTED_PLATE) {
        if (uiState.isNew) stringResource(R.string.saved_to_wishlist)
        else stringResource(R.string.saved_generic)
    } else {
        if (uiState.isNew) stringResource(R.string.saved_new_item, uiState.itemDetails.regNo ?: "")
        else stringResource(R.string.saved_old_item, uiState.itemDetails.regNo ?: "")
    }
    val copyToast = stringResource(R.string.copied)
    val pasteToast = stringResource(R.string.pasted)
    val onBackBehavior = { if (uiState.hasUnsavedChanges) showDiscardDialog = true else onBack() }
    val onSaveBehavior = {
        viewModel.saveEntry(context); viewModel.showToast(context, saveToast); onBack()
    }
    val isPasteEnabled = viewModel.canPasteFromInternalClipboard.collectAsState()
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
                onCopy = {
                    viewModel.copyItemDetails()
                    viewModel.showToast(context, copyToast)
                },
                onPaste = {
                    viewModel.pasteItemDetails()
                    viewModel.showToast(context, pasteToast)
                },
                isPasteEnabled = isPasteEnabled.value
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
    val localeCode = userPreferences.userCountry
    val lengthUnit = getMeasurementUnitSymbol(userPreferences.lengthUnit)
    val weightUnit = getMeasurementUnitSymbol(userPreferences.weightUnit)

    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        EntrySection(
            type = EntrySectionType.COMMON_DETAILS,
            image = { EntryFormImage(viewModel, uiState, Modifier.padding(10.dp)) }
        ) {
            Row {
                EntryField(
                    label = stringResource(R.string.reg_no),
                    value = uiState.itemDetails.regNo ?: "",
                    onValueChange = { onValueChange(uiState.itemDetails.copy(regNo = it)) },
                    modifier = Modifier.weight(1f),
                    capitalization = KeyboardCapitalization.Characters,
                )
                Spacer(modifier = Modifier.weight(0.33f))//.width(10.dp))
                /*EntryField(
                    label = "id debug",
                    value = uiState.itemDetails.id.toString(),
                    onValueChange = { /*NULL*/ },
                    modifier = Modifier.weight(0.5f),
                    enabled = false
                )*/
            }
            /*EntryField(
                label = "imagePath debug",
                value = uiState.itemDetails.imagePath.toString(),
                onValueChange = { /*NULL*/ },
                enabled = false,
                singleLine = false
            )*/
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
            InfoField(
                text = stringResource(R.string.info_region_1st),
                modifier = Modifier.padding(top = 4.dp)
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
                        label = { Text(
                            text = collection.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        ) },
                        leadingIcon = {
                            if (!collection.emoji.isNullOrBlank()) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Text(collection.emoji)
                                }
                            } else {
                                IconCollectionLabel(
                                    color = collection.color.color
                                )
                            }
                        }
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
                        singleLine = false,
                        hasEntryDialog = true
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
                        DatePickerField(
                            label = stringResource(R.string.date),
                            dateValue = uiState.itemDetails.date ?: "",
                            onDateSelected = { onValueChange(uiState.itemDetails.copy(date = it)) },
                            userCountry = localeCode
                        )
                    }
                    Row {
                        EntryFieldBackground(modifier = Modifier.weight(1f)) {
                            EntryField(
                                label = stringResource(R.string.cost),
                                placeholder = { Text(getCurrencySymbol(localeCode)) },
                                value = uiState.itemDetails.cost?.toString() ?: "",
                                onValueChange = { onValueChange(
                                    uiState.itemDetails.copy(cost = it.toLongOrNull() ?: 0L))
                                },
                                keyboardType = KeyboardType.Number,
                                isCurrency = true,
                                localeCode = localeCode
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        if (uiState.itemType == ItemType.FORMER_PLATE) {
                            Spacer(modifier = Modifier.weight(1f))
                        } else {
                            EntryFieldBackground(modifier = Modifier.weight(1f)) {
                                EntryField(
                                    label = stringResource(R.string.value),
                                    placeholder = { Text(getCurrencySymbol(localeCode)) },
                                    value = uiState.itemDetails.value?.toString() ?: "",
                                    onValueChange = { newValue ->
                                        onValueChange(uiState.itemDetails.copy(
                                            value = if (newValue.isBlankOrZero()) null
                                            else newValue.toLongOrNull())
                                        )
                                    },
                                    keyboardType = KeyboardType.Number,
                                    isCurrency = true,
                                    localeCode = localeCode
                                )
                            }
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
            InfoField(stringResource(R.string.info_width))
            Row {
                EntryFieldBackground(modifier = Modifier.weight(1f)) {
                    EntryField(
                        label = stringResource(R.string.width),
                        placeholder = { Text(lengthUnit) },
                        value = uiState.itemDetails.width?.toString() ?: "",
                        onValueChange = { newValue ->
                            onValueChange(uiState.itemDetails.copy(
                                width = if (newValue.isBlankOrZero()) null
                                else newValue.toIntOrNull())
                            )
                        },
                        keyboardType = KeyboardType.Number,
                        isMeasurement = true,
                        measurementUnit = lengthUnit,
                        localeCode = localeCode
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                EntryFieldBackground(modifier = Modifier.weight(1f)) {
                    EntryField(
                        label = stringResource(R.string.height),
                        placeholder = { Text(lengthUnit) },
                        value = uiState.itemDetails.height?.toString() ?: "",
                        onValueChange = { newValue ->
                            onValueChange(uiState.itemDetails.copy(
                                height = if (newValue.isBlankOrZero()) null
                                else newValue.toIntOrNull())
                            )
                        },
                        keyboardType = KeyboardType.Number,
                        isMeasurement = true,
                        measurementUnit = lengthUnit,
                        localeCode = localeCode
                    )
                }
            }
            EntryFieldBackground {
                EntryField(
                    label = stringResource(R.string.weight),
                    placeholder = { Text(weightUnit) },
                    value = uiState.itemDetails.weight?.toString() ?: "",
                    onValueChange = { newValue ->
                        onValueChange(uiState.itemDetails.copy(
                            weight = if (newValue.isBlankOrZero()) null
                            else newValue.toIntOrNull())
                        )
                    },
                    keyboardType = KeyboardType.Number,
                    isMeasurement = true,
                    measurementUnit = weightUnit,
                    localeCode = localeCode
                )
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
                    onValueChange = { onValueChange(uiState.itemDetails.copy(colorSecondary = it)) },
                    imeAction = if (uiState.itemType == ItemType.WANTED_PLATE) ImeAction.Done
                    else ImeAction.Next
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
                    DatePickerField(
                        label = stringResource(R.string.archival_date),
                        dateValue = uiState.itemDetails.archivalDate ?: "",
                        onDateSelected = {
                            onValueChange(uiState.itemDetails.copy(archivalDate = it))
                        },
                        userCountry = localeCode
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
                        placeholder = { Text(getCurrencySymbol(localeCode)) },
                        value = uiState.itemDetails.price?.toString() ?: "",
                        onValueChange = { newValue ->
                            onValueChange(uiState.itemDetails.copy(
                                price = if (newValue.isBlankOrZero()) null
                                else newValue.toLongOrNull())
                            )
                        },
                        keyboardType = KeyboardType.Number,
                        isCurrency = true,
                        localeCode = localeCode
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
    viewModel: ItemEntryViewModel,
    uiState: ItemEntryUiState,
    modifier: Modifier = Modifier
) {
    pickItemImage(
        existingImagePath = uiState.itemDetails.imagePath,
        modifier = modifier,
        temporaryImageUri = uiState.temporaryImageUri,
        onPick = viewModel::handlePickedImage,
        onRemove = viewModel::clearImagePath
    )
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
    placeholder: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    hasEntryDialog: Boolean = false,
    isCurrency: Boolean = false,
    localeCode: String = "",
    isMeasurement: Boolean = false,
    measurementUnit: String = ""
) {
    var showEntryDialog by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusManager = LocalFocusManager.current

    val currentValue = remember(value, isCurrency, localeCode, isMeasurement, measurementUnit) {
        if (isCurrency && value.isNotBlank()) {
            val longValue: Long = value.toLongOrNull() ?: 0L
            longValue.toString()
        } else if (isMeasurement && value.isNotBlank()) {
            val intValue: Int = value.toIntOrNull() ?: 0
            intValue.toString()
        } else {
            value
        }
    }
    val visualTransformation = if (isCurrency) {
        rememberCurrencyVisualTransformation(localeCode)
    } else if (isMeasurement) {
        rememberMeasurementVisualTransformation(measurementUnit, localeCode)
    } else VisualTransformation.None

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        OutlinedTextField(
            value = currentValue,
            onValueChange = { newValue ->
                if (isCurrency || isMeasurement) {
                    val rawValue = newValue.replace(Regex("\\D"), "")
                    onValueChange(rawValue)
                } else {
                    onValueChange(newValue)
                }
            },
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
            placeholder = placeholder,
            trailingIcon = { if (isFocused && currentValue.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_close_24),
                        contentDescription = null
                    )
                }
            }},
            modifier = Modifier.fillMaxWidth().weight(1f),
            enabled = enabled,
            singleLine = singleLine,
            interactionSource = interactionSource,
            visualTransformation = visualTransformation
        )
        if (hasEntryDialog && isFocused) {
            FilledTonalIconButton(
                onClick = { showEntryDialog = !showEntryDialog  },
                modifier = Modifier.padding(start = 10.dp, top = 8.dp, end = 2.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.rounded_edit_square),
                    contentDescription = null
                )
            }
        }
    }

    if (showEntryDialog) {
        EntryDialog(
            value = currentValue,
            onValueChange = { onValueChange(it) },
            label = label,
            onDismiss = { showEntryDialog = false },
            onConfirm = {
                showEntryDialog = false

                focusManager.moveFocus(FocusDirection.Next)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    dateValue: String,
    onDateSelected: (String) -> Unit,
    userCountry: String,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusManager = LocalFocusManager.current

    val displayValue = dateValue.toFormattedDate(userCountry)
    val locale = getCalendarLocale(userCountry)
    val inputFormat = remember(locale) { SimpleDateFormat("yyyy-MM-dd", locale) }

    val initialSelectedDateMillis = remember(dateValue) {
        if (dateValue.isNotBlank()) {
            try {
                inputFormat.parse(dateValue)?.time
            } catch (e: Exception) {
                Calendar.getInstance().timeInMillis
            }
        } else {
            Calendar.getInstance().timeInMillis
        }
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis
    )

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        OutlinedTextField(
            value = displayValue,
            onValueChange = { showDatePicker = true },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            label = { Text(label) },
            trailingIcon = { if (isFocused && displayValue.isNotEmpty()) {
                IconButton(onClick = { onDateSelected("") }) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_close_24),
                        contentDescription = null
                    )
                } }
            },
            interactionSource = interactionSource,
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
        if (isFocused) {
            FilledTonalIconButton(
                onClick = { showDatePicker = !showDatePicker  },
                modifier = Modifier.padding(start = 10.dp, top = 8.dp, end = 2.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.rounded_event),
                    contentDescription = null
                )
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Date(millis)
                            onDateSelected(inputFormat.format(date))
                        }
                        focusManager.moveFocus(FocusDirection.Next)
                    },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text(stringResource(R.string.ok_text))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}

@Composable
private fun InfoField(text: String, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.rounded_info),
            contentDescription = null,
            tint = colorScheme.outline,
            modifier = Modifier.size(24.dp).padding(end = 8.dp)
        )
        Text(
            text = text,
            color = colorScheme.outline,
            style = typography.bodySmall
        )
    }
}

private enum class EntrySectionType {
    COMMON_DETAILS,
    UNIQUE_DETAILS,
    COLLECTIONS,
    GENERAL
}
