package com.example.visondl

import android.content.Context
import android.os.Environment
import android.util.Log
import com.example.visondl.model.DownloadState
import com.example.visondl.model.VideoQuality
import com.example.visondl.ui.ItemUiState
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import com.yausername.youtubedl_android.YoutubeDLRequest
import java.io.File

val APP_FOLDER_PATH = "${Environment.getExternalStorageDirectory().path}/.ViSonDl"
val THUMBNAILS_FOLDER_PATH = "$APP_FOLDER_PATH/Thumbnails"
val ARCHIVES_FOLDER_PATH = "$APP_FOLDER_PATH/Archives"
val JSON_DATA_FILE_PATH = "${APP_FOLDER_PATH}/ViSonDl.json"
val DEFAULT_DOWNLOAD_FOLDER_PATH = "Interne/Music/ViSonDl/"
val MUSICS_FOLDER_PATH = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).path}/ViSonDl/"
val VIDEOS_FOLDER_PATH = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).path}/ViSonDl/"

// WORK MANAGER TAGS
const val TAG_OUTPUT = "Output"


val DEFAULT_ITEM_UI_STATE =
    ItemUiState("default", "default", false, DEFAULT_DOWNLOAD_FOLDER_PATH, VideoQuality.LOW,
        false, DownloadState.ERROR, "0", {}, {}, {}, {}, {}, {})

val DEFAULT_ITEM_UI_STATE_FOR_PREVIEW =
    ItemUiState("default", "default", false, "path", VideoQuality.LOW,
        false, DownloadState.ERROR, "0", {}, {}, {}, {}, {}, {})

fun creationFolderAndFile() {

    //Création des dossiers /.ViSon_Dl + /Thumbnails + /Archives
    val appFolder = File(APP_FOLDER_PATH)
    val thumbnailFolder = File(THUMBNAILS_FOLDER_PATH)
    val archivesFolder = File(ARCHIVES_FOLDER_PATH)
    if (!appFolder.exists() || !thumbnailFolder.exists()) {
        appFolder.mkdir()
        thumbnailFolder.mkdir()
        archivesFolder.mkdir()
    }
    val musicFolder = File(MUSICS_FOLDER_PATH)
    if (!musicFolder.exists()) {
        musicFolder.mkdir()
    }
    val videoFolder = File(VIDEOS_FOLDER_PATH)
    if (!videoFolder.exists()) {
        videoFolder.mkdir()
    }
    Log.i("MainActivity", "Check/Création dossiers et fichiers fini(e)")
} ///////////////////////////////////////////////////////////////////////////////////////////////


fun checkSpellTitle(title: String): String {
    return title.replace("[/%|\\\\]".toRegex(), "-")
}

fun checkDownloadPath(downloadPath: String): String {
    return if (downloadPath.last() != '/') downloadPath.plus('/').trim() else downloadPath.trim()
}


fun initLibs(applicationContext: Context) {
    try {
        YoutubeDL.getInstance().init(applicationContext)
        //YoutubeDL.getInstance().updateYoutubeDL(applicationContext)
        FFmpeg.getInstance().init(applicationContext)
        //Aria2c.getInstance().init(applicationContext)
    } catch (e: YoutubeDLException) {
        e.printStackTrace()
    }
}

private fun test() {
    val youtubeDLDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "youtubedl-android")
    val request = YoutubeDLRequest("https://www.youtube.com/watch?v=sFzDQ2OjFko")
    request.addOption("--extractor-args", "youtube:player_client=ios")
    //request.addOption("--downloader", "libaria2c.so");
    //request.addOption("--external-downloader-args", "aria2c:\"--summary-interval=1\"");
    request.addOption("-o", youtubeDLDir.absolutePath + "/%(title)s.%(ext)s")
    request.addOption("-x")
    request.addOption("--embed-thumbnail")
    request.addOption("--audio-format", "mp3")
    val processId = "MyProcessDownloadId"
    YoutubeDL.execute(
        request = request,
        processId = processId,
        callback = { progress : Float, etaInSeconds : Long, _ : String ->
            println("$progress% (ETA $etaInSeconds seconds)")
        }
    )
}