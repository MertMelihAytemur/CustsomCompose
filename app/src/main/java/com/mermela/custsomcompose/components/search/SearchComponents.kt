package com.mermela.custsomcompose.components.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MorphingSearch(
    modifier: Modifier = Modifier,
    hint: String = "Search",
    onSearch: (String) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    BoxWithConstraints(modifier) {
        val collapsedHeight = 56.dp
        val slot = 56.dp
        val spacing = 8.dp
        val expandedWidth = maxWidth

        val transition = updateTransition(expanded, label = "searchTransition")

        val width by transition.animateDp(label = "width") {
            if (it) expandedWidth else collapsedHeight
        }
        val corner by transition.animateDp(label = "corner") {
            if (it) 20.dp else collapsedHeight / 2
        }

        val targetIsExpanded = transition.targetState
        val settled by remember(transition) {
            derivedStateOf { transition.currentState == transition.targetState }
        }
        val contentMounted = targetIsExpanded && settled
        val startPadding = slot + spacing
        val endPadding = if (targetIsExpanded) slot + 4.dp else 12.dp

        Surface(
            modifier = Modifier
                .width(width)
                .height(collapsedHeight)
                .clip(RoundedCornerShape(corner))
                .clickable { if (!expanded) expanded = true },
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = startPadding, end = endPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedVisibility(
                        visible = contentMounted,
                        enter = fadeIn(),
                        exit = ExitTransition.None
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            BasicTextField(
                                value = query,
                                onValueChange = {
                                    query = it
                                    onSearch(it)
                                },
                                modifier = Modifier
                                    .focusRequester(focusRequester)
                                    .weight(1f),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                decorationBox = { inner ->
                                    Box(Modifier.fillMaxWidth()) {
                                        if (query.isEmpty()) {
                                            Text(
                                                text = hint,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                            )
                                        }
                                        inner()
                                    }
                                }
                            )
                        }
                    }
                }
                if (contentMounted) {
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 4.dp),
                        onClick = {
                            if (query.isNotEmpty()) {
                                query = ""
                            } else {
                                expanded = false
                                keyboard?.hide()
                            }
                        }
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Clear/Collapse")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(slot)
                        .align(Alignment.CenterStart),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            }
        }
        LaunchedEffect(contentMounted) {
            if (contentMounted) {
                focusRequester.requestFocus()
                keyboard?.show()
            }
        }
    }
}