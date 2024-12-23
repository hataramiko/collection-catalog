package com.mikohatara.collectioncatalog.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.settings.SettingsUiState
import com.mikohatara.collectioncatalog.ui.settings.SettingsViewModel
import com.mikohatara.collectioncatalog.util.toCountryCode
import com.mikohatara.collectioncatalog.util.toDisplayCountry

@Composable
fun SettingsDialog(
    uiState: SettingsUiState,
    viewModel: SettingsViewModel,
    label: String,
    options: List<String> = emptyList(),
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    infoText: String? = null
) {
    Dialog(onDismissRequest = onCancel) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerHigh),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 555.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.height(64.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 4.dp)
                )
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(options) { item ->
                    SettingsRadioButton(
                        value = item,
                        selectedOption = uiState.userCountry.toDisplayCountry(),
                        onClick = { viewModel.setUserCountry(item) }
                    )
                    Log.d("country", uiState.userCountry)
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            if (infoText != null) {
                InfoFooter(infoText)
            }
            /*HorizontalDivider(modifier = Modifier.fillMaxWidth())
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
                }*/
        }
    }
}

@Composable
private fun SettingsRadioButton(
    value: String,
    selectedOption: String,
    onClick: (String) -> Unit
) {
    val isSelected = value == selectedOption

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
            modifier = Modifier
                .padding(start = 24.dp, top = 16.dp, end = 20.dp, bottom = 16.dp)
        )
        Text(value)
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
private fun InfoFooter(
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Row {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                painter = painterResource(R.drawable.rounded_info),
                contentDescription = null,
                modifier = Modifier
                    .padding(12.dp)
                    .scale(0.75f)
            )
            Text(
                text = text,
                modifier = Modifier.padding(
                    start = 2.dp,
                    top = 12.dp,
                    bottom = 12.dp,
                    end = 16.dp
                )
            )
        }
    }
}
