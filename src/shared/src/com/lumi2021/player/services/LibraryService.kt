package com.lumi2021.player.services

import com.lumi2021.player.core.AudioLibrary
import com.lumi2021.player.core.audio.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class LibraryService(
    audioLibrary: AudioLibrary
) {
    val audioLibrary = audioLibrary

    private var _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks = _tracks.asStateFlow()

    suspend fun loadAudioFiles(directory: String) {
        _tracks.value = audioLibrary.loadTracks(File(directory))
    }

}
