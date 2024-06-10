package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
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
fun ItemScreenColumnSpacer() {
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
fun ItemScreenRowSpacer() {
    Spacer(modifier = Modifier.width(8.dp))
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
    val column: Modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
    val rowWithIcon: Modifier = Modifier.padding(top = 8.dp, end = 16.dp)
    val rowNoIcon: Modifier = Modifier.padding(start = 64.dp , top = 8.dp, end = 16.dp)

    val label: Modifier = Modifier.padding(start = 76.dp, top = 24.dp, bottom = 0.dp)
    val icon: Modifier = Modifier.padding(20.dp)

    val rowOld: Modifier = Modifier.padding(vertical = 18.dp)
    val columnOld: Modifier = Modifier
        .fillMaxWidth()
        .padding(end = 16.dp)
}
