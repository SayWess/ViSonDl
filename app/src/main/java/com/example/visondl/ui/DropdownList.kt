package com.example.visondl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.visondl.model.VideoQuality
import com.example.visondl.ui.theme.ViSonDlTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuBox(
    itemVideoQualityIndex: Int,
    onVideoQualityChange: (VideoQuality) -> Unit
) {
    val videoQualities = arrayOf("480p", "720p", "1080p", "1440p")
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier,
        //.padding(5.dp)
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier,
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            },
        ) {
            TextField(
                value = videoQualities[itemVideoQualityIndex],
                onValueChange = {},
                shape = CircleShape,
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .width(120.dp),
                colors = ExposedDropdownMenuDefaults.textFieldColors(
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

                    focusedLabelColor = MaterialTheme.colorScheme.secondary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.secondary,
                    disabledLabelColor = MaterialTheme.colorScheme.secondary,
                    errorLabelColor = MaterialTheme.colorScheme.secondary,

                    focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    errorPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,

                    focusedPrefixColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedPrefixColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledPrefixColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    errorPrefixColor = MaterialTheme.colorScheme.onPrimaryContainer,

                    focusedSuffixColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedSuffixColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledSuffixColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    errorSuffixColor = MaterialTheme.colorScheme.onPrimaryContainer,

                    )
            )

            ExposedDropdownMenu(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .border(2.dp, MaterialTheme.colorScheme.onSecondary),
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                videoQualities.forEachIndexed { index, videoQualityString ->
                    DropdownMenuItem(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                        text = { Text(text = videoQualityString) },
                        onClick = {
                            expanded = false
                            onVideoQualityChange(VideoQuality.entries[index])
                        }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DropdownListPreview() {
    ViSonDlTheme {
        ExposedDropdownMenuBox(0) {}
    }
}