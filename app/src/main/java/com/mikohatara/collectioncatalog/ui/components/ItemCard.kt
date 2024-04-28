package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.sample.SampleImage
import com.mikohatara.collectioncatalog.data.samplePlates

@Composable
fun ItemCard(
    item: Plate,
    //hasImage: Boolean,
    modifier: Modifier = Modifier
) {
    // For testing the if condition below
    val hasImage = false
    var imageWidth: Int

    Card(
        modifier = modifier
            //.height(IntrinsicSize.Min)
    ) {
        if (/*item.imageResourceId*/hasImage != false) {
            //imageWidth = defineImageSize(item)

            /*Image(
                painter = painterResource(item.imageResourceId),
                contentDescription = null,
                modifier = Modifier
                    //.width(imageWidth.dp)
                    .height(IntrinsicSize.Min),
                contentScale = ContentScale.FillWidth
            )*/
            Text(
                text = "This is an image",
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
            ) {
                Image(
                    imageVector = Icons.Rounded.Clear,
                    contentDescription = null
                )
                Text(
                    text = item.uniqueDetails.number,
                    modifier = Modifier
                        .height(64.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}























private fun defineImageSize(item: SampleImage): Int {
    val sizeToGive: Int

    if (item == SampleImage(R.drawable.j_sa0123_a) ||
        item == SampleImage(R.drawable.j_a1234_a)
    ) {
            sizeToGive = 256
    } else if (item == SampleImage(R.drawable.fin_abc012_a) ||
        item == SampleImage(R.drawable.fin_abc012_b)
    ) {
            sizeToGive = 300
    } else {
        sizeToGive = 400
    }

    return sizeToGive
}

@Preview
@Composable
fun CardPreview() {
    //ItemCard(SampleImage(R.drawable.j_sa0123_a)) //SampleImage
    ItemCard(item = samplePlates[0]) //Plate
}