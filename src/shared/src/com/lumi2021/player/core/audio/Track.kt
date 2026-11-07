package com.lumi2021.player.core.audio

data class Track(
    val title: String?,
    val artist: String?,
    val album: String?,
    val durationMs: Long,
    val filePath: String,
        val cover: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Track
        return filePath == other.filePath
    }

    override fun hashCode(): Int {
        var result = durationMs.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + filePath.hashCode()
        return result
    }
}
