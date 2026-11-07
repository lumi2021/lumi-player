package com.lumi2021.player.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.lumi2021.player.core.audio.Track
import com.lumi2021.player.core.toTime
import com.lumi2021.player.services.LibraryService
import androidx.compose.ui.unit.dp
import com.lumi2021.player.core.decodeImage
import com.lumi2021.player.presentation.components.TrackCover
import com.lumi2021.player.services.AppScreen
import com.lumi2021.player.services.PlayerService
import com.lumi2021.player.services.StateService
import java.io.File

@Composable
fun LibraryScreen(
    stateService: StateService,
    libraryService: LibraryService,
    playerService: PlayerService
)
{
    val tracks by libraryService.tracks.collectAsState()
    val currentTrack by playerService.playing.collectAsState()

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
            Text(
                "Your Library",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(Modifier.height(12.dp))

            TrackList(tracks, currentTrack) {
                playerService.loadTrack(it)
            }
        }
    }
}

@Composable
private fun TrackList(
    tracks: List<Track>,
    currentTrack: Track?,
    onTrackClick: (Track) -> Unit
) {
    val listState = rememberLazyListState()

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = tracks,
                key = { it.filePath }
            ) { track ->
                TrackItem(
                    track = track,
                    isPlaying = track == currentTrack,
                    onClick = { onTrackClick(track) }
                )
            }
        }
    }
}

@Composable
private fun TrackItem(
    track: Track,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (isPlaying) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isPlaying)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                else
                    Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TrackCover(
            cover = track.cover,
            modifier = Modifier.size(60.dp)
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title ?: File(track.filePath).name,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor,
                maxLines = 1
            )

            Text(
                text = track.artist ?: "<Unknown artist>",
                style = MaterialTheme.typography.bodySmall,
                color = if (isPlaying)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        Text(
            text = track.durationMs.toTime(),
            style = MaterialTheme.typography.bodySmall,
            color = contentColor
        )
    }
}


