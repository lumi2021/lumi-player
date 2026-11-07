package com.lumi2021.player

enum class PlatformKind {
    ANDROID,
    DESKTOP
}

interface Platform {
    val kind: PlatformKind
    val name: String
}

expect fun getPlatform(): Platform
