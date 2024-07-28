package com.example.visondl.model

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.visondl.TAG_OUTPUT
import com.example.visondl.checkDownloadPath
import com.example.visondl.checkSpellTitle
import com.example.visondl.data.DataManager
import com.example.visondl.ui.ItemUiState
import com.example.visondl.ui.ItemsUiState
import com.example.visondl.workers.ItemDownloadWorker
import com.example.visondl.workers.KEY_DOWNLOAD_PERCENT
import com.example.visondl.workers.KEY_IS_PLAYLIST
import com.example.visondl.workers.KEY_ITEM
import com.example.visondl.workers.KEY_ITEM_ID
import com.example.visondl.workers.KEY_ITEM_URL
import com.example.visondl.workers.KEY_PLAYLISTS_DAILY_DOWNLOAD
import com.example.visondl.workers.PERIODIC_WORKER_FLEX_TIME
import com.example.visondl.workers.PERIODIC_WORKER_REPEAT_TIME
import com.example.visondl.workers.PlaylistsDailyDownloadWorker
import com.example.visondl.workers.TAG_ITEM_DOWNLOAD
import com.example.visondl.workers.TAG_PLAYLIST
import com.example.visondl.workers.TAG_VIDEO
import com.example.visondl.workers.ThumbnailDownloadWorker
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.lang.Thread.sleep
import java.util.UUID
import java.util.concurrent.TimeUnit

private const val TAG = "ItemsViewModel"

