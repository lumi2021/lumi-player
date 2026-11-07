package com.lumi2021.player

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.dp
import com.lumi2021.player.core.AudioLibrary
import com.lumi2021.player.core.audio.AudioPlayer
import com.lumi2021.player.presentation.*
import com.lumi2021.player.presentation.components.MiniPlayer
import com.lumi2021.player.services.AppScreen
import com.lumi2021.player.services.PlayerService
import com.lumi2021.player.services.LibraryService
import com.lumi2021.player.services.StateService


@Composable
fun App(
    audioPlayer: AudioPlayer,
    audioLibrary: AudioLibrary
) {
    val stateService = remember { StateService() }
    val libraryService = remember { LibraryService(audioLibrary) }
    val playerService = remember { PlayerService(audioPlayer, libraryService) }

    LaunchedEffect(libraryService) {
        libraryService.loadAudioFiles(
            when (getPlatform().kind) {
                PlatformKind.DESKTOP -> "/home/leo/Músicas/"
                PlatformKind.ANDROID -> "/storage/emulated/0/Music/Songs"
            }
        )
    }

    val screens: Map<AppScreen, @Composable () -> Unit> = mapOf(
        AppScreen.Library to { LibraryScreen(stateService, libraryService, playerService) },
        AppScreen.Feed to { FeedScreen() },
        AppScreen.Player to { PlayerScreen(playerService) }
    )

    MaterialTheme {
        AppTheme {

            Surface(
                color = MaterialTheme.colorScheme.background
            ) {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val isWide = maxWidth >= 700.dp && maxWidth / maxHeight >= 1.2f
                    val minLimit = 0.20f
                    val maxLimit = 0.35f

                    var leftRatio by remember { mutableFloatStateOf(0.25f) }
                    var rightRatio by remember { mutableFloatStateOf(0.25f) }

                    val focusedScreen by stateService.focusedScreen.collectAsState()

                    if (isWide) {
                        val totalWidth = maxWidth.value

                        val leftWidth = (maxWidth * leftRatio)
                        val rightWidth = (maxWidth * rightRatio)

                        Row(Modifier.fillMaxSize()) {
                            Box(Modifier.width(leftWidth).fillMaxHeight()) {
                                screens[AppScreen.Library]?.invoke()
                            }

                            ResizeHandle { delta -> leftRatio = (leftRatio + delta / totalWidth).coerceIn(minLimit, maxLimit) }

                            Box(Modifier.weight(1f).fillMaxHeight()) {
                                screens[AppScreen.Feed]?.invoke()
                            }

                            ResizeHandle { delta -> rightRatio = (rightRatio - delta / totalWidth).coerceIn(minLimit, maxLimit) }

                            Box(Modifier.width(rightWidth).fillMaxHeight()) {
                                screens[AppScreen.Player]?.invoke()
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                screens[focusedScreen]?.invoke()

                                if (focusedScreen != AppScreen.Player) {
                                    MiniPlayer(
                                        playerService = playerService,
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) { stateService.focusOn(AppScreen.Player) }
                                }
                            }

                            NavigationBar {
                                NavigationBarItem(
                                    selected = focusedScreen == AppScreen.Library,
                                    onClick = { stateService.focusOn(AppScreen.Library) },
                                    icon = {
                                        Icon(
                                            Icons.Default.LibraryMusic,
                                            contentDescription = "Library"
                                        )
                                    },
                                    label = { Text("Library") }
                                )

                                NavigationBarItem(
                                    selected = focusedScreen == AppScreen.Feed,
                                    onClick = { stateService.focusOn(AppScreen.Feed) },
                                    icon = {
                                        Icon(
                                            Icons.Default.Home,
                                            contentDescription = "Feed"
                                        )
                                    },
                                    label = { Text("Feed") }
                                )

                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResizeHandle(
    onDrag: (Float) -> Unit
) {
    Box(
        modifier = Modifier
            .width(8.dp)
            .fillMaxHeight()
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    onDrag(change.position.x / density)
                }
            }
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .width(2.dp)
                .height(32.dp)
                .background(MaterialTheme.colorScheme.outline)
        )
    }
}
