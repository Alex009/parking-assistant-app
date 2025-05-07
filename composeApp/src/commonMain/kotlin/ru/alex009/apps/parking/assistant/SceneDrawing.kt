package ru.alex009.apps.parking.assistant

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.toOffset

@Immutable
sealed interface SceneDrawing {

    @Immutable
    data class Line(
        val start: IntOffset,
        val end: IntOffset,
        val width: Float
    ) : SceneDrawing {
        override fun draw(scope: DrawScope, centerX: Float, centerY: Float) = with(scope) {
            val centerOffset = Offset(centerX, centerY)
            drawLine(
                color = Color.Companion.Black,
                start = start.toOffset().plus(centerOffset),
                end = end.toOffset().plus(centerOffset),
                strokeWidth = width
            )
        }
    }

    fun draw(scope: DrawScope, centerX: Float, centerY: Float)
}

