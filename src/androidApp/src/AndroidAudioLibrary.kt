
package com.lumi2021.player

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.lumi2021.player.core.AudioLibrary
import com.lumi2021.player.core.audio.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.os.Build
import android.util.Size
import java.io.File

class AndroidAudioLibrary(
    private val context: Context
) : AudioLibrary {

    override suspend fun loadTracks(directory: File): List<Track> = withContext(Dispatchers.IO) {

        val tracks = mutableListOf<Track>()
        val coverCache = mutableMapOf<Long, ByteArray?>()

        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
        )

        context.contentResolver.query(
            collection,
            projection,
            "${MediaStore.Audio.Media.IS_MUSIC} != 0",
            null,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )?.use { cursor ->

            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {

                val id = cursor.getLong(idColumn)
                val albumId = cursor.getLong(albumIdColumn)

                val uri = ContentUris.withAppendedId(collection, id)
                val albumUri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId)

                val cover = coverCache.getOrPut(albumId) {
                    loadAlbumCover(albumId)
                }

                tracks.add(
                    Track(
                        title = cursor.getString(titleColumn),
                        artist = cursor.getString(artistColumn),
                        album = cursor.getString(albumColumn),
                        durationMs = cursor.getLong(durationColumn),
                        filePath = uri.toString(),
                        cover = cover
                    )
                )
            }
        }

        tracks
    }

    private fun loadAlbumCover(albumId: Long): ByteArray? {
        if (albumId <= 0) return null

        val albumUri = ContentUris.withAppendedId(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            albumId
        )

        return runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver
                    .loadThumbnail(albumUri, Size(512, 512), null)
                    .let { bitmap ->
                        val output = java.io.ByteArrayOutputStream()
                        bitmap.compress(
                            android.graphics.Bitmap.CompressFormat.JPEG,
                            90,
                            output
                        )
                        bitmap.recycle()
                        output.toByteArray()
                    }
            } else {
                context.contentResolver
                    .openInputStream(albumUri)
                    ?.use { it.readBytes() }
            }
        }.getOrNull()
    }
}
