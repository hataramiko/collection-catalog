package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mikohatara.collectioncatalog.R

@Composable
fun MassChangeDialog(
    targetFieldList: List<String>,
    selectedTargetField: String,
    oldValue: String,
    newValue: String,
    onTargetFieldChange: (String) -> Unit,
    onOldValueChange: (String) -> Unit,
    onNewValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isOldValueEnabled: Boolean,
    isNewValueEnabled: Boolean,
    isConfirmEnabled: Boolean,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.mass_change)
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerHigh),
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = label,
                    color = colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                DropdownMenuField(
                    label = stringResource(R.string.mass_change_field),
                    values = targetFieldList,
                    selectedValue = selectedTargetField,
                    onValueChange = onTargetFieldChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    label = { Text(stringResource(R.string.mass_change_old_value)) },
                    value = oldValue,
                    onValueChange = { onOldValueChange(it) },
                    enabled = isOldValueEnabled,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    maxLines = 1,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    label = { Text(stringResource(R.string.mass_change_new_value)) },
                    value = newValue,
                    onValueChange = { onNewValueChange(it) },
                    enabled = isNewValueEnabled,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    maxLines = 1,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = stringResource(R.string.cancel),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onConfirm,
                        enabled = isConfirmEnabled
                    ) {
                        Text(
                            text = stringResource(R.string.replace),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        }
    }
}
