package com.example.visondl.data

import com.example.visondl.model.DownloadState
import com.example.visondl.model.Item
import com.example.visondl.model.VideoQuality
import com.example.visondl.ui.ItemUiState

val dataSet = mutableListOf(
    Item("test","1","test1", DownloadState.DOWNLOADABLE,"test3", VideoQuality.LOW, false),
    Item("test","2","test2", DownloadState.TODOWNLOAD,"test3", VideoQuality.LOW, false),
    Item("test","3","test3", DownloadState.DOWNLOADING,"test3", VideoQuality.LOW, false),
    Item("test","4","test4", DownloadState.DOWNLOADED,"test3", VideoQuality.LOW, false),
    Item("test","5","test5", DownloadState.ERROR,"test3", VideoQuality.LOW, false)
)

val testItemsUiState = listOf(
    ItemUiState("default", "toDownload", false, "default", VideoQuality.LOW, false, DownloadState.TODOWNLOAD, "0", {}, {}, {}, {}, {}, {}),
    ItemUiState("default1", "toDownload", false, "default", VideoQuality.LOW, false, DownloadState.TODOWNLOAD, "0", {}, {}, {}, {}, {}, {}),
    ItemUiState("default2", "toDownload", false, "default", VideoQuality.LOW, false, DownloadState.TODOWNLOAD, "0", {}, {}, {}, {}, {}, {}),
    ItemUiState("default3", "toDownload", false, "default", VideoQuality.LOW, false, DownloadState.TODOWNLOAD, "0", {}, {}, {}, {}, {}, {}),
    ItemUiState("default4", "toDownload", false, "default", VideoQuality.LOW, false, DownloadState.TODOWNLOAD, "0", {}, {}, {}, {}, {}, {}),
    ItemUiState("default5", "toDownload", false, "default", VideoQuality.LOW, false, DownloadState.TODOWNLOAD, "0", {}, {}, {}, {}, {}, {}),
    ItemUiState("default6", "toDownload", false, "default", VideoQuality.LOW, false, DownloadState.TODOWNLOAD, "0", {}, {}, {}, {}, {}, {}),
    ItemUiState("default7", "toDownload", false, "default", VideoQuality.LOW, false, DownloadState.TODOWNLOAD, "0", {}, {}, {}, {}, {}, {}),
    ItemUiState("defaul", "toDownload", false, "default", VideoQuality.LOW, false, DownloadState.TODOWNLOAD, "0", {}, {}, {}, {}, {}, {}),
    ItemUiState("defaul3", "toDownload", false, "default", VideoQuality.LOW, false, DownloadState.TODOWNLOAD, "0", {}, {}, {}, {}, {}, {}),
    ItemUiState("defaul2", "toDownload", false, "default", VideoQuality.LOW, false, DownloadState.TODOWNLOAD, "0", {}, {}, {}, {}, {}, {}),
    ItemUiState("defaul1", "toDownload", false, "default", VideoQuality.LOW, false, DownloadState.TODOWNLOAD, "0", {}, {}, {}, {}, {}, {}),
    ItemUiState("defaul4", "toDownload", false, "default", VideoQuality.LOW, false, DownloadState.TODOWNLOAD, "0", {}, {}, {}, {}, {}, {}),
    ItemUiState("defaul5", "toDownload", false, "default", VideoQuality.LOW, false, DownloadState.TODOWNLOAD, "0", {}, {}, {}, {}, {}, {}),
    ItemUiState("defaul6", "toDownload", false, "default", VideoQuality.LOW, false, DownloadState.TODOWNLOAD, "0", {}, {}, {}, {}, {}, {}),
    ItemUiState("default1", "Downloadable", false, "default", VideoQuality.LOW, false, DownloadState.DOWNLOADABLE, "0", {}, {}, {}, {}, {}, {}),
    ItemUiState("default2", "Downloading", false, "default", VideoQuality.LOW, false, DownloadState.DOWNLOADING, "0", {}, {}, {}, {}, {}, {}),
    ItemUiState("default2", "Error", false, "default", VideoQuality.LOW, false, DownloadState.ERROR, "0", {}, {}, {}, {}, {}, {}),
    ItemUiState("default2", "Downloaded", false, "default", VideoQuality.LOW, false, DownloadState.DOWNLOADED, "0", {}, {}, {}, {}, {}, {}),

)