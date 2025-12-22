package com.mikohatara.collectioncatalog.ui.item

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Collection
import com.mikohatara.collectioncatalog.data.ItemDetails
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.ui.components.DiscardDialog
import com.mikohatara.collectioncatalog.ui.components.EntryDialog
import com.mikohatara.collectioncatalog.ui.components.IconCollectionLabel
import com.mikohatara.collectioncatalog.ui.components.ItemEntryTopAppBar
import com.mikohatara.collectioncatalog.ui.components.Loading
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
    val context = LocalContext.current
    val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isPasteEnabled by viewModel.canPasteFromInternalClipboard.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.deleteUnusedImages(context)
    }

    ItemEntryScreen(
        context = context,
        itemDetails = uiState.itemDetails,
        itemType = uiState.itemType,
        temporaryImageUri = uiState.temporaryImageUri,
        allCollections = viewModel.getCollections(),
        selectedCollections = uiState.selectedCollections,
        localeCode = userPreferences.userCountry,
        lengthUnit = getMeasurementUnitSymbol(userPreferences.lengthUnit),
        weightUnit = getMeasurementUnitSymbol(userPreferences.weightUnit),
        isLoading = uiState.isLoading,
        isNew = uiState.isNew,
        isValidEntry = uiState.isValidEntry,
        isPasteEnabled = isPasteEnabled,
        hasUnsavedChanges = uiState.hasUnsavedChanges,
        showToast = viewModel::showToast,
        onBack = onBack,
        onSave = viewModel::saveEntry,
        onCopy = viewModel::copyItemDetails,
        onPaste = viewModel::pasteItemDetails,
        onValueChange = viewModel::updateUiState,
        onImagePicked = viewModel::handlePickedImage,
        onImageRemoved = viewModel::clearImagePath,
        onToggleCollection = viewModel::toggleCollectionSelection
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemEntryScreen(
    context: Context,
    itemDetails: ItemDetails,
    itemType: ItemType,
    temporaryImageUri: Uri?,
    allCollections: List<Collection>,
    selectedCollections: List<Collection>,
    localeCode: String,
    lengthUnit: String,
    weightUnit: String,
    isLoading: Boolean,
    isNew: Boolean,
    isValidEntry: Boolean,
    isPasteEnabled: Boolean,
    hasUnsavedChanges: Boolean,
    showToast: (Context, String) -> Unit,
    onBack: () -> Unit,
    onSave: (Context) -> Unit,
    onCopy: () -> Unit,
    onPaste: () -> Unit,
    onValueChange: (ItemDetails) -> Unit,
    onImagePicked: (Uri?) -> Unit,
    onImageRemoved: () -> Unit,
    onToggleCollection: (Collection) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    val onDismissDiscardDialog = { showDiscardDialog = false }

    val (saveButtonText, saveButtonIcon) = if (isNew) {
        stringResource(R.string.save_added_item, itemDetails.regNo ?: "") to
                painterResource(R.drawable.rounded_save)
    } else {
        stringResource(R.string.save_edited_item, itemDetails.regNo ?: "") to
                painterResource(R.drawable.rounded_save_as)
    }
    val topBarTitle = if (!isNew) {
        stringResource(R.string.edit_item_title, itemDetails.regNo ?: "")
    } else {
        stringResource(R.string.add_item_title)
    }

    val copyToast = stringResource(R.string.copied)
    val pasteToast = stringResource(R.string.pasted)
    val saveToast = if (itemType == ItemType.WANTED_PLATE) {
        if (isNew) stringResource(R.string.saved_to_wishlist)
        else stringResource(R.string.saved_generic)
    } else {
        val regNo = itemDetails.regNo ?: ""
        if (isNew) {
            stringResource(R.string.saved_new_item, regNo)
        } else {
            stringResource(R.string.saved_old_item, regNo)
        }
    }

    val onBackBehavior = { if (hasUnsavedChanges) showDiscardDialog = true else onBack() }
    BackHandler(enabled = true) { onBackBehavior() }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ItemEntryTopAppBar(
                title = topBarTitle,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface,
                    scrolledContainerColor = colorScheme.surfaceVariant
                ),
                scrollBehavior = scrollBehavior,
                onBack = onBackBehavior,
                onSave = { onSave(context); showToast(context, saveToast); onBack() },
                saveIcon = saveButtonIcon,
                isSaveEnabled = isValidEntry,
                onCopy = { onCopy(); showToast(context, copyToast) },
                onPaste = { onPaste(); showToast(context, pasteToast) },
                isPasteEnabled = isPasteEnabled
            )
        },
        content = { innerPadding ->
            if (isLoading) {
                Loading()
            } else {
                ItemEntryScreenContent(
                    itemDetails = itemDetails,
                    itemType = itemType,
                    temporaryImageUri = temporaryImageUri,
                    allCollections = allCollections,
                    selectedCollections = selectedCollections,
                    localeCode = localeCode,
                    lengthUnit = lengthUnit,
                    weightUnit = weightUnit,
                    saveButtonText = saveButtonText,
                    saveButtonIcon = saveButtonIcon,
                    isValidEntry = isValidEntry,
                    onValueChange = onValueChange,
                    onImagePicked = onImagePicked,
                    onImageRemoved = onImageRemoved,
                    onToggleCollection = onToggleCollection,
                    onSave = { onSave(context); showToast(context, saveToast); onBack() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    )
    if (showDiscardDialog) {
        DiscardDialog(
            onConfirm = {
                onDismissDiscardDialog()
                onBack()
            },
            onCancel = onDismissDiscardDialog
        )
    }
}

@Composable
private fun ItemEntryScreenContent(
    itemDetails: ItemDetails,
    itemType: ItemType,
    temporaryImageUri: Uri?,
    allCollections: List<Collection>,
    selectedCollections: List<Collection>,
    localeCode: String,
    lengthUnit: String,
    weightUnit: String,
    saveButtonText: String,
    saveButtonIcon: Painter,
    isValidEntry: Boolean,
    onValueChange: (ItemDetails) -> Unit,
    onImagePicked: (Uri?) -> Unit,
    onImageRemoved: () -> Unit,
    onToggleCollection: (Collection) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        EntryFormImage(
            existingImagePath = itemDetails.imagePath,
            tempImagePath = temporaryImageUri,
            onPick = onImagePicked,
            onRemove = onImageRemoved,
            modifier = Modifier.padding(16.dp)//10.dp)
        )
        EntryFormSection(
            label = stringResource(R.string.common_details_label),
            isFirst = true
        ) {
            EntryField(
                label = stringResource(R.string.reg_no),
                value = itemDetails.regNo ?: "",
                onValueChange = { onValueChange(itemDetails.copy(regNo = it)) },
                capitalization = KeyboardCapitalization.Characters
            )
            EntryFormHorizontalSpacer()
            EntryField(
                label = stringResource(R.string.country),
                value = itemDetails.country ?: "",
                onValueChange = { onValueChange(itemDetails.copy(country = it)) }
            )
            EntryField(
                label = stringResource(R.string.subdivision),
                value = itemDetails.region1st ?: "",
                onValueChange = { onValueChange(itemDetails.copy(region1st = it)) }
            )
            InfoField(text = stringResource(R.string.info_region_1st))
            EntryField(
                label = stringResource(R.string.region),
                value = itemDetails.region2nd ?: "",
                onValueChange = { onValueChange(itemDetails.copy(region2nd = it)) }
            )
            EntryField(
                label = stringResource(R.string.region_second),
                value = itemDetails.region3rd ?: "",
                onValueChange = { onValueChange(itemDetails.copy(region3rd = it)) }
            )
            EntryFormHorizontalSpacer()
            EntryField(
                label = stringResource(R.string.type),
                value = itemDetails.type ?: "",
                onValueChange = { onValueChange(itemDetails.copy(type = it)) }
            )
            Row {
                EntryField(
                    label = stringResource(R.string.period_start),
                    value = itemDetails.periodStart?.toString() ?: "",
                    onValueChange = { newValue ->
                        onValueChange(
                            itemDetails.copy(
                                periodStart = if (newValue.isValidYear()) newValue.toInt()
                                else null
                            )
                        )
                    },
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f),
                )
                EntryFormVerticalSpacer()
                EntryField(
                    label = stringResource(R.string.period_end),
                    value = itemDetails.periodEnd?.toString() ?: "",
                    onValueChange = { newValue ->
                        onValueChange(
                            itemDetails.copy(
                                periodEnd = if (newValue.isValidYear()) newValue.toInt() else null
                            )
                        )
                    },
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }
            EntryField(
                label = stringResource(R.string.year),
                value = itemDetails.year?.toString() ?: "",
                onValueChange = { newValue ->
                    onValueChange(
                        itemDetails
                            .copy(year = if (newValue.isValidYear()) newValue.toInt() else null)
                    )
                },
                keyboardType = KeyboardType.Number
            )
        }
        if (itemType == ItemType.PLATE && allCollections.isNotEmpty()) {
            EntryFormSection(label = stringResource(R.string.collections)) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    allCollections.forEach { collection ->
                        FilterChip(
                            selected = selectedCollections.any { it.id == collection.id },
                            onClick = { onToggleCollection(collection) },
                            label = {
                                Text(
                                    text = collection.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            leadingIcon = {
                                if (!collection.emoji.isNullOrBlank()) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Text(collection.emoji)
                                    }
                                } else {
                                    IconCollectionLabel(color = collection.color.color)
                                }
                            }
                        )
                    }
                }
            }
        }
        EntryFormSection(label = stringResource(R.string.unique_details_label)) {
            EntryField(
                label = stringResource(R.string.notes),
                value = itemDetails.notes ?: "",
                onValueChange = { onValueChange(itemDetails.copy(notes = it)) },
                singleLine = false,
                hasEntryDialog = true
            )
            if (itemType != ItemType.WANTED_PLATE) {
                EntryField(
                    label = stringResource(R.string.vehicle),
                    value = itemDetails.vehicle ?: "",
                    onValueChange = { onValueChange(itemDetails.copy(vehicle = it)) },
                    singleLine = false
                )
                EntryFormHorizontalSpacer()
                DatePickerField(
                    label = stringResource(R.string.date),
                    dateValue = itemDetails.date ?: "",
                    onDateSelected = { onValueChange(itemDetails.copy(date = it)) },
                    userCountry = localeCode
                )
                Row {
                    EntryField(
                        label = stringResource(R.string.cost),
                        placeholder = {
                            Text(getCurrencySymbol(localeCode))
                        },
                        value = itemDetails.cost?.toString() ?: "",
                        onValueChange = { onValueChange(
                            itemDetails.copy(cost = it.toLongOrNull() ?: 0L))
                        },
                        keyboardType = KeyboardType.Number,
                        isCurrency = true,
                        localeCode = localeCode,
                        modifier = Modifier.weight(1f)
                    )
                    EntryFormVerticalSpacer()
                    if (itemType == ItemType.FORMER_PLATE) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        EntryField(
                            label = stringResource(R.string.value),
                            placeholder = {
                                Text(getCurrencySymbol(localeCode))
                            },
                            value = itemDetails.value?.toString() ?: "",
                            onValueChange = { newValue ->
                                onValueChange(itemDetails.copy(
                                    value = if (newValue.isBlankOrZero()) null
                                    else newValue.toLongOrNull())
                                )
                            },
                            keyboardType = KeyboardType.Number,
                            isCurrency = true,
                            localeCode = localeCode,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                if (itemType == ItemType.PLATE) {
                    EntryFormHorizontalSpacer()
                    EntryField(
                        label = stringResource(R.string.location),
                        value = itemDetails.status ?: "",
                        onValueChange = {
                            onValueChange(itemDetails.copy(status = it))
                        }
                    )
                }
            }
        }
        EntryFormSection(label = stringResource(R.string.size)) {
            Row {
                EntryField(
                    label = stringResource(R.string.width),
                    placeholder = { Text(lengthUnit) },
                    value = itemDetails.width?.toString() ?: "",
                    onValueChange = { newValue ->
                        onValueChange(itemDetails.copy(
                            width = if (newValue.isBlankOrZero()) null
                            else newValue.toIntOrNull())
                        )
                    },
                    keyboardType = KeyboardType.Number,
                    isMeasurement = true,
                    measurementUnit = lengthUnit,
                    localeCode = localeCode,
                    modifier = Modifier.weight(1f)
                )
                EntryFormVerticalSpacer()
                EntryField(
                    label = stringResource(R.string.height),
                    placeholder = { Text(lengthUnit) },
                    value = itemDetails.height?.toString() ?: "",
                    onValueChange = { newValue ->
                        onValueChange(itemDetails.copy(
                            height = if (newValue.isBlankOrZero()) null
                            else newValue.toIntOrNull())
                        )
                    },
                    keyboardType = KeyboardType.Number,
                    isMeasurement = true,
                    measurementUnit = lengthUnit,
                    localeCode = localeCode,
                    modifier = Modifier.weight(1f)
                )
            }
            InfoField(stringResource(R.string.info_width))
            EntryField(
                label = stringResource(R.string.weight),
                placeholder = { Text(weightUnit) },
                value = itemDetails.weight?.toString() ?: "",
                onValueChange = { newValue ->
                    onValueChange(itemDetails.copy(
                        weight = if (newValue.isBlankOrZero()) null
                        else newValue.toIntOrNull())
                    )
                },
                keyboardType = KeyboardType.Number,
                isMeasurement = true,
                measurementUnit = weightUnit,
                localeCode = localeCode
            )
            EntryFormSectionLabel(
                label = stringResource(R.string.color),
                modifier = Modifier.padding(top = 24.dp)
            )
            EntryField(
                label = stringResource(R.string.color_base),
                value = itemDetails.colorMain ?: "",
                onValueChange = { onValueChange(itemDetails.copy(colorMain = it)) }
            )
            EntryField(
                label = stringResource(R.string.color_characters),
                value = itemDetails.colorSecondary ?: "",
                onValueChange = { onValueChange(itemDetails.copy(colorSecondary = it)) },
                imeAction = if (itemType == ItemType.WANTED_PLATE) ImeAction.Done
                else ImeAction.Next
            )
        }
        if (itemType != ItemType.WANTED_PLATE) {
            EntryFormSection(label = stringResource(R.string.source)) {
                EntryField(
                    label = stringResource(R.string.source_name),
                    value = itemDetails.sourceName ?: "",
                    onValueChange = { onValueChange(itemDetails.copy(sourceName = it)) }
                )
                EntryField(
                    label = stringResource(R.string.source_alias),
                    value = itemDetails.sourceAlias ?: "",
                    onValueChange = { onValueChange(itemDetails.copy(sourceAlias = it)) }
                )
                EntryFormHorizontalSpacer()
                EntryField(
                    label = stringResource(R.string.source_type),
                    value = itemDetails.sourceType ?: "",
                    onValueChange = { onValueChange(itemDetails.copy(sourceType = it)) }
                )
                EntryField(
                    label = stringResource(R.string.source_country),
                    value = itemDetails.sourceCountry ?: "",
                    onValueChange = { onValueChange(itemDetails.copy(sourceCountry = it)) }
                )
                EntryFormHorizontalSpacer()
                EntryField(
                    label = stringResource(R.string.source_details),
                    value = itemDetails.sourceDetails ?: "",
                    onValueChange = { onValueChange(itemDetails.copy(sourceDetails = it)) },
                    hasEntryDialog = true,
                    imeAction = if (itemType != ItemType.FORMER_PLATE) ImeAction.Done
                    else ImeAction.Next
                )
            }
        }
        if (itemType == ItemType.FORMER_PLATE) {
            EntryFormSection(label = stringResource(R.string.archival)) {
                DatePickerField(
                    label = stringResource(R.string.archival_date),
                    dateValue = itemDetails.archivalDate ?: "",
                    onDateSelected = {
                        onValueChange(itemDetails.copy(archivalDate = it))
                    },
                    userCountry = localeCode
                )
                EntryField(
                    label = stringResource(R.string.archival_reason),
                    value = itemDetails.archivalType ?: "",
                    onValueChange = {
                        onValueChange(itemDetails.copy(archivalType = it))
                    }
                )
                EntryField(
                    label = stringResource(R.string.sold_price),
                    placeholder = { Text(getCurrencySymbol(localeCode)) },
                    value = itemDetails.price?.toString() ?: "",
                    onValueChange = { newValue ->
                        onValueChange(itemDetails.copy(
                            price = if (newValue.isBlankOrZero()) null
                            else newValue.toLongOrNull())
                        )
                    },
                    keyboardType = KeyboardType.Number,
                    isCurrency = true,
                    localeCode = localeCode
                )
                EntryFormHorizontalSpacer()
                EntryField(
                    label = stringResource(R.string.recipient_name),
                    value = itemDetails.recipientName ?: "",
                    onValueChange = {
                        onValueChange(itemDetails.copy(recipientName = it))
                    }
                )
                EntryField(
                    label = stringResource(R.string.recipient_alias),
                    value = itemDetails.recipientAlias ?: "",
                    onValueChange = {
                        onValueChange(itemDetails.copy(recipientAlias = it))
                    }
                )
                EntryField(
                    label = stringResource(R.string.recipient_country),
                    value = itemDetails.recipientCountry ?: "",
                    onValueChange = {
                        onValueChange(itemDetails.copy(recipientCountry = it))
                    }
                )
                EntryFormHorizontalSpacer()
                EntryField(
                    label = stringResource(R.string.archival_details),
                    value = itemDetails.archivalDetails ?: "",
                    onValueChange = {
                        onValueChange(itemDetails.copy(archivalDetails = it))
                    },
                    hasEntryDialog = true,
                    imeAction = ImeAction.Done
                )
            }
        }
        Box(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 32.dp)
        ) {
            Button(
                onClick = onSave,
                enabled = isValidEntry,
                modifier = Modifier.fillMaxWidth().height(64.dp)
            ) {
                Icon(
                    painter = saveButtonIcon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(text = saveButtonText)
            }
        }
    }
}

@Composable
private fun EntryFormImage(
    existingImagePath: String?,
    tempImagePath: Uri?,
    onPick: (Uri?) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    pickItemImage(
        existingImagePath = existingImagePath,
        modifier = modifier,
        temporaryImageUri = tempImagePath,
        onPick = onPick,
        onRemove = onRemove
    )
}

@Composable
private fun EntryFormSection(
    label: String,
    isFirst: Boolean = false,
    content: @Composable () -> Unit
) {
    if (!isFirst) HorizontalDivider(modifier = Modifier.padding(16.dp))
    Spacer(modifier = Modifier.height(8.dp))
    Column(
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        EntryFormSectionLabel(label)
        content()
    }
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
private fun EntryFormSectionLabel(label: String, modifier: Modifier = Modifier) {
    Text(
        text = label,
        color = colorScheme.primary,
        style = typography.titleMedium,
        modifier = modifier.padding(8.dp)
    )
}

@Composable
private fun EntryFormHorizontalSpacer(height: Dp = 24.dp) {
    Spacer(modifier = Modifier.height(height))
}

@Composable
private fun EntryFormVerticalSpacer(width: Dp = 12.dp) {
    Spacer(modifier = Modifier.width(width))
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
                if (imeAction == ImeAction.Next) focusManager.moveFocus(FocusDirection.Next)
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
                Log.e("DatePickerField", "Error parsing date: $dateValue", e)
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
    Spacer(modifier = Modifier.height(2.dp))
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
