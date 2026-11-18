package com.example.pawconnect.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Constraints
import kotlin.math.roundToInt

@Composable
fun CollapsingHeaderLayout(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    // Full height of the header, and how far it is currently pushed up (0 = fully shown)
    var headerHeightPx by remember { mutableFloatStateOf(0f) }
    var headerOffsetPx by remember { mutableFloatStateOf(0f) }

    val scrollConnection = remember {
        object : NestedScrollConnection {

            // Finger moving up (available.y < 0): hide the header before the list scrolls
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y >= 0f) return Offset.Zero
                val previous = headerOffsetPx
                headerOffsetPx = (previous + available.y).coerceIn(-headerHeightPx, 0f)
                return Offset(0f, headerOffsetPx - previous)
            }

            // Finger moving down (available.y > 0): show the header only after the list reached its top
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (available.y <= 0f) return Offset.Zero
                val previous = headerOffsetPx
                headerOffsetPx = (previous + available.y).coerceIn(-headerHeightPx, 0f)
                return Offset(0f, headerOffsetPx - previous)
            }
        }
    }

    Column(modifier = modifier.nestedScroll(scrollConnection)) {
        // Header: measured at full height, but only the visible part takes up space
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clipToBounds()
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(
                        constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity)
                    )
                    val offset = headerOffsetPx.roundToInt().coerceIn(-placeable.height, 0)
                    layout(placeable.width, placeable.height + offset) {
                        placeable.place(0, offset)
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { size ->
                        headerHeightPx = size.height.toFloat()
                        headerOffsetPx = headerOffsetPx.coerceIn(-headerHeightPx, 0f)
                    }
            ) {
                header()
            }
        }

        // Content fills the rest of the space
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            content()
        }
    }
}
