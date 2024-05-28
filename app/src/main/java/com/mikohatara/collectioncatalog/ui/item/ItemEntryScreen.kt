package com.mikohatara.collectioncatalog.ui.item

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ItemEntryScreen(
    viewModel: ItemEntryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState = viewModel.uiState

    ItemEntryScreenContent(
        number = uiState.item?.uniqueDetails?.number,
        variant = uiState.item?.uniqueDetails?.variant,
    )

    Log.d("ItemEntry", "item: " + uiState.item.toString())
    Log.d("ItemEntry", uiState.itemDetails.toString())
    Log.d("ItemEntry", "isNew: " + uiState.isNew.toString())
}

@Composable
fun ItemEntryScreenContent(number: String?, variant: String?) {
    if (number != null || variant != null) {
        Text(
            "$number, $variant",
            modifier = Modifier.padding(64.dp)
        )
    } else {
        Text(
            "Blank",
            modifier = Modifier.padding(64.dp)
        )
    }
}