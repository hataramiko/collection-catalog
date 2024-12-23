package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableCard(
    label: String,
    data: List<Any?> = emptyList(),
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val onClick = remember { Modifier.clickable { isExpanded = !isExpanded } }

    //if (data.any { it != null }) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .padding(/*horizontal = 8.dp, vertical = if (isExpanded) 16.dp else */8.dp)
                .animateContentSize()
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
                    imageVector = if (isExpanded) Icons.Rounded.KeyboardArrowUp
                    else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp)
                )
            }
            if (isExpanded) {
                Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                    content()
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
        }
   // }
}
