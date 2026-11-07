package com.lumi2021.player

class JVMPlatform: Platform {
    override val kind = PlatformKind.DESKTOP
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()
