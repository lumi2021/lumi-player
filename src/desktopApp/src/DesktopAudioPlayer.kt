package com.lumi2021.player

import com.lumi2021.player.core.audio.AudioPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import kotlin.time.Duration.Companion.milliseconds

class DesktopAudioPlayer : AudioPlayer {

    private var factory: MediaPlayerFactory? = null
    private var player: MediaPlayer? = null

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()

    private var progressJob: Job? = null

    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    override val duration = _duration.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying = _isPlaying.asStateFlow()

    override fun load(path: String, stillPlaying: Boolean) {
        scope.launch {
            mutex.withLock {
                releaseLocked()

                val newFactory = MediaPlayerFactory()
                val newPlayer = newFactory.mediaPlayers().newMediaPlayer()

                factory = newFactory
                player = newPlayer

                newPlayer.events().addMediaPlayerEventListener(
                    object : MediaPlayerEventAdapter() {
                        override fun mediaPlayerReady(mediaPlayer: MediaPlayer) {
                            _duration.value = mediaPlayer.status().length()
                            _currentPosition.value = 0L
                        }

                        override fun playing(mediaPlayer: MediaPlayer) {
                            _isPlaying.value = true
                        }

                        override fun paused(mediaPlayer: MediaPlayer) {
                            _isPlaying.value = false
                        }

                        override fun stopped(mediaPlayer: MediaPlayer) {
                            _isPlaying.value = false
                        }
                    }
                )

                newPlayer.media().prepare(path)

                progressJob = scope.launch {
                    while (isActive) {
                        mutex.withLock {
                            if (player === newPlayer) {
                                _currentPosition.value =
                                    newPlayer.status().time()
                            }
                        }

                        delay(250.milliseconds)
                    }
                }

                if (stillPlaying) newPlayer.controls().play()
            }
        }
    }

    override fun play() {
        scope.launch {
            mutex.withLock {
                player?.controls()?.play()
            }
        }
    }

    override fun pause() {
        scope.launch {
            mutex.withLock {
                player?.controls()?.pause()
            }
        }
    }

    override fun seekTo(position: Float) {
        scope.launch {
            mutex.withLock {
                val realPosition = (_duration.value * position).toLong()
                player?.controls()?.setTime(realPosition)
            }
        }
    }

    private suspend fun releaseLocked() {
        progressJob?.cancel()
        progressJob?.join()
        progressJob = null

        player?.let {
            it.controls().stop()
            it.release()
        }

        player = null

        factory?.release()
        factory = null

        _isPlaying.value = false
        _currentPosition.value = 0L
        _duration.value = 0L
    }

    override fun release() {
        scope.launch {
            mutex.withLock {
                releaseLocked()
            }
        }
    }
}