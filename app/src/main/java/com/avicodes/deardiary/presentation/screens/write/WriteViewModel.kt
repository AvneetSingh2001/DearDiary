package com.avicodes.deardiary.presentation.screens.write

import android.app.ActivityManager.TaskDescription
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avicodes.deardiary.data.repository.MongoDB
import com.avicodes.deardiary.model.Diary
import com.avicodes.deardiary.model.Mood
import com.avicodes.deardiary.utils.Constants.WRITE_SCREEN_ARGUMENT_KEY
import com.avicodes.deardiary.utils.RequestState
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WriteViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var uiState by mutableStateOf(UiState())
        private set

    init {
        getDiaryIdArgument()
        fetchSelectedDiary()
    }

    private fun getDiaryIdArgument() {
        uiState = uiState.copy(
            selectedDiaryId = savedStateHandle.get(
                key = WRITE_SCREEN_ARGUMENT_KEY
            )
        )
    }

    private fun fetchSelectedDiary() {
        if (uiState.selectedDiaryId != null) {
            viewModelScope.launch(Dispatchers.Main) {
                val diary = MongoDB.getSelectedDiary(
                    diaryId = ObjectId.Companion.from(uiState.selectedDiaryId!!)
                )
                if (diary is RequestState.Success) {
                    setSelectedDiary(diary = diary.data)
                    setTitle(title = diary.data.title)
                    setDescription(description = diary.data.description)
                    setMood(mood = Mood.valueOf(diary.data.mood))

                }
            }
        }
    }

    fun setSelectedDiary(diary: Diary) {
        uiState = uiState.copy(
            selectedDiary = diary
        )
    }

    fun setTitle(title: String) {
        uiState = uiState.copy(
            title = title
        )
    }

    fun setDescription(description: String) {
        uiState = uiState.copy(
            description = description
        )
    }

    fun setMood(mood: Mood) {
        uiState = uiState.copy(
            mood = mood
        )
    }
}


data class UiState(
    val selectedDiaryId: String? = null,
    val selectedDiary: Diary? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral
)