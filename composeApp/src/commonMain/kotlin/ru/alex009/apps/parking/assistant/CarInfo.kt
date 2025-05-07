package ru.alex009.apps.parking.assistant

data class CarInfo(
    val width: Meter,
    val length: Meter,
    val wheelBase: Meter,
    val frontOverhang: Meter,
    val turningRadius: Meter,
    /** максимальный угол поворота руля от центра */
    val maxSteeringAngle: Float,
)
