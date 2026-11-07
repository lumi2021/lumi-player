package com.lumi2021.player

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val audioPlayer = AndroidAudioPlayer(this)
        val audioLibrary = AndroidAudioLibrary(this)

        setContent {

            var hasPermission by remember {
                mutableStateOf(false)
            }

            RequestAudioPermission {
                hasPermission = true
            }

            App(
                audioPlayer = audioPlayer,
                audioLibrary = audioLibrary
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {

    lateinit var audioPlayer: AndroidAudioPlayer
    lateinit var audioLibrary: AndroidAudioLibrary

    App(
        audioPlayer = audioPlayer,
        audioLibrary = audioLibrary
    )
}

@Composable
fun RequestAudioPermission(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current

    val permission = if (
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    ) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onPermissionGranted()
        }
    }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            onPermissionGranted()
        } else {
            launcher.launch(permission)
        }
    }
}