class ItemsViewModel(private val workManager: WorkManager) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemsUiState())
    val uiState: StateFlow<ItemsUiState> = _uiState

    internal val outputWorkInfos: LiveData<List<WorkInfo>>

    init {
        collectData()
        outputWorkInfos = workManager.getWorkInfosByTagLiveData(TAG_ITEM_DOWNLOAD)
        scheduleDailyWork()

    }

    fun workInfosObserver(): Observer<List<WorkInfo>> {
        return Observer {
            listOfWorkInfo ->

            if (listOfWorkInfo.isEmpty()) {
                return@Observer
            }

            listOfWorkInfo.forEach {

                if (it.state == WorkInfo.State.RUNNING) {
                    collectData()
                }

            }

        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Schedule the 12h/24h worker to auto-download new videos from playlist
    //
    private fun scheduleDailyWork() {

        val constraints: Constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()


        //val periodicWorkRequest = PeriodicWorkRequestBuilder<PlaylistsDailyDownloadWorker>(PERIODIC_WORKER_REPEAT_TIME, TimeUnit.HOURS, PERIODIC_WORKER_FLEX_TIME, TimeUnit.MINUTES)
        val periodicWorkRequest = PeriodicWorkRequestBuilder<PlaylistsDailyDownloadWorker>(PERIODIC_WORKER_REPEAT_TIME, TimeUnit.HOURS)
        //    .setInitialDelay(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .addTag(TAG_ITEM_DOWNLOAD)
            .build()

        // Queue the work
        workManager.enqueueUniquePeriodicWork(KEY_PLAYLISTS_DAILY_DOWNLOAD, ExistingPeriodicWorkPolicy.UPDATE, periodicWorkRequest)

    }

    fun toggleDownload(isPlaylist: Boolean) {
        Log.d(TAG, "Download Toggled")

        if (isPlaylist) {
            if (_uiState.value.isDownloadingPlaylists) {
                stopItemsDownload(isPlaylist)
                // Stop Download
            } else {
                uiState.value.items.filter {
                    it.state == DownloadState.TODOWNLOAD && it.isPlaylist
                }.forEach {
                    startItemDownload(itemId = it.id, isPlaylist = it.isPlaylist)
                }
            }
        } else {
            if (_uiState.value.isDownloadingVideos) {
                stopItemsDownload(isPlaylist)
                // Stop Download
            } else {

                // Start Download
                uiState.value.items.filter {
                    it.state == DownloadState.TODOWNLOAD && !it.isPlaylist
                }.forEach {
                    startItemDownload(itemId = it.id, isPlaylist = it.isPlaylist)
                }
            }
        }

    }

    private fun startItemDownload(itemId: String, isPlaylist: Boolean) {
        Log.d(TAG, "ENQUEUING WORKER, Item : $itemId ")
        val workerRequest = OneTimeWorkRequestBuilder<ItemDownloadWorker>()
            .setInputData(Data.Builder()
                .putString(KEY_ITEM_ID, itemId)
                .build()
            )
            .addTag(if (isPlaylist) TAG_PLAYLIST else TAG_VIDEO)
            .addTag(TAG_ITEM_DOWNLOAD)
            .build()
        workManager.enqueueUniqueWork("Item-${itemId}", ExistingWorkPolicy.KEEP, workerRequest)

    }

    private fun stopItemsDownload(isPlaylist: Boolean = false, itemId: String? = null) {
        Log.d(TAG, "Cancel work : $isPlaylist")
        if (itemId != null) workManager.cancelUniqueWork("Item-${itemId}")
        else workManager.cancelAllWorkByTag(if (isPlaylist) TAG_PLAYLIST else TAG_VIDEO)
    }

    private fun collectData() {

        _uiState.update {
            it.copy(
                isDownloadingVideos =  _uiState.value.items.any { item -> !item.isPlaylist && item.state == DownloadState.DOWNLOADING },
                isDownloadingPlaylists =  _uiState.value.items.any { item -> item.isPlaylist && item.state == DownloadState.DOWNLOADING } ,
                items = sortItems(getLastItems())
            )
        }

    }

    /**
     * Map the items received from the DataManager to transform them in ItemUiState
     * @return a list of ItemUiState
     */
    private fun getLastItems() : List<ItemUiState>  {
        return DataManager().getItems().map { item ->
            ItemUiState(
                id = item.id,
                title = item.title,
                isPlaylist = item.isPlaylist,
                state = item.state,
                downloadPercent = item.downloadPercent,
                downloadPath = item.downloadPath,
                video = item.video,
                videoQuality = item.videoQuality,
                onCheckedChange = {
                    item.state = if (it) DownloadState.TODOWNLOAD else DownloadState.DOWNLOADABLE
                    collectData()
                },
                onTitleChange = {
                    item.title = checkSpellTitle(it)
                    collectData()
                },
                onDownloadPathChange = {
                    item.downloadPath = checkDownloadPath(it)
                    collectData()
                },
                onVideoChange = {
                    if (item.state == DownloadState.DOWNLOADING) return@ItemUiState
                    item.video = it
                    collectData()
                },
                onVideoQualityChange = {
                    item.videoQuality = it
                    collectData()
                },
                onItemLongClick = {
                    if (item.state == DownloadState.DOWNLOADING) stopItemsDownload(itemId = item.id)
                    if (item.state == DownloadState.DOWNLOADED || item.state == DownloadState.ERROR) {
                        item.state = DownloadState.DOWNLOADABLE
                    }
                    collectData()
                }
            )
        }
    }

    private fun sortItems(items: List<ItemUiState>) : List<ItemUiState> {
        val listError = mutableListOf<ItemUiState>()
        val listDownloadableToDownload = mutableListOf<ItemUiState>()
        val listDownloading = mutableListOf<ItemUiState>()
        val listDownloaded = mutableListOf<ItemUiState>()
        items.groupBy { it.state }.forEach { (key, value) ->
            when (key) {
                DownloadState.ERROR -> listError.addAll(value)
                DownloadState.TODOWNLOAD, DownloadState.DOWNLOADABLE -> listDownloadableToDownload.addAll(value)
                DownloadState.DOWNLOADING -> listDownloading.addAll(value)
                DownloadState.DOWNLOADED -> listDownloaded.addAll(value)
            }
        }

        return listError.sortedBy { it.title } + listDownloadableToDownload.sortedBy { it.title } +
                listDownloading.sortedBy { it.title } + listDownloaded.sortedBy { it.title }

    }

    private fun addItem(url: String, title: String) {
        DataManager().addItem(url, title)
        collectData()
    }

    fun proceedIntent(intent: Intent?) {
        if (intent != null && intent.type == "text/plain") {
            val url = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
            val title = intent.getStringExtra(Intent.EXTRA_SUBJECT) ?: "Untitled"
            Log.d(TAG, url + "\n" + title)
            addItem(url = url, title = title)
        }
    }

    /**
     * Create a worker that will download an item (video or playlist) thumbnail
     * @return Unit
     */
    fun downloadImage(itemId: String) {
        val item = DataManager().getItemById(itemId)
        val workerRequest = OneTimeWorkRequestBuilder<ThumbnailDownloadWorker>()
            .setInputData(Data.Builder()
                .putString(KEY_ITEM_URL, item.url)
                .putString(KEY_ITEM_ID, itemId)
                .putString(KEY_IS_PLAYLIST, item.isPlaylist.toString())
                .build()
            ).build()
        workManager.enqueueUniqueWork("Image-${itemId}", ExistingWorkPolicy.KEEP, workerRequest)
    }

    /**
     * Delete definitively an item then update UI
     * @return Unit
     */
    fun deleteItemById(itemId: String) {
        DataManager().deleteItemById(itemId)
        collectData()
    }
}