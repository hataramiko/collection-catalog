package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.CollectionColor

@Composable
fun EmptyList(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.rounded_indeterminate),
    message: String = stringResource(R.string.empty_list_default_msg),
    description: String? = null,
    collectionEmoji: String? = null,
    collectionColor: CollectionColor = CollectionColor.DEFAULT,
    button: @Composable (() -> Unit)? = null
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .height((screenHeight / 1.6).dp)
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {
        if (collectionEmoji != null) {
            Text(
                text = collectionEmoji,
                fontSize = 64.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        } else if (collectionColor != CollectionColor.DEFAULT) {
            IconCollectionLabel(
                color = collectionColor.color,
                modifier = Modifier.size(96.dp)
            )
        } else {
            Icon(
                painter = painter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline.copy(0.66f),
                modifier = iconModifier.size(96.dp)
            )
        }
        Text(
            text = message,
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp, top = 32.dp)
        )
        description?.let {
            Text(
                text = it,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.outline,
            )
        }
        button?.let {
            Spacer(modifier = Modifier.height(32.dp))
            it()
        }
    }
}


