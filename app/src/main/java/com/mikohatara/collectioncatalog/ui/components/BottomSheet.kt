package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.home.HomeViewModel
import com.mikohatara.collectioncatalog.ui.home.SortBy

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
        Header(stringResource(R.string.sort_by), false)
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Column(
            modifier = Modifier
                .selectableGroup()
                .padding(top = 8.dp, bottom = 16.dp)
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
                                    SortBy.COUNTRY_AND_TYPE_ASC ->
                                        viewModel.setSortBy(SortBy.COUNTRY_AND_TYPE_ASC)
                                    SortBy.COUNTRY_AND_TYPE_DESC ->
                                        viewModel.setSortBy(SortBy.COUNTRY_AND_TYPE_DESC)
                                    SortBy.DATE_NEWEST -> viewModel.setSortBy(SortBy.DATE_NEWEST)
                                    SortBy.DATE_OLDEST -> viewModel.setSortBy(SortBy.DATE_OLDEST)
                                }
                            },
                            role = Role.RadioButton
                        )
                ) {
                    RadioButton(
                        selected = (option == selectedOption),
                        onClick = null,
                        modifier = Modifier
                            .padding(start = 32.dp, top = 12.dp, end = 16.dp, bottom = 12.dp)
                    )
                    Text(text = when (option) {
                        SortBy.COUNTRY_ASC -> stringResource(R.string.sort_by_country_asc)
                        SortBy.COUNTRY_DESC -> stringResource(R.string.sort_by_country_desc)
                        SortBy.COUNTRY_AND_TYPE_ASC ->
                            stringResource(R.string.sort_by_country_and_type_asc)
                        SortBy.COUNTRY_AND_TYPE_DESC ->
                            stringResource(R.string.sort_by_country_and_type_desc)
                        SortBy.DATE_NEWEST -> stringResource(R.string.sort_by_date_newest)
                        SortBy.DATE_OLDEST -> stringResource(R.string.sort_by_date_oldest)
                    })
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
    val filterOptions = listOf("Country, Region", "Type")

    ModalBottomSheet(
        onDismissRequest = { onDismiss() }
    ) {
        Header(stringResource(R.string.filter), false)
        /*Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                stringResource(R.string.filter),
                modifier = Modifier.padding(start = 32.dp, bottom = 16.dp)
            )
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(end = 20.dp)
                    .absoluteOffset(y = (-8).dp)
            ) {
                Text(text = "Reset")
            }
        }*/
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Column {
            filterOptions.forEach { option ->
                Column(
                    modifier = Modifier
                        .clickable { /*TODO*/ }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 16.dp)
                    ) {
                        Text(option)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            painter = painterResource(R.drawable.rounded_chevron_forward),
                            contentDescription = null
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Row(
            //horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            TextButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .weight(0.9f)
            ) {
                Text(text = "Reset")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(text = "Filter")
            }
        }
    }
}

@Composable
private fun Header(label: String, hasBackNavigation: Boolean) {
    if (hasBackNavigation) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null
                )
            }
            Text(
                label,
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, bottom = 16.dp)
            )
        }
    } else {
        Text(
            label,
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 34.dp, bottom = 16.dp)
        )
    }
}
