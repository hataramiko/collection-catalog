package com.mikohatara.collectioncatalog.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.SampleImage

@Composable
fun ItemCard(
    item: SampleImage,
    modifier: Modifier = Modifier
) {
    // For testing the if condition below
    val hasImage = false

    Card(modifier = modifier) {
        if (/*item.imageResourceId*/hasImage != false) {
            Image(
                painter = painterResource(item.imageResourceId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight(),
                contentScale = ContentScale.FillWidth
            )
        } else {
            Text(
                text = "No image",
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun CardPreview() {
    ItemCard(SampleImage(R.drawable.j_sa0123_a))
}