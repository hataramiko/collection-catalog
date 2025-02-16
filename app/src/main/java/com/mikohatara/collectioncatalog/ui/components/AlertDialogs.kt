package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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

@Preview
@Composable
fun DeletionDialogPreview() {
    CollectionCatalogTheme {
        DeletionDialog(onConfirm = {}) {
            
        }
    }
}
