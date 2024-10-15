package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.ItemDetails
import com.mikohatara.collectioncatalog.util.getItemDetailsCheckboxList

@Composable
fun CopyItemDetailsDialog(
    itemDetails: ItemDetails,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val checkboxList = getItemDetailsCheckboxList(itemDetails)

    Dialog(onDismissRequest = onCancel) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerHigh),
            modifier = Modifier
                .fillMaxWidth()
                .height(538.dp)
        ) {
            Column(
                modifier = Modifier.height(96.dp)
            ) {

            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            //.then(onClickOption)
                            .clickable {  }
                    ) {
                        Checkbox(
                            checked = false,
                            onCheckedChange = null,
                            modifier = Modifier
                                .padding(start = 24.dp, top = 16.dp, bottom = 16.dp, end = 20.dp)
                        )
                        Text(stringResource(R.string.select_all))
                    }
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                    )
                }
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            //.then(onClickOption)
                            .clickable {  }
                    ) {
                        Checkbox(
                            checked = false,
                            onCheckedChange = null,
                            modifier = Modifier
                                .padding(start = 24.dp, top = 16.dp, bottom = 16.dp, end = 20.dp)
                        )
                        Text(stringResource(R.string.include_image))
                    }
                }
                items(checkboxList) { item ->
                    ItemDetailsCheckbox(
                        label = item.label,
                        value = item.value.takeIf { !it.isNullOrBlank() }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
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
    }
}

@Composable
private fun ItemDetailsCheckbox(
    label: String,
    value: String? = null
) {
    val valueColor = if (value != null) colorScheme.secondary else colorScheme.outlineVariant

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            //.then(onClickOption)
            .clickable {  }
    ) {
        Checkbox(
            checked = false,
            onCheckedChange = null,
            modifier = Modifier
                .padding(start = 24.dp, top = 16.dp, bottom = 16.dp, end = 20.dp)
        )
        Column {
            Text(
                text = label,
                fontSize = 10.sp
            )
            Text(
                text = value ?: stringResource(R.string.copy_dialog_empty_value),
                color = valueColor,
                modifier = Modifier.offset(y = (-6).dp)
            )
        }
    }
}
