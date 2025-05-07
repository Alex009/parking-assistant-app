package ru.alex009.apps.parking.assistant

import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.DrawableResource

@Immutable
data class SceneCar(
    val image: DrawableResource,
    val position: CarPosition,
)
