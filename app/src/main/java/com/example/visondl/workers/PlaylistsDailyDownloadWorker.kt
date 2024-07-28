package com.example.visondl.workers

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat.BigTextStyle
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.visondl.ARCHIVES_FOLDER_PATH
import com.example.visondl.data.DataManager
import com.example.visondl.initLibs
import com.example.visondl.model.DownloadState
import com.example.visondl.notification.NotificationManager
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import kotlin.math.roundToInt
import kotlin.random.Random


private const val TAG = "PlaylistsDailyDownloadWorker"


class PlaylistsDailyDownloadWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val notificationManager = NotificationManager(applicationContext)
    private val notificationId = 0
    private val playlistUpdated = mutableMapOf<String,Pair<String, Int>>()

    override fun doWork(): Result {

        initLibs(applicationContext)

        // Manage Notification
        notificationManager.createNotificationChannel()

        notificationManager.buildNotification(
            KEY_NOTIFICATION_SUMMARY,
            notificationManager.summaryNotification("VisonDl", "Playlists Daily Update")
        )
        //
        setForegroundAsync(
            ForegroundInfo(
                notificationId,
                notificationManager.basicNotificationBuilder("Playlists Check", "Vérification des playlists à Update").build(),
                if (Build.VERSION.SDK_INT >= 34) ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC else 0
            )
        )


        updatePlaylistsState()
        updatePlaylists()

        return Result.success()
    }


    private fun updatePlaylistsState() {
        for (playlist in DataManager().getItems().filter { it.isPlaylist && it.state == DownloadState.DOWNLOADED}) {

            val playlistLength = getPlaylistLength(playlist.url) ?: continue
            Log.d(TAG, "Playlist Length : $playlistLength")

            val nbOfAlreadyDownloadedVideos = getOfNbAlreadyDownloadedVideos(playlist.id)
            Log.d(TAG, "Downloaded Videos : $nbOfAlreadyDownloadedVideos/$playlistLength")
            if (nbOfAlreadyDownloadedVideos == playlistLength) continue
            playlist.state = DownloadState.TODOWNLOAD
        }
    }



    private fun updatePlaylists() {
        DataManager().getItems().filter { it.isPlaylist && ( it.state == DownloadState.TODOWNLOAD || it.state == DownloadState.DOWNLOADABLE)}.forEach { playlist ->
            Log.d(TAG, "Update ${playlist.title}")

            playlist.state = DownloadState.DOWNLOADING

            val playlistLength = getPlaylistLength(playlist.url)
            Log.d(TAG, "Playlist Length : $playlistLength")

            var nbOfAlreadyDownloadedVideos = getOfNbAlreadyDownloadedVideos(playlist.id)
            Log.d(TAG, "Downloaded Videos : $nbOfAlreadyDownloadedVideos/$playlistLength")

            playlistUpdated[playlist.id] = Pair(playlist.title, playlistLength?.minus(nbOfAlreadyDownloadedVideos) ?: 0)

            val request = prepareDownloadPlaylistRequest(playlist)
            try {
                YoutubeDL.getInstance().execute(request) { progress : Float, etaInSeconds : Long, output : String ->
                    Log.d(TAG, "Output : $output")
                    Log.d(TAG, "$progress% (ETA $etaInSeconds seconds)")
                    playlist.downloadPercent = progress.roundToInt().toString()

                    if (Regex("\\[download] Downloading item (.*) of (.*)").matches(output)) {
                        nbOfAlreadyDownloadedVideos += 1
                    }


                    //// ServiceInfo.TYPE needed from SDK 34 to run ForegroundInfo !
                    setForegroundAsync(
                        ForegroundInfo(
                            notificationId,
                            notificationManager.basicNotificationBuilder(playlist.title, "Downloading video $nbOfAlreadyDownloadedVideos/$playlistLength", playlist.downloadPercent.toInt()).build(),
                            if (Build.VERSION.SDK_INT >= 34) ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC else 0
                        )
                    )
                    ////
                }

                playlist.state = DownloadState.DOWNLOADED


            } catch (e: Throwable) {
                e.printStackTrace()
                playlist.state =
                    if (e.toString().contains("Sign in") || e.toString().contains("Video unavailable"))
                        DownloadState.DOWNLOADED
                    else
                        DownloadState.TODOWNLOAD
            }

        }

        if (playlistUpdated.isNotEmpty()) {
            notificationManager.buildNotification(
                notificationId + 1,
                notificationManager.basicNotificationBuilder(
                    "Playlists Updated",
                    "The following playlists have been updated"
                ).setStyle(BigTextStyle()
                    .bigText(playlistUpdated.values.joinToString(separator = "\n") {
                        "${it.first} : ${it.second} items"
                    })
                )
            )
        } else notificationManager.buildNotification(
            notificationId + 1,
                notificationManager.basicNotificationBuilder("Nothing new", "")
            )



    }

    override fun onStopped() {
        super.onStopped()
        DataManager().saveData()
    }
}