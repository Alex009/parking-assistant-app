package ru.alex009.apps.parking.assistant

import androidx.compose.runtime.Immutable

@Immutable
data class CarPosition(
    val rotationAngle: Float = 0f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
)
