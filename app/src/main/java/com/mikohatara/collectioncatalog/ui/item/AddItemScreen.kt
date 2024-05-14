package com.mikohatara.collectioncatalog.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.collectioncatalog.data.samplePlates
import com.mikohatara.collectioncatalog.ui.components.AddItemScreenTopAppBar
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme

@Composable
fun AddItemScreen(
    viewModel: AddItemViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val uiState: AddItemUiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddItemScreenContent(uiState, onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreenContent(
    uiState: AddItemUiState,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = { AddItemScreenTopAppBar(onBack) },
        content = { innerPadding ->
            InputForm(
                uiState,
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
}

@Composable
private fun InputForm(
    uiState: AddItemUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = uiState.country,
            onValueChange = { },
            //keyboardActions = ,
            label = { Text("Country") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.type,
            onValueChange = { },
            //keyboardActions = ,
            label = { Text("Type") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.number,
            onValueChange = { },
            //keyboardActions = ,
            label = { Text("Number") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.variant,
            onValueChange = { },
            //keyboardActions = ,
            label = { Text("Variant") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        Button(
            onClick = {  },
            //enabled = uiState.hasValidEntry,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Add ")
            Text(text = "ABC-012")
        }
    }
}

@Composable
private fun AddItemTextField() {
    OutlinedTextField(
        value = samplePlates[0].commonDetails.country,
        onValueChange = { },
        //keyboardActions = ,
        label = null,//{ Text() },
        modifier = Modifier.fillMaxWidth(),
        enabled = true,
        singleLine = true
    )
}

@Preview
@Composable
fun Preview() {
    CollectionCatalogTheme {
        //InputForm()
    }
}