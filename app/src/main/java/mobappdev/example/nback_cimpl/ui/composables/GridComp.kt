package mobappdev.example.nback_cimpl.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mobappdev.example.nback_cimpl.ui.viewmodels.GameState

@Composable
fun GridComp (eventValue: Int) {
    val grid: List<List<Int>> = listOf(
        listOf(1, 2, 3),
        listOf(4, 5, 6),
        listOf(7, 8, 9)
    )
    Box(
    ) {
        Column(
        ) {
            for ((r, row) in grid.withIndex()) {
                Row(
                    modifier = Modifier.wrapContentSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)

                ) {
                    for (col in grid[r]) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (eventValue == col) Color(0xFF7ab3ef)
                                    else Color(0xFF1e1e1e)
                                ),
                        )
                    }
                }
                if (r < grid.size-1) Spacer(modifier = Modifier.size(8.dp))
            }
        }
    }
}