package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.theme.RekkaryTheme

@Composable
fun ExpandableCard(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val onClick = remember { Modifier.clickable { isExpanded = !isExpanded } }

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.animateContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .then(onClick)
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Icon(
                painter = if (isExpanded) painterResource(R.drawable.rounded_keyboard_arrow_up_24)
                else painterResource(R.drawable.rounded_keyboard_arrow_down_24),
                contentDescription = null,
                modifier = Modifier.padding(16.dp)
            )
        }
        if (isExpanded) {
            content()
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
    if (isExpanded) { // Extra space after the card if it's expanded
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun ExpandableStatsCard(
    label: String,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RekkaryTheme.shapes.card8,
    content: @Composable () -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val onClick = remember { Modifier.clickable { isExpanded = !isExpanded } }
    val containerColor by animateColorAsState(
        targetValue = if (isExpanded) {
            colorScheme.surfaceContainerHigh
        } else {
            colorScheme.surfaceContainerLow
        },
        label = "ExpandableStatsCardContainerColor"
    )

    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = modifier.animateContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .then(onClick)
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Icon(
                painter = if (isExpanded) painterResource(R.drawable.rounded_keyboard_arrow_up_24)
                else painterResource(R.drawable.rounded_keyboard_arrow_down_24),
                contentDescription = null,
                tint = colorScheme.outline,
                modifier = Modifier.padding(16.dp)
            )
        }
        if (isExpanded) {
            content()
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
