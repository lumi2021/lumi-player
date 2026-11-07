package com.lumi2021.player.core.audio

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayer {
    val currentPosition: StateFlow<Long>
    val duration: StateFlow<Long>
    val isPlaying: StateFlow<Boolean>

    fun load(path: String, stillPlaying: Boolean)
    fun play()
    fun pause()
    fun seekTo(position: Float)
    fun release()
}
