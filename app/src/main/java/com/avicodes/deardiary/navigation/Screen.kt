package com.avicodes.deardiary.navigation

import com.avicodes.deardiary.utils.Constants.WRITE_SCREEN_ARGUMENT_KEY

sealed class Screen(val route: String){
    object Authentication: Screen(route = "authenticatoin_screen")
    object Home: Screen(route = "home_screen")

    object Write: Screen(route = "write_screen?$WRITE_SCREEN_ARGUMENT_KEY="+
            "{$WRITE_SCREEN_ARGUMENT_KEY}") {
        fun passDiaryId(diaryId: String) = "write_screen?$WRITE_SCREEN_ARGUMENT_KEY=$diaryId"
    }

}