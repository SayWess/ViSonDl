package com.example.visondl.ui

import android.content.Intent
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager
import com.example.visondl.DEFAULT_ITEM_UI_STATE
import com.example.visondl.R
import com.example.visondl.model.ItemsViewModel
import com.example.visondl.ui.theme.ViSonDlTheme

private const val TAG = "MainScreen"

enum class VisonDlScreen(@StringRes val title: Int) {
    Video(title = R.string.videos),
    Playlist(title = R.string.playlists),
    ItemInfo(title = R.string.info)
}

@Composable
fun VisonDlApp(
    newIntent: Intent? = null,
    itemsViewModel: ItemsViewModel = ItemsViewModel(WorkManager.getInstance(LocalContext.current)),
    navController: NavHostController = rememberNavController()
) {

    Log.d(TAG, "MainScreen")
    // Get Intent to add new item
    //Log.d(TAG, "${newIntent?.getStringExtra(Intent.EXTRA_TEXT)}")
    itemsViewModel.proceedIntent(newIntent)

    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = VisonDlScreen.valueOf(
        if ( !backStackEntry?.destination?.route.isNullOrEmpty() && backStackEntry?.destination?.route!!.contains("/")) {
            VisonDlScreen.ItemInfo.name
        } else {
            backStackEntry?.destination?.route ?: VisonDlScreen.Video.name
        }
    )


    Scaffold(
        topBar = {
            TopAppBar(currentScreen = currentScreen)
        }
    ) { innerPadding ->
        val uiState by itemsViewModel.uiState.collectAsStateWithLifecycle()
        itemsViewModel.outputWorkInfos.observe(LocalLifecycleOwner.current, itemsViewModel.workInfosObserver())

        Log.d(TAG, "Video" + uiState.isDownloadingPlaylists + uiState.isDownloadingVideos)

        NavHost(
            navController = navController,
            startDestination = VisonDlScreen.Video.name,
            modifier = Modifier
        ) {
            composable(route = VisonDlScreen.Video.name) {
                ItemsScreen(
                    modifier = Modifier.padding(innerPadding),
                    uiState = uiState,
                    onSwitchItemsButtonClicked = {
                        navController.navigate(VisonDlScreen.Playlist.name)
                    },
                    onItemInfoClick = {
                        navController.navigate("${VisonDlScreen.ItemInfo.name}/$it")
                    },
                    onImageLoadingError = {
                        itemsViewModel.downloadImage(it)
                    },
                    onDownloadClick = {
                        itemsViewModel.toggleDownload(isPlaylist = false)
                    }
                )
            }

            composable(route = VisonDlScreen.Playlist.name) {
                ItemsScreen(
                    modifier = Modifier.padding(innerPadding),
                    uiState = uiState,
                    isPlaylist = true,
                    onSwitchItemsButtonClicked = {
                        navController.popBackStack(VisonDlScreen.Video.name, false)
                    },
                    onItemInfoClick = {
                        navController.navigate("${VisonDlScreen.ItemInfo.name}/$it")
                    },
                    onImageLoadingError = {
                        itemsViewModel.downloadImage(it)
                    },
                    onDownloadClick = {
                        itemsViewModel.toggleDownload(isPlaylist = true)
                    }
                )
            }

            composable("${VisonDlScreen.ItemInfo.name}/{itemId}") { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId")
                Log.d(TAG, "$itemId")
                if (itemId != null) {

                    val itemUiState = uiState.items.find { it.id == itemId } ?: DEFAULT_ITEM_UI_STATE

                    ItemInfo(
                        modifier = Modifier.padding(top = innerPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding()),
                        onDeleteClick = {
                            itemsViewModel.deleteItemById(itemId)
                            navController.popBackStack(VisonDlScreen.Video.name, false)
                        },
                        itemUiState = itemUiState
                    )
                }

            }
        }


    }



}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(modifier: Modifier = Modifier, currentScreen: VisonDlScreen) {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(currentScreen.title),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    ViSonDlTheme {
        VisonDlApp()
    }
}