package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.ui.theme.RekkaryTheme

@Composable
fun CardGroup(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RekkaryTheme.shapes.card20,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.clip(shape = shape)
    ) {
        content()
    }
}

@Composable
fun CardGroupSpacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(4.dp))
}
