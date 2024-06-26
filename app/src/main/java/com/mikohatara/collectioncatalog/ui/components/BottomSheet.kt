package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.home.HomeViewModel
import com.mikohatara.collectioncatalog.ui.home.SortBy
import com.mikohatara.collectioncatalog.ui.theme.CollectionCatalogTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortByBottomSheet(
    onDismiss: () -> Unit,
    viewModel: HomeViewModel
) {
    val sortByOptions = viewModel.sortByOptions
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(sortByOptions[0]) }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() }
    ) {
        Text(
            stringResource(R.string.sort_by),
            modifier = Modifier.padding(start = 32.dp, bottom = 16.dp)
        )
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Column(
            modifier = Modifier
                .selectableGroup()
                .padding(top = 10.dp, bottom = 20.dp)
        ) {
            sortByOptions.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (option == selectedOption),
                            onClick = {
                                onOptionSelected(option)
                                when (option) {
                                    SortBy.COUNTRY_ASC -> viewModel.setSortBy(SortBy.COUNTRY_ASC)
                                    SortBy.COUNTRY_DESC -> viewModel.setSortBy(SortBy.COUNTRY_DESC)
                                    SortBy.DATE_ASC -> viewModel.setSortBy(SortBy.DATE_ASC)
                                    SortBy.DATE_DESC -> viewModel.setSortBy(SortBy.DATE_DESC)
                                }
                            },
                            role = Role.RadioButton
                        )
                ) {
                    RadioButton(
                        selected = (option == selectedOption),
                        onClick = null,
                        modifier = Modifier
                            .padding(start = 28.dp, top = 12.dp, bottom = 12.dp)
                    )
                    Text(
                        text = option.toString(),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                stringResource(R.string.filter),
                modifier = Modifier.padding(start = 32.dp, bottom = 16.dp)
            )
            Button(
                onClick = { /*TODO*/ }
            ) {
                Text(text = "Reset")
            }
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
    }
}
