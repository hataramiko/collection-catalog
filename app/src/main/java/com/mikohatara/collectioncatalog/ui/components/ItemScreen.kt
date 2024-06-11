package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.R

@Composable
fun ItemScreenLabel(label: String) {
    Text(
        label,
        //fontWeight = FontWeight.Bold,
        //textDecoration = TextDecoration.Underline,
        //letterSpacing = 2.sp,
        modifier = ItemScreenModifiers.label
    )
}

@Composable
fun ItemEntryVerticalSpacer() {
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
fun ItemEntryHorizontalSpacer() {
    Spacer(modifier = Modifier.width(8.dp))
}

@Composable
fun ItemSummaryVerticalSpacer(isEnd: Boolean) {
    val value: Int = if(isEnd) {
        8
    } else {
        8 // To be played with
    }

    Spacer(modifier = Modifier.height(value.dp))
}

@Composable
fun IconAbc123() {
    Box(modifier = ItemScreenModifiers.icon) {
        Icon(
            painter = painterResource(R.drawable.rounded_abc),
            contentDescription = null,
            modifier = Modifier.absoluteOffset(y = (-5).dp)
        )
        Icon(
            painter = painterResource(R.drawable.rounded_123),
            contentDescription = null,
            modifier = Modifier.absoluteOffset(y = 6.dp)
        )
    }
}

data object ItemScreenModifiers {
    val card: Modifier = Modifier.padding(16.dp)
    val label: Modifier = Modifier.padding(start = 76.dp, top = 24.dp, bottom = 0.dp)
    val icon: Modifier = Modifier.padding(20.dp)
    // Entry
    val column: Modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
    val rowWithIcon: Modifier = Modifier.padding(top = 8.dp, end = 16.dp)
    val rowNoIcon: Modifier = Modifier.padding(start = 64.dp , top = 8.dp, end = 16.dp)
    // Summary
    val row: Modifier = Modifier.padding(end = 16.dp, top = 8.dp, bottom = 8.dp)
}
