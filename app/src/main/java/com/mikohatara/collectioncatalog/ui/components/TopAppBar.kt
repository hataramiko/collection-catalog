package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import com.mikohatara.collectioncatalog.data.Plate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopAppBar(
    title: String,
    onAddItem: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = {
            Text(
                title, // database tableName?
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                //modifier = Modifier.padding(16.dp)
            )
        },
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = "Menu"
                )
            }
        },
        actions = {
            IconButton(onClick = { onAddItem() }) {
                Icon(
                    imageVector = Icons.Rounded.AddCircle,
                    contentDescription = "Add"
                )
            }/*
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = null
                )
            }*/
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreenTopAppBar(
    title: String,
    item: Plate,
    onBack: () -> Unit,
    onEdit: (Plate) -> Unit,
    onDelete: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { onBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = { onEdit(item) }) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit"
                )
            }
            IconButton(onClick = { onDelete() }) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete"
                )
            }/*
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = null
                )
            }*/
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEntryTopAppBar(
    title: String,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { onBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },/*
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = null
                )
            }
        }*/
    )
}
