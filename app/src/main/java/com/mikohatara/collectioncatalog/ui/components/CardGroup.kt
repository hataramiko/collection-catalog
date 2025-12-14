package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.ui.theme.RekkaryTheme

@Composable
fun CardGroup(
    modifier: Modifier = Modifier,
    label: String? = null,
    shape: RoundedCornerShape = RekkaryTheme.shapes.card20,
    content: @Composable () -> Unit
) {
    label?.let {
        Text(
            text = label,
            color = colorScheme.primary,
            style = typography.titleSmall,
            modifier = Modifier.padding(12.dp)
        )
    }
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
