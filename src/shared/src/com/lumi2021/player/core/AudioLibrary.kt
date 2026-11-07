package com.lumi2021.player.core

import com.lumi2021.player.core.audio.Track
import java.io.File

interface AudioLibrary {
    suspend fun loadTracks(directory: File): List<Track>
}
