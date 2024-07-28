package com.example.visondl.workers

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker.Result.Success
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.visondl.data.DataManager
import com.example.visondl.model.DownloadState
import com.example.visondl.model.Item
import com.example.visondl.notification.NotificationManager
import com.google.gson.Gson
import com.yausername.youtubedl_android.YoutubeDL
import java.lang.Thread.sleep
import kotlin.math.roundToInt
import kotlin.random.Random

private const val TAG = "ItemDownloadWorker"

class ItemDownloadWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val appContext = applicationContext

        Log.d(TAG, "ItemDownload")

        // Get Input Data
        val itemId = inputData.getString(KEY_ITEM_ID) ?: return Success.failure()
        // Get the Item to Download and put its state to DOWNLOADING
        val item = DataManager().getItemById(itemId)
        item.state = DownloadState.DOWNLOADING

        // If item is playlist, get advancement of the downloaded video of the playlist
        val playlistLength = if (item.isPlaylist) getPlaylistLength(item.url) else null
        var nbOfAlreadyDownloadedVideos = if (item.isPlaylist) getOfNbAlreadyDownloadedVideos(item.id) else null
        val nbOfVideosToDownload = nbOfAlreadyDownloadedVideos?.let { playlistLength?.minus(it) }

        // Manage Notification
        val notificationManager = NotificationManager(appContext)
        notificationManager.createNotificationChannel()
        val notificationId = Random.nextInt()
        val currentTime = System.currentTimeMillis()

        // Build Summary notification, Notif group which will contains all other notifs
        notificationManager.buildNotification(
            KEY_NOTIFICATION_SUMMARY,
            notificationManager.summaryNotification("VisonDl")
        )

        // Notify the user of the starting download
        notificationManager.buildNotification(
            notificationId,
            notificationManager.basicNotificationBuilder(item.title, "Starting Download of ${item.title}", createdTime = currentTime)
        )
        //////

        Log.d(TAG,"$item")
        // Prepare the YoutubeDlRequest for the item
        val request = if (item.isPlaylist) prepareDownloadPlaylistRequest(item) else prepareDownloadVideoRequest(item)

        return try {
            // Execute request
            YoutubeDL.execute(request, item.id) { progress : Float, etaInSeconds : Long, output : String ->
                Log.d(TAG, "$progress% (ETA $etaInSeconds seconds)")

                // If video : downloadPercent, if playlist : videosAlreadyDownloaded/PlaylistLength
                item.downloadPercent = if (!item.isPlaylist) progress.roundToInt().toString() else "$nbOfAlreadyDownloadedVideos/$playlistLength"

                // Update the number of videos downloaded when output matches the download of next video
                if (nbOfAlreadyDownloadedVideos != null) {
                    if (Regex("\\[download] Downloading item (.*) of (.*)").matches(output)) {
                        nbOfAlreadyDownloadedVideos += 1
                    }
                }

                // Adapt notification message if playlist
                val notificationMessage = if (!item.isPlaylist) "Downloading..." else "Downloading : $nbOfAlreadyDownloadedVideos/$playlistLength"
                //// ServiceInfo.TYPE needed from SDK 34 to run ForegroundInfo !
                setForegroundAsync(
                    ForegroundInfo(
                        notificationId,
                        notificationManager.basicNotificationBuilder(item.title, notificationMessage, if (!item.isPlaylist) item.downloadPercent.toInt() else progress.roundToInt(), currentTime).build(),
                        if (Build.VERSION.SDK_INT >= 34) ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC else 0
                    )
                )
                ////

            }
            // Change item state to DOWNLOADED
            item.state = DownloadState.DOWNLOADED

            ////
            // notify on new notificationId, otherwise will not show up
            val itemDownloadSuccessNotificationMessage =
                if (!item.isPlaylist) "Download Successful !"
                else "$nbOfVideosToDownload new items have been downloaded successfully !"
            notificationManager.buildNotification(
                notificationId + 1,
                notificationManager.basicNotificationBuilder(item.title, itemDownloadSuccessNotificationMessage, createdTime = currentTime)
            )
            ////

            Success.success()
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error while downloading Item")
            item.state = DownloadState.ERROR

            ////
            notificationManager.buildNotification(
                notificationId + 1,
                notificationManager.basicNotificationBuilder(item.title, "Download Error", createdTime = currentTime)
            )
            ////

            Success.failure()
        }

    }

    override fun onStopped() {
        super.onStopped()
        Log.d(TAG, "Worker and Download Stopped")
        YoutubeDL.getInstance().destroyProcessById(inputData.getString(KEY_ITEM_ID) ?: "")
        DataManager().saveData()
    }


}