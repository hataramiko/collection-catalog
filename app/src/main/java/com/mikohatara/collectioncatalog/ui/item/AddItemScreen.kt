package com.mikohatara.collectioncatalog.ui.item

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.mikohatara.collectioncatalog.ui.components.AddItemScreenTopAppBar
import com.mikohatara.collectioncatalog.ui.components.ItemImage
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme
import kotlinx.coroutines.launch

@Composable
fun AddItemScreen(
    viewModel: AddItemViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState = viewModel.uiState

    AddItemScreenContent(
        uiState,
        viewModel,
        onValueChange = viewModel::updateUiState,
        onBack
    )
}

@Composable
fun AddItemScreenContent(
    uiState: AddItemUiState,
    viewModel: AddItemViewModel,
    onValueChange: (NewItemDetails) -> Unit,
    onBack: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { AddItemScreenTopAppBar(onBack) },
        content = { innerPadding ->
            InputForm(
                uiState,
                //newItemDetails = uiState.newItemDetails,
                modifier = Modifier.padding(innerPadding),
                onValueChange,
                onAdd = {
                    coroutineScope.launch {
                        viewModel.addItem()
                        onBack()
                    }
                }
            )
        }
    )
}

@Composable
private fun Image(imageUri: Uri?) {

    if (imageUri != null) {
        /*val painter = rememberAsyncImagePainter(
            ImageRequest
                .Builder(LocalContext.current)
                .data(data = imageUri)
                .build()
        )*/

        AsyncImage(
            model = imageUri,
            contentDescription = null,
        )
    } else {
        androidx.compose.foundation.Image(
            imageVector = Icons.Rounded.Clear,
            contentDescription = null,
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .height(128.dp)
        )
    }
}

@Composable
private fun InputForm(
    uiState: AddItemUiState,
    //newItemDetails: NewItemDetails,
    modifier: Modifier = Modifier,
    onValueChange: (NewItemDetails) -> Unit = {},
    onAdd: () -> Unit
) {
    Column(
        modifier = modifier
            //.padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val imagePath: String? = ItemImage(uiState)
        //Log.d("imagePath in AddItemScreen", imagePath.toString())

        val updateUiState: (NewItemDetails) -> Unit = {
            onValueChange(uiState.newItemDetails.copy(imagePath = imagePath))
        }

        updateUiState.invoke(uiState.newItemDetails)

        Log.d("imagePath in uiState", uiState.newItemDetails.imagePath.toString())
        
        OutlinedTextField(
            value = uiState.newItemDetails.country,
            onValueChange = { onValueChange(uiState.newItemDetails.copy(country = it)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            label = { Text("Country") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.newItemDetails.region ?: "",
            onValueChange = { onValueChange(uiState.newItemDetails.copy(region = it)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            label = { Text("Region") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.newItemDetails.area ?: "",
            onValueChange = { onValueChange(uiState.newItemDetails.copy(area = it)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            label = { Text("Area") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.newItemDetails.type,
            onValueChange = { onValueChange(uiState.newItemDetails.copy(type = it)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            label = { Text("Type") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.newItemDetails.number,
            onValueChange = { onValueChange(uiState.newItemDetails.copy(number = it)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.Next
            ),
            label = { Text("Number") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.newItemDetails.variant,
            onValueChange = { onValueChange(uiState.newItemDetails.copy(variant = it)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done //Next
            ),
            label = { Text("Variant") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.newItemDetails.imagePath.toString(),
            onValueChange = { onValueChange(uiState.newItemDetails.copy(imagePath = it)) },
            //keyboardOptions = ,
            label = { Text("imagePath debug") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            singleLine = true
        )
        /*OutlinedTextField(
            value = uiState.newItemDetails.width.toString(),
            onValueChange = {
                if (!it.isDigitsOnly()) {
                    onValueChange(uiState.newItemDetails.copy(width = 0.0))
                } else {
                    onValueChange(uiState.newItemDetails.copy(width = it.toDouble()))
                }
            },
            //onValueChange = { onValueChange(uiState.newItemDetails.copy(width = it.toDouble())) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                //imeAction = ImeAction.Done
            ),
            label = { Text("Width") },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )*/
        Button(
            onClick = onAdd,
            //enabled = uiState.hasValidEntry,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Add ${uiState.newItemDetails.number}")
        }
    }
}

/*
@Composable
private fun TextField(
    value: String,
    onValueChange: (NewItemDetails) -> Unit = {},
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(uiState.newItemDetails.copy()) },
        //keyboardActions = ,
        label = label,
        modifier = Modifier.fillMaxWidth(),
        enabled = true,
        singleLine = true
    )
}*/

@Preview
@Composable
fun Preview() {
    CollectionCatalogTheme {
        //InputForm()
    }
}