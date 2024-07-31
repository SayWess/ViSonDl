package com.example.visondl.ui

import com.example.visondl.model.DownloadState
import com.example.visondl.model.VideoQuality

data class ItemsUiState(
    val isDownloadingVideos: Boolean = false,
    val isDownloadingPlaylists: Boolean = false,
    val items: List<ItemUiState> = listOf(),
)


data class ItemUiState(
    val id: String,
    val title: String,
    val isPlaylist: Boolean,
    var downloadPath: String,
    var videoQuality: VideoQuality = VideoQuality.MEDIUM,
    var video: Boolean = false,
    val state: DownloadState,
    val downloadPercent: String?,
    val onCheckedChange: (Boolean) -> Unit,
    val onTitleChange: (String) -> Unit,
    val onDownloadPathChange: (String) -> Unit,
    val onVideoChange: (Boolean) -> Unit,
    val onVideoQualityChange: (VideoQuality) -> Unit,
    val onItemLongClick: () -> Unit
)

