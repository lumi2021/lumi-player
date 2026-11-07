package com.lumi2021.player.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lumi2021.player.core.decodeImage

@Composable
fun TrackCover(
    cover: ByteArray?,
    modifier: Modifier = Modifier,
) {
    val image = remember(cover) {
        cover?.let(::decodeImage)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.linearGradient(
                listOf(
                    Color(MaterialTheme.colorScheme.primary.value),
                    Color(MaterialTheme.colorScheme.secondary.value),
                    Color(MaterialTheme.colorScheme.tertiary.value)
                )
            )),
        contentAlignment = Alignment.Center
    ) {
        if (image != null) {
            Image(
                bitmap = image,
                contentDescription = "Album cover",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}