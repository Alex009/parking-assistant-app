package ru.alex009.apps.parking.assistant

import kotlin.jvm.JvmInline

@JvmInline
value class Meter(val value: Float) {
    val pixels: Float
        get() {
            // 4.7 метра = 487px
            return value * (487 / 4.7f)
        }
}

val Float.meters get() = Meter(this)
val Float.centimeters get() = Meter(this / 100)
val Float.millimeters get() = Meter(this / (100 * 10))
