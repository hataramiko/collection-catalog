package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Item

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    title: String,
    onOpenDrawer: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = {
            Text(
                title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(
                onClick = {  },
                enabled = false
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemSummaryTopAppBar(
    title: String,
    item: Item,
    colors: TopAppBarColors,
    scrollBehavior: TopAppBarScrollBehavior,
    onBack: () -> Unit,
    onEdit: (Item) -> Unit,
    onDelete: () -> Unit,
    onCopy: (() -> Unit)? = null
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    MediumTopAppBar(
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
                    painter = painterResource(R.drawable.rounded_edit),
                    contentDescription = "Edit"
                )
            }
            IconButton(onClick = { isMenuExpanded = !isMenuExpanded }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = null
                )
            }
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false },
                shape = RoundedCornerShape(12.dp)
            ) {
                ModifiedDropdownMenuItem(
                    onClick = {
                        onCopy?.let { it() }
                        isMenuExpanded = false
                    },
                    painterResource = painterResource(R.drawable.rounded_content_copy),
                    text = stringResource(R.string.copy),
                    enabled = onCopy != null
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                ModifiedDropdownMenuItem(
                    onClick = {
                        onDelete()
                        isMenuExpanded = false
                    },
                    painterResource = painterResource(R.drawable.rounded_delete_forever),
                    text = stringResource(R.string.delete)
                )
            }
        },
        colors = colors,
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEntryTopAppBar(
    title: String,
    colors: TopAppBarColors,
    scrollBehavior: TopAppBarScrollBehavior,
    onBack: () -> Unit,
    onCopy: (() -> Unit)? = null,
    onPaste: (() -> Unit)? = null
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

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
            IconButton(onClick = { isMenuExpanded = !isMenuExpanded }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = null
                )
            }
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false },
                shape = RoundedCornerShape(12.dp)
            ) {
                ModifiedDropdownMenuItem(
                    onClick = {
                        onCopy?.let { it() }
                        isMenuExpanded = false
                    },
                    painterResource = painterResource(R.drawable.rounded_content_copy),
                    text = stringResource(R.string.copy),
                    enabled = onCopy != null
                )
                ModifiedDropdownMenuItem(
                    onClick = {
                        onPaste?.let { it() }
                        isMenuExpanded = false
                    },
                    painterResource = painterResource(R.drawable.rounded_content_paste),
                    text = stringResource(R.string.paste),
                    enabled = onPaste != null
                )
            }
        },
        colors = colors,
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionListTopAppBar(
    title: String,
    onBack: () -> Unit,
    colors: TopAppBarColors
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
        colors = colors
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionEntryTopAppBar(
    title: String,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    colors: TopAppBarColors
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

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
            IconButton(onClick = { isMenuExpanded = !isMenuExpanded }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = null
                )
            }
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false },
                shape = RoundedCornerShape(12.dp)
            ) {
                ModifiedDropdownMenuItem(
                    onClick = {
                        onDelete()
                        isMenuExpanded = false
                    },
                    painterResource = painterResource(R.drawable.rounded_delete_forever),
                    text = stringResource(R.string.delete)
                )
            }
        },
        colors = colors
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsTopAppBar(
    onOpenDrawer: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        title = { Text(stringResource(R.string.statistics)) },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopAppBar(
    onBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        title = { Text(stringResource(R.string.settings)) },
        navigationIcon = {
            IconButton(onClick = { onBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun ModifiedDropdownMenuItem(
    onClick: () -> Unit,
    painterResource: Painter,
    text: String,
    enabled: Boolean = true
) {
    DropdownMenuItem(
        leadingIcon = {
            Icon(
                painter = painterResource,
                contentDescription = null,
                modifier = Modifier.padding(start = 4.dp)
            )
        },
        text = {
            Text(
                text = text,
                modifier = Modifier.padding(end = 1.dp)
            )
        },
        onClick = onClick,
        enabled = enabled
    )
}
