package com.lumi2021.player.services

import com.lumi2021.player.core.audio.AudioPlayer
import com.lumi2021.player.core.audio.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.concurrent.Executors

data class PlayerState(
    val title: String = "Midnight Memories",
    val artist: String = "The Midnight · Endless Summer",
)

class PlayerService(
    audioPlayer: AudioPlayer,
    libraryService: LibraryService
) {
    val audioPlayer = audioPlayer
    val libraryService = libraryService
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    var i = 0;
    private val _playing = MutableStateFlow<Track?>(null)
    val playing = _playing.asStateFlow()

    val isPlaying = combine(playing, audioPlayer.isPlaying) { track, isAudioPlaying ->
            track != null && isAudioPlaying
        }.stateIn(scope, SharingStarted.WhileSubscribed(5000), false)
    val progress = audioPlayer.currentPosition

    private val _volume = MutableStateFlow(0f)
    val volume = _volume.asStateFlow()

    fun loadTrack(track: Track) {
        if (playing == track) return
        audioPlayer.load(track.filePath, audioPlayer.isPlaying.value)
        _playing.value = track
    }

    fun togglePlayback() {
        if (audioPlayer.isPlaying.value)
            audioPlayer.pause()
        else
            audioPlayer.play()
    }

    fun seek(progress: Float) {
        audioPlayer.seekTo(progress)
    }

    fun previous() {
        i = Math.floorMod(i - 1, libraryService.tracks.value.size)
        loadTrack(libraryService.tracks.value[i])
    }

    fun next() {
        i = Math.floorMod(i + 1, libraryService.tracks.value.size)
        loadTrack(libraryService.tracks.value[i])
    }

    fun setVolume(value: Float) {
        _volume.value = value.coerceIn(0f, 1f)
    }
}
