package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Item

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogTopAppBar(
    title: String,
    onOpenDrawer: () -> Unit,
    onToggleSearch: (() -> Unit)? = null,
    onImport: (() -> Unit)? = null,
    onExport: (() -> Unit)? = null,
    itemListSize: Int = 0,
    isSearchActive: Boolean = false,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    val onToggleMenu = { isMenuExpanded = !isMenuExpanded }
    val onDismissMenu = { isMenuExpanded = false }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    if (isSearchActive) {
        TopAppBar(
            title = {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text(stringResource(R.string.search_placeholder)) },
                    singleLine = true,
                    trailingIcon = { if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    painter = painterResource(R.drawable.rounded_close_24),
                                    contentDescription = null
                                )
                            }
                        }},
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        onToggleSearch.let { it?.invoke() }
                        focusManager.clearFocus()
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_arrow_back_24),
                        contentDescription = null
                    )
                }
            },
            colors = CustomTopAppBarColors(),
            scrollBehavior = scrollBehavior
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    } else {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            navigationIcon = {
                IconButton(onClick = onOpenDrawer) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_menu_24),
                        contentDescription = null
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = onToggleSearch ?: {},
                    enabled = onToggleSearch != null
                ) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_search_24),
                        contentDescription = null
                    )
                }
                IconButton(
                    onClick = onToggleMenu,
                    enabled = onExport != null || onImport != null
                ) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_more_vert_24),
                        contentDescription = null
                    )
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = onDismissMenu,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        // Example of an item which shouldn't be visible if the action is null
                        /*onImport?.let {
                            ModifiedDropdownMenuItem(
                                onClick = {
                                    it()
                                    isMenuExpanded = false
                                },
                                painterResource = painterResource(R.drawable.rounded_download_24),
                                text = stringResource(R.string.import_text)
                            )
                        }*/
                        ModifiedDropdownMenuItem(
                            onClick = {
                                onImport?.let { it() }
                                onDismissMenu()
                            },
                            painterResource = painterResource(R.drawable.rounded_download_24),
                            text = stringResource(R.string.import_text),
                            enabled = onImport != null
                        )
                        ModifiedDropdownMenuItem(
                            onClick = {
                                onExport?.let { it() }
                                onDismissMenu()
                            },
                            painterResource = painterResource(R.drawable.rounded_file_export_24),
                            text = stringResource(R.string.export_text),
                            enabled = onExport != null && itemListSize > 0
                        )
                    }
                }
            },
            colors = CustomTopAppBarColors(),
            scrollBehavior = scrollBehavior
        )
    }
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
    onCopy: (() -> Unit)? = null,
    onCheckWishlist: (() -> Unit)? = null,
    onTransfer: (() -> Unit)? = null,
    transferButtonText: String = "",
    transferButtonPainter: Painter = painterResource(R.drawable.rounded_question_mark)
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    val onToggleMenu = { isMenuExpanded = !isMenuExpanded }
    val onDismissMenu = { isMenuExpanded = false }

    LargeTopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.rounded_arrow_back_24),
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
            IconButton(onClick = onToggleMenu) {
                Icon(
                    painter = painterResource(R.drawable.rounded_more_vert_24),
                    contentDescription = null
                )
            }
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = onDismissMenu,
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
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                onCheckWishlist?.let {
                    ModifiedDropdownMenuItem(
                        onClick = {
                            it()
                            onDismissMenu()
                        },
                        painterResource = painterResource(R.drawable.rounded_check_24),
                        text = stringResource(R.string.check_wishlist_button),
                    )
                }
                onTransfer?.let {
                    ModifiedDropdownMenuItem(
                        onClick = {
                            it()
                            onDismissMenu()
                        },
                        painterResource = transferButtonPainter,
                        text = transferButtonText,
                    )
                }
                ModifiedDropdownMenuItem(
                    onClick = {
                        onDelete()
                        onDismissMenu()
                    },
                    painterResource = painterResource(R.drawable.rounded_delete_forever),
                    text = stringResource(R.string.delete)
                )
            }
        },
        colors = colors
            .copy(navigationIconContentColor = CustomTopAppBarColors().navigationIconContentColor),
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
    onSave: () -> Unit,
    saveIcon: Painter,
    isSaveEnabled: Boolean = true,
    onCopy: (() -> Unit)? = null,
    onPaste: (() -> Unit)? = null,
    isPasteEnabled: Boolean = false
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    val onToggleMenu = { isMenuExpanded = !isMenuExpanded }
    val onDismissMenu = { isMenuExpanded = false }

    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.rounded_arrow_back_24),
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            FilledIconButton(
                onClick = onSave,
                enabled = isSaveEnabled
            ) {
                Icon(
                    painter = saveIcon,
                    contentDescription = null
                )
            }
            IconButton(onClick = onToggleMenu) {
                Icon(
                    painter = painterResource(R.drawable.rounded_more_vert_24),
                    contentDescription = null
                )
            }
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = onDismissMenu,
                shape = RoundedCornerShape(12.dp)
            ) {
                ModifiedDropdownMenuItem(
                    onClick = {
                        onCopy?.let { it() }
                        onDismissMenu()
                    },
                    painterResource = painterResource(R.drawable.rounded_content_copy),
                    text = stringResource(R.string.copy),
                    enabled = onCopy != null
                )
                ModifiedDropdownMenuItem(
                    onClick = {
                        onPaste?.let { it() }
                        onDismissMenu()
                    },
                    painterResource = painterResource(R.drawable.rounded_content_paste),
                    text = stringResource(R.string.paste),
                    enabled = onPaste != null && isPasteEnabled
                )
            }
        },
        colors = colors
            .copy(navigationIconContentColor = CustomTopAppBarColors().navigationIconContentColor),
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
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.rounded_arrow_back_24),
                    contentDescription = "Back"
                )
            }
        },
        colors = colors
            .copy(navigationIconContentColor = CustomTopAppBarColors().navigationIconContentColor)
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
    val onToggleMenu = { isMenuExpanded = !isMenuExpanded }
    val onDismissMenu = { isMenuExpanded = false }

    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.rounded_arrow_back_24),
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = onToggleMenu) {
                Icon(
                    painter = painterResource(R.drawable.rounded_more_vert_24),
                    contentDescription = null
                )
            }
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = onDismissMenu,
                shape = RoundedCornerShape(12.dp)
            ) {
                ModifiedDropdownMenuItem(
                    onClick = {
                        onDelete()
                        onDismissMenu()
                    },
                    painterResource = painterResource(R.drawable.rounded_delete_forever),
                    text = stringResource(R.string.delete)
                )
            }
        },
        colors = colors
            .copy(navigationIconContentColor = CustomTopAppBarColors().navigationIconContentColor)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsTopAppBar(
    onOpenDrawer: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        title = { Text(text = stringResource(R.string.statistics)) },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    painter = painterResource(R.drawable.rounded_menu_24),
                    contentDescription = null
                )
            }
        },
        colors = CustomTopAppBarColors(),
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
        title = { Text(text = stringResource(R.string.settings)) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.rounded_arrow_back_24),
                    contentDescription = "Back"
                )
            }
        },
        colors = CustomTopAppBarColors(),
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpTopAppBar(
    title: String,
    onBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.rounded_arrow_back_24),
                    contentDescription = null
                )
            }
        },
        colors = CustomTopAppBarColors(),
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
                modifier = Modifier.padding(end = 8.dp)
            )
        },
        onClick = onClick,
        enabled = enabled
    )
}

@Composable
private fun CustomTopAppBarColors(): TopAppBarColors = TopAppBarDefaults.topAppBarColors(
    navigationIconContentColor = colorScheme.onSurfaceVariant
)
