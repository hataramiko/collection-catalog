package com.mikohatara.collectioncatalog.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mikohatara.collectioncatalog.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopRow(
    isHidden: Boolean,
    isAtTop: Boolean,
    onSortByClick: () -> Unit,
    onFilterClick: () -> Unit,
    filterCount: Int
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val backgroundColor by animateColorAsState(
        targetValue = if (isAtTop) TopAppBarDefaults.topAppBarColors().containerColor else
            TopAppBarDefaults.topAppBarColors().scrolledContainerColor,
        animationSpec = tween(250),
        label = "TopRowBackgroundColor"
    )
    /*  The backgroundColor seems to be in sync _well enough_ with that of the TopAppBar,
    *   but it might be worth exploring the possibility of getting the color directly off
    *   of the TopAppBar.
    * */

    /*  Wrapping the AnimatedVisibility block inside a Box seems to prevent crashes when
    *   rapidly scrolling to the very top.
    *   If issues persist, consider adding...
    *       if (isHidden) Spacer(modifier = Modifier.height(1.dp))
    *   ...within the Box scope, for further enforcement.
    * */
    Box {
        AnimatedVisibility(
            visible = !isHidden,
            enter = slideInVertically(initialOffsetY = { -it * 2 }),
            exit = slideOutVertically(targetOffsetY = { -it * 2 })
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 26.dp,
                    bottomEnd = 26.dp
                ),
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .requiredWidth(screenWidth.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onSortByClick() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_swap_vert),
                            contentDescription = null,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            stringResource(R.string.sort_by),
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    BadgedBox(
                        modifier = Modifier.weight(1f),
                        badge = {
                            if (filterCount > 0) {
                                Box(
                                    contentAlignment = Alignment.TopEnd,
                                    modifier = Modifier.fillMaxWidth().padding(end = 16.dp)
                                ) {
                                    Badge {
                                        Text(filterCount.toString())
                                    }
                                }
                            }
                        }
                    ) {
                        OutlinedButton(
                            onClick = { onFilterClick() },
                            modifier = Modifier
                                .fillMaxWidth()
                            //.weight(1f)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.rounded_filter),
                                contentDescription = null,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text(
                                stringResource(R.string.filter),
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
