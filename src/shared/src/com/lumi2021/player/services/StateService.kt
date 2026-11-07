package com.lumi2021.player.services

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppScreen {
    Library,
    Feed,
    Player
}

class StateService {
    private var _focusedScreen = MutableStateFlow(AppScreen.Feed)
    val focusedScreen = _focusedScreen.asStateFlow()

    fun focusOn(screen: AppScreen) {
        _focusedScreen.value = screen
    }
}
