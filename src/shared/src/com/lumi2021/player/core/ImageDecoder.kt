package com.lumi2021.player.core

import androidx.compose.ui.graphics.ImageBitmap

expect fun decodeImage(bytes: ByteArray): ImageBitmap?