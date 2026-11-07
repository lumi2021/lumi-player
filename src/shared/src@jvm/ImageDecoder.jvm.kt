package com.lumi2021.player.core

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

actual fun decodeImage(bytes: ByteArray): ImageBitmap? {
    return runCatching {
        Image.makeFromEncoded(bytes).toComposeImageBitmap()
    }.getOrNull()
}
