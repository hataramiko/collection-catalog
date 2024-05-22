package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.data.Plate

@Composable
fun ItemCard(
    item: Plate,
    onClick: () -> Unit
) {
    ItemCard(
        image = true /*item.uniqueDetails.imagePath*/,
        width = 520.0 /*item.measurements.width*/,
        title = item.uniqueDetails.number,
        onClick = onClick
    )
}

@Composable
private fun ItemCard(
    image: Boolean, // Change this to the image path etc. when possible
    width: Double,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            //.height(IntrinsicSize.Min)
    ) {
        if (image != false /* image != null */ ) {

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
                text = title,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(width.dp)
                    .height((width/5).dp)
                    .padding((width/13).dp)
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                Image(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                )
                Text(
                    text = title,
                    modifier = Modifier
                )
            }
        }
    }
}

/*
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
}*/

@Preview
@Composable
fun CardPreview() {
    //ItemCard(SampleImage(R.drawable.j_sa0123_a)) //SampleImage
    //ItemCard(item = samplePlates[0], onClick = /*TODO*/) //Plate
}