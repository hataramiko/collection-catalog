package com.mikohatara.collectioncatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogNavGraph
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CollectionCatalogTheme {
                CollectionCatalogApp()
            }
        }
    }
}
