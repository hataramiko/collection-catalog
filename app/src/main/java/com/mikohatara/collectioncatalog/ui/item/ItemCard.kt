package com.mikohatara.collectioncatalog.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.SampleImage

@Composable
fun ItemCard(
    item: SampleImage,
    //hasImage: Boolean,
    modifier: Modifier = Modifier
) {
    // For testing the if condition below
    val hasImage = true

    var imageWidth: Int

    Card(
        modifier = modifier
            //.height(IntrinsicSize.Min)
    ) {
        if (/*item.imageResourceId*/hasImage != false) {
            imageWidth = defineImageSize(item)

            Image(
                painter = painterResource(item.imageResourceId),
                contentDescription = null,
                modifier = Modifier
                    .width(imageWidth.dp)
                    .height(IntrinsicSize.Min),
                contentScale = ContentScale.FillWidth
            )
        } else {
            Image(
                imageVector = Icons.Rounded.Clear,
                contentDescription = null
            )
            Text(
                text = "No image", //item.title
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
            )
        }
    }
}

private fun defineImageSize(item: SampleImage): Int {
    val sizeToGive: Int

    if (item == SampleImage(R.drawable.j_sa0123_a) ||
        item == SampleImage(R.drawable.j_a1234_a)) {
            sizeToGive = 256
    } else if (item == SampleImage(R.drawable.fin_abc012_a) ||
        item == SampleImage(R.drawable.fin_abc012_b)) {
            sizeToGive = 300
    } else {
        sizeToGive = 400
    }

    return sizeToGive
}

@Preview
@Composable
fun CardPreview() {
    ItemCard(SampleImage(R.drawable.j_sa0123_a))
}