package com.example.visondl.ui

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.SyncAlt
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.visondl.R
import com.example.visondl.THUMBNAILS_FOLDER_PATH
import com.example.visondl.data.testItemsUiState
import com.example.visondl.model.DownloadState
import com.example.visondl.ui.theme.ViSonDlTheme
import java.io.File

private const val TAG = "ItemsScreen"

@Composable
fun ItemsScreen(
    modifier: Modifier = Modifier,
    uiState: ItemsUiState,
    isPlaylist: Boolean = false,
    onSwitchItemsButtonClicked: () -> Unit,
    onItemInfoClick: (String) -> Unit,
    onDownloadClick: () -> Unit,
    onImageLoadingError: (String) -> Unit
) {

    Log.d(TAG, "ItemsScreen")


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        ItemsColumn(
            modifier = modifier,
            onItemInfoClick = onItemInfoClick,
            onImageLoadingError = onImageLoadingError,
            itemsList = uiState.items.filter { item -> if (isPlaylist) item.isPlaylist else !item.isPlaylist }
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp)
        ) {
            DownloadButton(
                modifier = modifier,
                isDownloading = if (isPlaylist) uiState.isDownloadingPlaylists else uiState.isDownloadingVideos,
                onClick = onDownloadClick
            )

            Spacer(modifier = modifier.width(5.dp))

            SwitchItemsButton(
                modifier = modifier,
                onClick = onSwitchItemsButtonClicked
            )
        }


    }


}

@Composable
fun ItemsColumn(
    itemsList: List<ItemUiState>,
    modifier: Modifier = Modifier,
    onItemInfoClick: (String) -> Unit,
    onImageLoadingError: (String) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(items = itemsList, key = { _, item -> item.id }) { index, item ->
            ItemRow(item = item,
                onItemInfoClick = {
                    Log.d(TAG, "$item")
                    onItemInfoClick(item.id)
                },
                onItemLongClick = item.onItemLongClick,
                onImageLoadingError = { onImageLoadingError(item.id) }
            )

            if (index < itemsList.lastIndex) HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5F)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemRow(
    item: ItemUiState,
    modifier: Modifier = Modifier,
    onItemInfoClick: () -> Unit,
    onImageLoadingError: () -> Unit,
    onItemLongClick: () -> Unit
) {

    val rowModifier = when (item.state) {
        DownloadState.DOWNLOADABLE, DownloadState.TODOWNLOAD, DownloadState.DOWNLOADING -> modifier.background(
            MaterialTheme.colorScheme.secondaryContainer
        )

        DownloadState.DOWNLOADED -> modifier.background(MaterialTheme.colorScheme.onPrimaryContainer)
        DownloadState.ERROR -> modifier.background(MaterialTheme.colorScheme.errorContainer)
    }

    Row(
        modifier = rowModifier
            .combinedClickable(onClick = onItemInfoClick, onLongClick = onItemLongClick)
            .padding(2.dp)
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(File("$THUMBNAILS_FOLDER_PATH/${item.id}.webp"))
                //.data(File(""))
                .build(),
            error = painterResource(R.drawable.ic_broken_image),
            onError = { onImageLoadingError() },
            placeholder = painterResource(R.drawable.ic_broken_image),
            contentDescription = stringResource(id = R.string.thumbnailImage),
            modifier = modifier
                .width((90.dp))
                .clip(RoundedCornerShape(percent = 70)),
            contentScale = ContentScale.FillWidth
            //modifier = modifier.weight(1f)
        )

        Text(
            text = item.title,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
                .weight(1f)
                .padding(2.dp)
                .padding(start = 5.dp)
        )

        when (item.state) {
            DownloadState.TODOWNLOAD, DownloadState.DOWNLOADABLE, DownloadState.ERROR -> {
                Checkbox(
                    modifier = modifier
                        .weight(0.15f)
                        .padding(0.dp),
                    checked = item.state == DownloadState.TODOWNLOAD,
                    onCheckedChange = item.onCheckedChange,
                    colors = CheckboxColors(
                        checkedBorderColor = MaterialTheme.colorScheme.outline,
                        checkedCheckmarkColor = MaterialTheme.colorScheme.onSurface,
                        checkedBoxColor = MaterialTheme.colorScheme.surface,
                        uncheckedCheckmarkColor = MaterialTheme.colorScheme.onSurface,
                        uncheckedBorderColor = MaterialTheme.colorScheme.outline,
                        uncheckedBoxColor = MaterialTheme.colorScheme.surface,
                        disabledCheckedBoxColor = MaterialTheme.colorScheme.onSurface,
                        disabledUncheckedBorderColor = MaterialTheme.colorScheme.outline,
                        disabledUncheckedBoxColor = MaterialTheme.colorScheme.surface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledIndeterminateBorderColor = MaterialTheme.colorScheme.onSurface,
                        disabledIndeterminateBoxColor = MaterialTheme.colorScheme.surface

                    )
                )
            }

            DownloadState.DOWNLOADING -> {
                Text(
                    text = "${item.downloadPercent}" + if (!item.isPlaylist && item.downloadPercent != "") "%" else "",
                    fontSize = TextUnit(14F, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = modifier
                        .padding(0.dp)
                        .weight(0.15f)
                )
            }

            DownloadState.DOWNLOADED -> {
                Spacer(modifier = modifier.weight(0.15f))
            }
        }


    }
}


@Composable
fun DownloadButton(modifier: Modifier = Modifier, isDownloading: Boolean, onClick: () -> Unit) {

    if (isDownloading) {
        IconButton(
            onClick = onClick, modifier = modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Icon(
                imageVector = Icons.Rounded.Stop,
                contentDescription = stringResource(id = R.string.startDownloadTxt)
            )
        }
    } else {
        IconButton(
            onClick = onClick, modifier = modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Icon(
                imageVector = Icons.Rounded.Download,
                contentDescription = stringResource(id = R.string.startDownloadTxt)
            )
        }
    }
}

@Composable
fun SwitchItemsButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    IconButton(
        onClick = onClick, modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Icon(
            imageVector = Icons.Rounded.SyncAlt,
            contentDescription = stringResource(id = R.string.switchVideoPlaylist)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ItemsScreenPreview() {
    ViSonDlTheme {
        ItemsScreen(
            uiState = ItemsUiState(isDownloadingVideos = false, items = testItemsUiState),
            onSwitchItemsButtonClicked = {},
            onItemInfoClick = {},
            onImageLoadingError = {},
            onDownloadClick = {}
        )
    }
}