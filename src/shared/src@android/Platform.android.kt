package com.lumi2021.player

import android.os.Build
class AndroidPlatform : Platform {
    override val kind = PlatformKind.ANDROID
    override val name = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()
