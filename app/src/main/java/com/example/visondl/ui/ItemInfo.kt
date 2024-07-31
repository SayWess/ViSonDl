package com.example.visondl.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.visondl.R
import com.example.visondl.THUMBNAILS_FOLDER_PATH
import com.example.visondl.model.DownloadState
import com.example.visondl.model.VideoQuality
import com.example.visondl.ui.theme.ViSonDlTheme
import java.io.File

private const val TAG = "ItemInfo"


@Composable
fun ItemInfo(
    modifier: Modifier = Modifier,
    itemUiState: ItemUiState,
    onDeleteClick: () -> Unit
) {

    Log.d(TAG, "ItemInfo")

    Box(modifier = modifier.fillMaxSize()) {

        Column(modifier = Modifier) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(File("$THUMBNAILS_FOLDER_PATH/${itemUiState.id}.webp"))
                    //.data(File(""))
                    .build(),
                error = painterResource(R.drawable.ic_broken_image),
                placeholder = painterResource(R.drawable.ic_broken_image),
                contentDescription = stringResource(id = R.string.thumbnailImage),
                modifier = Modifier.fillMaxWidth(),
                //modifier = modifier.weight(1f)
            )

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                value = itemUiState.title,
                onValueChange = itemUiState.onTitleChange,
                enabled = itemUiState.state != DownloadState.DOWNLOADING,
                label = { Text(stringResource(id = R.string.title)) },
                colors = TextFieldDefaults.colors(
                    // Text Color
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    errorTextColor = MaterialTheme.colorScheme.onSurface,
                    // Container Color (background)
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    errorContainerColor = MaterialTheme.colorScheme.surface,

                    cursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    errorCursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectionColors = LocalTextSelectionColors.current,
                    // Little bottom bar
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,

                    focusedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    errorLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,

                    focusedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    errorTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    // Text Label Color
                    focusedLabelColor = MaterialTheme.colorScheme.secondary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.secondary,
                    disabledLabelColor = MaterialTheme.colorScheme.secondary,
                    errorLabelColor = MaterialTheme.colorScheme.secondary,

                    focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    errorPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,

                    focusedSupportingTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedSupportingTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledSupportingTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    errorSupportingTextColor = MaterialTheme.colorScheme.onPrimaryContainer,

                    focusedPrefixColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedPrefixColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledPrefixColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    errorPrefixColor = MaterialTheme.colorScheme.onPrimaryContainer,

                    focusedSuffixColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedSuffixColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledSuffixColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    errorSuffixColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                value = itemUiState.downloadPath,
                onValueChange = itemUiState.onDownloadPathChange,
                enabled = itemUiState.state != DownloadState.DOWNLOADING,
                label = { Text(stringResource(id = R.string.downloadPath)) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    errorTextColor = MaterialTheme.colorScheme.onSurface,

                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    errorContainerColor = MaterialTheme.colorScheme.surface,

                    cursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    errorCursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectionColors = LocalTextSelectionColors.current,

                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,

                    focusedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    errorLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,

                    focusedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    errorTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    // Label Text Color
                    focusedLabelColor = MaterialTheme.colorScheme.secondary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.secondary,
                    disabledLabelColor = MaterialTheme.colorScheme.secondary,
                    errorLabelColor = MaterialTheme.colorScheme.secondary,

                    focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    errorPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,

                    focusedSupportingTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedSupportingTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledSupportingTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    errorSupportingTextColor = MaterialTheme.colorScheme.onPrimaryContainer,

                    focusedPrefixColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedPrefixColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledPrefixColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    errorPrefixColor = MaterialTheme.colorScheme.onPrimaryContainer,

                    focusedSuffixColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedSuffixColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledSuffixColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    errorSuffixColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.itemInfoVideoText),
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.secondary
                )
                Switch(
                    checked = itemUiState.video,
                    onCheckedChange = itemUiState.onVideoChange,
                    enabled = itemUiState.state != DownloadState.DOWNLOADING,
                    modifier = Modifier,
                )
            }

            //HorizontalDivider(modifier = Modifier, thickness = 1.dp, color = MaterialTheme.colorScheme.onPrimaryContainer)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.itemInfoVideoQualityText),
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.weight(1F))
                ExposedDropdownMenuBox(
                    itemVideoQualityIndex = itemUiState.videoQuality.ordinal,
                    onVideoQualityChange = itemUiState.onVideoQualityChange
                )

            }

        }

        IconButton(
            enabled = itemUiState.state != DownloadState.DOWNLOADING,
            onClick = onDeleteClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .padding(bottom = 10.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.DeleteForever,
                contentDescription = stringResource(id = R.string.deleteItem)
            )
        }
    }


}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ItemInfoPreview() {
    ViSonDlTheme {
        ItemInfo(modifier = Modifier,
            onDeleteClick = {},
            itemUiState = ItemUiState("default", "default", false, "path", VideoQuality.LOW,
                false, DownloadState.ERROR, "0", {}, {}, {}, {}, {}, {})
        )
    }
}