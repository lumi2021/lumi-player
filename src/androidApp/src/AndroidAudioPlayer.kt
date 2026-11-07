package com.lumi2021.player

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.lumi2021.player.core.audio.AudioPlayer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import kotlin.time.Duration.Companion.milliseconds

class AndroidAudioPlayer(
    context: Context
) : AudioPlayer {

    private val appContext = context.applicationContext

    private var controller: MediaController? = null

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )

    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    override val duration = _duration.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying = _isPlaying.asStateFlow()

    private val listener = object : Player.Listener {

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }

        override fun onPlaybackStateChanged(state: Int) {
            updateDuration()

            if (state == Player.STATE_ENDED) {
                _currentPosition.value = controller
                    ?.duration
                    ?.coerceAtLeast(0L) ?: 0L
            }
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            updatePosition()
        }
    }

    private val controllerFuture = MediaController.Builder(
        appContext,
        SessionToken(
            appContext,
            ComponentName(appContext, PlaybackService::class.java)
        )
    ).buildAsync()

    init {
        controllerFuture.addListener({
            scope.launch {
                val mediaController = try {
                    controllerFuture.get()
                } catch (e: Exception) {
                    return@launch
                }

                if (!scope.isActive) {
                    mediaController.release()
                    return@launch
                }

                controller = mediaController
                mediaController.addListener(listener)

                updateDuration()
                updatePosition()

                _isPlaying.value = mediaController.isPlaying
            }
        }, MoreExecutors.directExecutor())

        scope.launch {
            while (isActive) {
                updatePosition()
                delay(250.milliseconds)
            }
        }
    }

    private fun updatePosition() {
        _currentPosition.value = controller
            ?.currentPosition
            ?.coerceAtLeast(0L) ?: 0L
    }

    private fun updateDuration() {
        _duration.value = controller
            ?.duration
            ?.coerceAtLeast(0L) ?: 0L
    }

    override fun load(path: String, stillPlaying: Boolean) {
        val uri = when {
            path.startsWith("content://") ||
                    path.startsWith("file://") ||
                    path.startsWith("http://") ||
                    path.startsWith("https://") -> path.toUri()

            else -> Uri.fromFile(File(path))
        }

        controller?.let {
            _currentPosition.value = 0L
            _duration.value = 0L

            it.setMediaItem(MediaItem.fromUri(uri))
            it.prepare()
            if (stillPlaying) it.play()
        }
    }

    override fun play() {
        controller?.play()
    }

    override fun pause() {
        controller?.pause()
    }

    override fun seekTo(position: Float) {
        val normalized = position.coerceIn(0f, 1f)
        val target = (_duration.value * normalized).toLong()

        controller?.seekTo(target)
    }

    override fun release() {
        controller?.let {
            it.removeListener(listener)
            it.release()
        }

        controller = null

        controllerFuture.cancel(false)

        scope.cancel()

        _currentPosition.value = 0L
        _duration.value = 0L
        _isPlaying.value = false
    }
}
