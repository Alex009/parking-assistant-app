package ru.alex009.apps.parking.assistant

import androidx.compose.runtime.Immutable

@Immutable
data class Scene(
    val cars: List<SceneCar> = emptyList(),
    val drawings: List<SceneDrawing> = emptyList(),
    val initialPosition: CarPosition = CarPosition(),
)
