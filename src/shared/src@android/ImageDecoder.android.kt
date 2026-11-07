package com.lumi2021.player.core

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun decodeImage(bytes: ByteArray): ImageBitmap? {
    return runCatching {
        BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size
        )?.asImageBitmap()
    }.getOrNull()
}
