package com.lumi2021.player.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumi2021.player.core.toTime
import com.lumi2021.player.presentation.components.TrackCover
import com.lumi2021.player.services.PlayerService

@Composable
fun PlayerScreen(
        service: PlayerService,
) {

    val track by service.playing.collectAsState()
    val isPlaying by service.isPlaying.collectAsState()
    val progress by service.progress.collectAsState()

    var sliderPosition by remember(track) { mutableFloatStateOf(0f) }
    var isDragging by remember(track) { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Album cover
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val imageSize = (maxHeight * 0.5f).coerceAtMost(maxWidth)

                TrackCover(
                    modifier = Modifier.size(imageSize),
                    cover = track?.cover
                )
            }

            Spacer(Modifier.height(28.dp))

            // Track information
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    track?.title ?: "Not playing yet",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    track?.artist ?: "What will be your next vibe?",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(Modifier.height(24.dp))

            // Progress
            LaunchedEffect(progress, track?.durationMs) {
                val duration = track?.durationMs ?: 0L
                if (!isDragging && duration > 0) sliderPosition = (progress.toFloat() / duration).coerceIn(0f, 1f)
            }

            Slider(
                value = sliderPosition,
                valueRange = 0f..1f,

                onValueChange = {
                    isDragging = true
                    sliderPosition = it
                },
                onValueChangeFinished = {
                    isDragging = false
                    service.seek(sliderPosition)
                },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(progress.toTime(), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text((track?.durationMs ?: 0L).toTime(), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(20.dp))

            // Playback controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { service.previous() }) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                FilledIconButton(
                    onClick = { service.togglePlayback() },
                    modifier = Modifier.size(64.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                ) {
                    Icon(
                        if (isPlaying)
                            Icons.Default.Pause
                        else
                            Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(onClick = { service.next() }) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}
