package com.example.visondl.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.visondl.THUMBNAILS_FOLDER_PATH
import com.yausername.youtubedl_android.YoutubeDL
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

private const val TAG = "ThumbnailDownloadWorker"

class ThumbnailDownloadWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    private fun cleanJpg(path: String) {
        val jpgFile = File(path)
        jpgFile.delete()
    }

    override fun doWork(): Result {
        //val appContext = applicationContext

        Log.d(TAG, "ThumbnailDownload")

        val itemUrl = inputData.getString(KEY_ITEM_URL)
        val itemId = inputData.getString(KEY_ITEM_ID)
        val isItemPlaylist = inputData.getString(KEY_IS_PLAYLIST)

        if (itemUrl == null || itemId == null || isItemPlaylist == null) return Result.failure()

        // Download Image
        val request = prepareThumbnailDownloadRequest(itemUrl, itemId, isItemPlaylist.toBoolean())
        YoutubeDL.execute(request)

        // Convert image to WEBP if necessary and suppress the JPG
        processImage(itemId)


        //makeStatusNotification()
        return Result.success()
    }
}