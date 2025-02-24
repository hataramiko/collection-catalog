package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.collection.isCollectionColor
import com.mikohatara.collectioncatalog.ui.collection.toColor

@Composable
fun SettingsDialog(
    label: String,
    options: List<String> = emptyList(),
    selectedOption: String,
    onToggleSelection: (String) -> Unit,
    onDismiss: () -> Unit,
    infoText: String? = null
) {
    val showInfo = rememberSaveable { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerHigh),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(64.dp)
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (infoText != null) {
                    IconButton(onClick = { showInfo.value = true }) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_info),
                            contentDescription = null,
                            tint = colorScheme.outline,
                        )
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            Column(
                modifier = Modifier
                    .heightIn(max = 480.dp)
                    .wrapContentHeight()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 8.dp)
                ) {
                    items(options) { item ->
                        SettingsRadioButton(
                            value = item,
                            selectedOption = selectedOption,
                            onClick = { onToggleSelection(item) },
                            color = if (item.isCollectionColor()) item.toColor() else null
                        )
                    }
                }
            }
            SingleButtonFooter(onClick = onDismiss)
        }
    }

    if (showInfo.value) {
        Dialog(
            onDismissRequest = { showInfo.value = false }
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults
                    .cardColors(containerColor = colorScheme.surfaceContainerHighest),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = infoText ?: "",
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Row {
                        Spacer(modifier = Modifier.weight(2f))
                        TextButton(
                            onClick = { showInfo.value = false },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp)
                        ) {
                            Text(stringResource(R.string.ok_text))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsRadioButton(
    value: String,
    selectedOption: String,
    onClick: (String) -> Unit,
    color: Color? = null
) {
    val isSelected = value == selectedOption
    val colors = if (color != null) {
        RadioButtonDefaults.colors(color, color, color, color)
    } else {
        RadioButtonDefaults.colors()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = { onClick(value) },
                role = Role.RadioButton
            )
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = colors,
            modifier = Modifier
                .padding(start = 24.dp, top = 16.dp, end = 20.dp, bottom = 16.dp)
        )
        Text(
            text = value,
            modifier = Modifier.padding(end = 24.dp)
        )
    }
}

@Composable
private fun SettingsRadioButtonWithLabel(
    label: String,
    value: String? = null
) {
    val valueColor = if (value != null) colorScheme.onSurface else colorScheme.outlineVariant

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = false,//(option == uiState.sortBy),
                onClick = { /*viewModel.setSortBy(option)*/ },
                role = Role.RadioButton
            )
    ) {
        RadioButton(
            selected = false,//(option == uiState.sortBy),
            onClick = null,
            modifier = Modifier
                .padding(start = 24.dp, top = 16.dp, end = 20.dp, bottom = 16.dp)
        )
        Column {
            Text(
                text = label,
                color = colorScheme.secondary,
                fontSize = 10.sp
            )
            value?.let {
                Text(
                    text = value,
                    color = valueColor,
                    modifier = Modifier.offset(y = (-6).dp)
                )
            }
        }
    }
}

@Composable
private fun ButtonsFooter(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    HorizontalDivider(modifier = Modifier.fillMaxWidth())
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        TextButton(
            onClick = onCancel,
            modifier = Modifier.weight(0.9f)
        ) {
            Text(stringResource(R.string.cancel))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onConfirm,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.copy))
        }
    }
}

@Composable
private fun SingleButtonFooter(
    onClick: () -> Unit
) {
    HorizontalDivider(modifier = Modifier.fillMaxWidth())
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Spacer(modifier = Modifier.weight(1.5f))
        Button(
            onClick = onClick,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.ok_text))
        }
    }
}
