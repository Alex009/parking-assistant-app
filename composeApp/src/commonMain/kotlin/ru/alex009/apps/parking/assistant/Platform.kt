package ru.alex009.apps.parking.assistant

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform