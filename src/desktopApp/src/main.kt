package com.lumi2021.player

import DesktopAudioLibrary
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lumi2021.player.presentation.PlayerScreen

fun main() {
    val audioLibrary = DesktopAudioLibrary()
    val audioPlayer = DesktopAudioPlayer()

    application {
        Window(onCloseRequest = ::exitApplication, title = "LumiPlayer") {
            App(
                audioPlayer = audioPlayer,
                audioLibrary = audioLibrary
            )
        }
    }
}
