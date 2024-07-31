package com.example.visondl.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.visondl.ARCHIVES_FOLDER_PATH
import com.example.visondl.R
import com.example.visondl.THUMBNAILS_FOLDER_PATH
import com.example.visondl.model.Item
import com.example.visondl.model.VideoQuality
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException

const val KEY_ITEM_ID = "ItemID"
const val KEY_DOWNLOAD_PERCENT = "DownloadPercent"
const val KEY_ITEM_URL = "ItemUrl"
const val KEY_IS_PLAYLIST = "isItemPlaylist"
const val KEY_ITEM = "Item"

val KEY_PLAYLISTS_DAILY_DOWNLOAD = "Playlists Daily Download"

const val KEY_NOTIFICATION_SUMMARY = 0

const val TAG_VIDEO = "Video"
const val TAG_PLAYLIST = "Playlist"
const val TAG_ITEM_DOWNLOAD = "Download Item"

const val PERIODIC_WORKER_REPEAT_TIME: Long = 12
const val PERIODIC_WORKER_FLEX_TIME: Long = 15

private const val TAG = "WorkersUtils"

// For ThumbnailDownloadWorker

fun prepareThumbnailDownloadRequest(itemUrl: String, itemId: String, isPlaylist: Boolean): YoutubeDLRequest {
    val request = YoutubeDLRequest(itemUrl)
        .addOption("-o", "$THUMBNAILS_FOLDER_PATH/$itemId.%(ext)s")
        .addOption("--write-thumbnail")
        .addOption("--skip-download")
    return if (isPlaylist) request.addOption("--playlist-start", "1").addOption("--playlist-end", "1") else request
}

fun processImage(itemId: String) {

    if (!File("$THUMBNAILS_FOLDER_PATH/$itemId.jpg").exists()) return

    val out: FileOutputStream
    try {
        val bm = BitmapFactory.decodeFile("$THUMBNAILS_FOLDER_PATH/$itemId.jpg")
        val imageFile = File("$THUMBNAILS_FOLDER_PATH/$itemId.webp") // Imagename.jpeg
        out = FileOutputStream(imageFile)
        bm.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, out)
        out.flush()
        out.close()

        File("$THUMBNAILS_FOLDER_PATH/$itemId.jpg").delete()

    } catch (e: IOException) {
        e.printStackTrace()
        Log.d(TAG, "Erreur while saving thumbnail")
    }
}


// For ItemDownloadWorker

fun prepareDownloadVideoRequest(item: Item): YoutubeDLRequest {
    val request = YoutubeDLRequest(item.url)
        .addOption("-o", item.downloadPath + item.title + ".%(ext)s")
        .addOption("--extractor-args", "youtube:player_client=ios")
        .addOption("--add-metadata")

    if (item.video) {

        when (item.videoQuality) {
            VideoQuality.LOW -> request.addOption("-f", "(mp4)[height=" + "360" + "]+bestaudio/best")
            VideoQuality.MEDIUM -> request.addOption("-f", "(mp4)[height=" + "720" + "]+bestaudio/best")
            VideoQuality.HIGH -> request.addOption("-f", "(mp4)[height=" + "1080" + "]+bestaudio/best")
            VideoQuality.ULTRAHIGH -> request.addOption("-f", "(mp4)[height=" + "1440" + "]+bestaudio/best")
        }

    } else {
        request.addOption("-x")
        request.addOption("--audio-format", "opus")
        request.addOption("--embed-thumbnail")
    }

    return request
}

fun prepareDownloadPlaylistRequest(item: Item): YoutubeDLRequest {
    val request = YoutubeDLRequest(item.url)
        .addOption("-o", item.downloadPath + item.title + "/%(title)s.%(ext)s")
        .addOption("--extractor-args", "youtube:player_client=ios")
        .addOption("--add-metadata")
        .addOption("--download-archive",ARCHIVES_FOLDER_PATH + "/" + item.id) // Garde en dans un fichier les ids des vidéos déjà téléchargées
        .addOption("-i") // Ne s'arrête pas de télécharger si il y a une erreur, comme une vidéo mise en privée ou bloquée dans le pays


    if (item.video) {

        when (item.videoQuality) {
            VideoQuality.LOW -> request.addOption("-f", "(mp4)[height=" + "480" + "]+bestaudio/best")
            VideoQuality.MEDIUM -> request.addOption("-f", "(mp4)[height=" + "720" + "]+bestaudio/best")
            VideoQuality.HIGH -> request.addOption("-f", "(mp4)[height=" + "1080" + "]+bestaudio/best")
            VideoQuality.ULTRAHIGH -> request.addOption("-f", "(mp4)[height=" + "1440" + "]+bestaudio/best")
        }

    } else {
        request.addOption("-x")
        request.addOption("--audio-format", "opus")
        request.addOption("--embed-thumbnail")
    }

    return request
}

fun getPlaylistLength(playlistUrl: String): Int? {

    val request = YoutubeDLRequest(playlistUrl)
        .addOption("--flat-playlist")
        .addOption("--playlist-end", "1")

    return try {
        val response = YoutubeDL.getInstance().execute(request).out
        Regex("Downloading 1 items of (.*)\n").find(response)?.value?.let { match ->
            Log.d(TAG, match)
            match.substringAfterLast(" ").removeSuffix("\n").toInt()
        }

//         Output de la forme ->
//            [youtube:tab] Extracting URL: https://youtube.com/playlist?list=PLN5L-o4iUBhYlhbKMCg3TVqDKvRxmY1_I&si=_kfwVoyf_YgOTAPr
//            [youtube:tab] PLN5L-o4iUBhYlhbKMCg3TVqDKvRxmY1_I: Downloading webpage
//            [youtube:tab] PLN5L-o4iUBhYlhbKMCg3TVqDKvRxmY1_I: Redownloading playlist API JSON with unavailable videos
//            [download] Downloading playlist: ＜Music Video＞
//            [youtube:tab] Playlist ＜Music Video＞: Downloading 1 items of 34
//            [download] Downloading item 1 of 1
//            [download] Finished downloading playlist: ＜Music Video＞
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }
}

fun getOfNbAlreadyDownloadedVideos(playlistId: String): Int {
    val archiveFile = File("$ARCHIVES_FOLDER_PATH/$playlistId")
    var nbOfAlreadyDownloadedVideos = 0
    if (archiveFile.exists()) {
        try {
            val br = BufferedReader(FileReader(archiveFile))
            while (br.readLine() != null) {
                nbOfAlreadyDownloadedVideos += 1
            }
            br.close()
        } catch (e: IOException) {
            //You'll need to add proper error handling here
        }
    }
    return nbOfAlreadyDownloadedVideos

}