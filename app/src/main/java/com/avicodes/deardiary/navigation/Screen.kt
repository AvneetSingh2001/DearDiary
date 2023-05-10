package com.avicodes.deardiary.navigation

import com.avicodes.deardiary.utils.Constants.WRITE_SCREEN_ARGUMENT_KEY

sealed class Screen(val route: String) {
    object Authentication: Screen(route = "authentication_screen")
    object Home: Screen(route = "home_screen")
    object Write: Screen(route = "$WRITE_SCREEN_ARGUMENT_KEY?diaryId={$WRITE_SCREEN_ARGUMENT_KEY}") {
        fun passDiaryId(diaryId: String) = "$WRITE_SCREEN_ARGUMENT_KEY?diaryId=$diaryId"
    }
}
