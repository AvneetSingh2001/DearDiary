package com.avicodes.write.navigation

import android.widget.Toast
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.avicodes.util.Constants
import com.avicodes.util.Screen
import com.avicodes.util.model.Mood
import com.avicodes.write.WriteScreen
import com.avicodes.write.WriteViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState


@OptIn(ExperimentalPagerApi::class)
fun NavGraphBuilder.writeRoute(
    onBackPressed: () -> Unit
) {
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(name = Constants.WRITE_SCREEN_ARGUMENT_KEY) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) {
        val pagerState = rememberPagerState()
        val viewModel: WriteViewModel = hiltViewModel()
        val uiState = viewModel.uiState
        val galleryState = viewModel.galleryState
        val context = LocalContext.current

        val pageNumber by remember {
            derivedStateOf { pagerState.currentPage }
        }

        WriteScreen(
            uiState = uiState,
            pagerState = pagerState,
            onBackPressed = onBackPressed,
            onDeleteConfirmed = {
                viewModel.deleteDiary(
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "Deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                        onBackPressed()
                    },
                    onError = { message ->
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            },
            onTitleChanged = { title ->
                viewModel.setTitle(title = title)
            },
            onDescriptionChanged = { description ->
                viewModel.setDescription(description = description)
            },
            moodName = {
                Mood.values()[pageNumber].name
            },
            onSaveClicked = { diary ->
                viewModel.upsertDiary(
                    diary = diary.apply { mood = Mood.values()[pageNumber].name },
                    onSuccess = { onBackPressed() },
                    onError = { message ->
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            },
            onDateTimeUpdated = { time ->
                viewModel.updateDateTime(zonedDateTime = time)
            },
            galleryState = galleryState,
            onImageSelect = {
                val type = context.contentResolver
                    .getType(it)
                    ?.split("/")
                    ?.last()
                    ?: "jpg"
                viewModel.addImage(
                    image = it,
                    imageType = type
                )
            },
            onImageDeleteClicked = {
                galleryState.removeImage(it)
            }
        )
    }
}