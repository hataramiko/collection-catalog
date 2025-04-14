package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme

@Composable
fun DeletionDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val highlightColor = MaterialTheme.colorScheme.error

    AlertDialog(
        onDismissRequest = { onCancel() },
        icon = { Icon(
            painter = painterResource(R.drawable.rounded_warning),
            contentDescription = null,
            tint = highlightColor,
            modifier = Modifier.size(48.dp)
        ) },
        title = { Text(stringResource(R.string.delete)) },
        text = { Text(stringResource(R.string.deletion_dialog)) },
        dismissButton = {
            TextButton(onClick = { onCancel() }) {
                Text(stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm() },
                colors = ButtonDefaults.buttonColors(highlightColor)
            ) {
                Text(stringResource(R.string.delete))
            }
        }
    )
}

@Composable
fun DiscardDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onCancel() },
        title = { Text(stringResource(R.string.discard_dialog_title)) },
        text = { Text(stringResource(R.string.discard_dialog_text)) },
        dismissButton = {
            TextButton(onClick = { onCancel() }) {
                Text(stringResource(R.string.keep_editing))
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(
                    stringResource(R.string.discard),
                    //color = Color(0xFFF44336)
                )
            }
        }
    )
}

@Composable
fun TransferDialog(
    title: String,
    text: String,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onCancel() },
        title = { Text(title) },
        text = { Text(text) },
        dismissButton = {
            TextButton(onClick = { onCancel() }) {
                Text(stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm() }) {
                Text(confirmButtonText)
            }
        }
    )
}

@Composable
fun RedirectDialog(
    message: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onCancel() },
        text = { Text(text = message) },
        dismissButton = {
            TextButton(onClick = { onCancel() }) {
                Text(stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm() }) {
                Text(stringResource(R.string.continue_button))
            }
        }
    )
}

@Composable
fun ImportDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onHelp: () -> Unit
) {
    val importFirstRowTemplate = "reg_no,country,region_1st,region_2nd,region_3rd," +
        "type,period_start,period_end,year,notes,vehicle,date,cost,value,status," +
        "width,height,weight,color_main,color_secondary," +
        "source_name,source_alias,source_type,source_country,source_details"
    val importFormatExample = "reg_no\ncountry\nregion_1st\nregion_2nd\nregion_3rd\ntype\n" +
        "period_start\nperiod_end\nyear\nnotes\nvehicle\ndate\ncost\nvalue\nstatus\n" +
        "width\nheight\nweight\ncolor_main\ncolor_secondary\nsource_name\nsource_alias\n" +
        "source_type\nsource_country\nsource_details" //TODO move to HELP when the time comes

    AlertDialog(
        onDismissRequest = { onCancel() },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.import_dialog_title))
                /*Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier.size(32.dp).offset(y = (-2).dp)) {
                    IconButton(onClick = { onHelp() }) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_help),
                            contentDescription = null,
                            tint = colorScheme.outline
                        )
                    }
                }*/
            }
        },
        text = {
            Column {
                Text(stringResource(R.string.import_dialog_text))
                Row(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .clickable { onHelp() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_help),
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = stringResource(R.string.import_details),
                        color = colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }

        },
        dismissButton = {
            TextButton(onClick = { onCancel() }) {
                Text(stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm() }) {
                Text(stringResource(R.string.continue_button))
            }
        }
    )
}

@Composable
fun ExportDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onCancel() },
        title = { Text(stringResource(R.string.export_text)) },
        text = { Text(stringResource(R.string.export_dialog_text)) },
        dismissButton = {
            TextButton(onClick = { onCancel() }) {
                Text(stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm() }) {
                Text(stringResource(R.string.continue_button))
            }
        }
    )
}

@Preview
@Composable
fun DeletionDialogPreview() {
    CollectionCatalogTheme {
        DeletionDialog(onConfirm = {}) {
            
        }
    }
}
