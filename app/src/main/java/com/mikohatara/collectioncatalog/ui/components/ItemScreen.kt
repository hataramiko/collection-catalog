package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ItemEntryVerticalSpacer() {
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
fun IconBlank() {
    Box(modifier = ItemScreenModifiers.icon.width(24.dp))
}

data object ItemScreenModifiers {
    val card: Modifier = Modifier.padding(16.dp)
    val label: Modifier = Modifier.padding(start = 76.dp, top = 24.dp, bottom = 0.dp)
    val icon: Modifier = Modifier.padding(20.dp)
    // Entry
    val column: Modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
    // Summary
    val row: Modifier = Modifier.padding(end = 16.dp, top = 8.dp, bottom = 8.dp)
}
