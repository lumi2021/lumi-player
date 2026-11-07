package com.lumi2021.player.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lumi2021.player.presentation.components.TrackCover
import com.lumi2021.player.services.PlayerService

@Composable
fun MiniPlayer(
    playerService: PlayerService,
    modifier: Modifier = Modifier,
    onExpand: () -> Unit
) {
    val track by playerService.playing.collectAsState()
    val isPlaying by playerService.isPlaying.collectAsState()
    val progress by playerService.progress.collectAsState()

    val duration = track?.durationMs ?: 0L

    val normalizedProgress = if (duration > 0) {
        (progress.toFloat() / duration).coerceIn(0f, 1f)
    } else {
        0f
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp)),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 3.dp
    ) {
        Column {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Album artwork
                TrackCover(
                    modifier = Modifier.size(46.dp),
                    cover = track?.cover
                )

                // Track information
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(onClick = onExpand)
                        .padding(start = 12.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = track?.title ?: "Not playing",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = track?.artist ?: "Select a track",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Playback button
                IconButton(
                    onClick = { playerService.togglePlayback() },
                    enabled = track != null
                ) {
                    Icon(
                        imageVector = if (isPlaying) {
                            Icons.Default.Pause
                        } else {
                            Icons.Default.PlayArrow
                        },
                        contentDescription = if (isPlaying) {
                            "Pause"
                        } else {
                            "Play"
                        }
                    )
                }
            }

            // Progress indicator
            LinearProgressIndicator(
                progress = { normalizedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            )
        }
    }
}