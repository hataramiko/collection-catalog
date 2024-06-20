package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortByBottomSheet(
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = { onDismiss() }) {

    }
}