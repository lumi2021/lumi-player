import com.lumi2021.player.core.AudioLibrary
import com.lumi2021.player.core.audio.Track
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File

class DesktopAudioLibrary : AudioLibrary {

    override suspend fun loadTracks(directory: File): List<Track> {
        val supportedExtensions = setOf(
            "mp3", "wav", "flac", "ogg", "m4a", "aac"
        )

        return directory.walkTopDown()
            .filter { it.isFile }
            .filter {
                it.extension.lowercase() in supportedExtensions
            }
            .mapNotNull { file ->
                try {
                    val audioFile = AudioFileIO.read(file)
                    val tag = audioFile.tag

                    val cover = tag?.firstArtwork?.binaryData

                    Track(
                        title = tag?.getFirst(FieldKey.TITLE)?.takeIf { it.isNotBlank() },
                        artist = tag?.getFirst(FieldKey.ARTIST)?.takeIf { it.isNotBlank() },
                        album = tag?.getFirst(FieldKey.ALBUM)?.takeIf { it.isNotBlank() },
                        durationMs = audioFile.audioHeader.trackLength.toLong() * 1000,
                        filePath = file.absolutePath,
                        cover = cover
                    )
                } catch (e: Exception) {
                    println("Failed to read ${file.name}: ${e.message}")
                    null
                }
            }
            .toList()
    }
}
