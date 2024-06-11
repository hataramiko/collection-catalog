package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme

@Composable
fun DeletionDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        icon = { Icon(
            painter = painterResource(R.drawable.rounded_warning),
            contentDescription = null
        ) },
        title = { Text(stringResource(R.string.delete)) },
        text = { Text(stringResource(R.string.deletion_dialog)) },
        dismissButton = {
            TextButton(onClick = { onCancel() }) {
                Text(stringResource(R.string.no))
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm() }) {
                Text(stringResource(R.string.yes))
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
