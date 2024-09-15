package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.R

@Composable
fun Loading() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()
            .width(IntrinsicSize.Max)
            .padding(top = 256.dp)
    ) {
        var textWidth by remember { mutableIntStateOf(0) }

        Text(
            text = stringResource(R.string.loading),
            modifier = Modifier
                .offset(x = 4.dp)
                .onGloballyPositioned { textWidth = it.size.width }
        )
        LinearProgressIndicator(
            modifier = Modifier
                .padding(top = 4.dp)
                .width((textWidth / 2).dp)
        )
    }
}
