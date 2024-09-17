package com.mikohatara.collectioncatalog

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.mikohatara.collectioncatalog.data.CollectionRepository
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogNavGraph

@Composable
fun CollectionCatalogApp(collectionRepository: CollectionRepository) {
    Surface {
        CollectionCatalogNavGraph(collectionRepository)
    }
}
