package com.example.visondl.model

import com.example.visondl.MUSICS_FOLDER_PATH

data class Item(
    val url : String,
    val id : String,
    var title : String,
    var state : DownloadState = DownloadState.TODOWNLOAD,
    var downloadPath : String = MUSICS_FOLDER_PATH,
    var videoQuality : VideoQuality = VideoQuality.MEDIUM,
    var video : Boolean = false,
    var isPlaylist : Boolean = false,
    var downloadPercent : String = ""
)

enum class DownloadState {
    ERROR, TODOWNLOAD, DOWNLOADABLE, DOWNLOADING, DOWNLOADED;
}

enum class VideoQuality {
    LOW, MEDIUM, HIGH, ULTRAHIGH
}

