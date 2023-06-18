package com.avicodes.deardiary.navigation

import android.widget.Toast
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.avicodes.deardiary.model.Diary
import com.avicodes.deardiary.model.Mood
import com.avicodes.deardiary.presentation.components.DisplayAlertDialog
import com.avicodes.deardiary.presentation.screens.auth.AuthenticationScreen
import com.avicodes.deardiary.presentation.screens.auth.AuthenticationViewModel
import com.avicodes.deardiary.presentation.screens.home.HomeViewModel
import com.avicodes.deardiary.presentation.screens.write.HomeScreen
import com.avicodes.deardiary.presentation.screens.write.WriteScreen
import com.avicodes.deardiary.presentation.screens.write.WriteViewModel
import com.avicodes.deardiary.utils.Constants.APP_ID
import com.avicodes.deardiary.utils.Constants.WRITE_SCREEN_ARGUMENT_KEY
import com.avicodes.deardiary.utils.RequestState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext

@Composable
fun SetUpNavGraph(
    startDestination: String, navController: NavHostController, onDataLoaded: () -> Unit
) {
    NavHost(navController = navController, startDestination = startDestination) {
        authenticationRoute(
            navigateToHome = {
                navController.popBackStack()
                navController.navigate(Screen.Home.route)
            },
            onDataLoaded = onDataLoaded
        )
        homeRoute(
            navigateToWrite = {
                navController.navigate(Screen.Write.route)
            },
            navigateToAuth = {
                navController.popBackStack()
                navController.navigate(Screen.Authentication.route)
            },
            onDataLoaded = onDataLoaded,
            navigateToWriteWithArgs = { diaryId ->
                navController.navigate(Screen.Write.passDiaryId(diaryId = diaryId))
            }
        )
        writeRoute(
            onBackPressed = {
                navController.popBackStack()
            }
        )
    }
}

fun NavGraphBuilder.authenticationRoute(
    navigateToHome: () -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(
        route = Screen.Authentication.route,
    ) {
        val viewModel: AuthenticationViewModel = viewModel()
        val loadingState by viewModel.loadingState
        val authenticated by viewModel.authenticated
        val oneTapState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()

        LaunchedEffect(key1 = Unit) {
            onDataLoaded()
        }

        AuthenticationScreen(
            authenticated = authenticated,
            loadingState = loadingState,
            oneTapSignInState = oneTapState,
            messageBarState = messageBarState,
            onTokenIdReceived = { tokenId ->
                viewModel.signInWithMongoAtlas(
                    tokenId = tokenId,
                    onSuccess = {
                        messageBarState.addSuccess("Success")
                        viewModel.setLoading(false)
                    },
                    onError = { exception ->
                        messageBarState.addError(exception = exception)
                        viewModel.setLoading(false)
                    }
                )
            },
            onDialogDismissed = { message ->
                messageBarState.addError(Exception(message))
                viewModel.setLoading(false)
            },
            onButtonClicked = {
                oneTapState.open()
                viewModel.setLoading(true)
            },
            navigateToHome = {
                navigateToHome()
            }
        )
    }
}

fun NavGraphBuilder.homeRoute(
    navigateToWrite: () -> Unit,
    navigateToWriteWithArgs: (String) -> Unit,
    navigateToAuth: () -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(route = Screen.Home.route) {
        val viewModel: HomeViewModel = viewModel()
        val diaries by viewModel.diaries
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var signOutDialogOpened by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = diaries) {
            if (diaries !is RequestState.Loading) {
                onDataLoaded()
            }
        }

        HomeScreen(
            diaries = diaries,
            onMenuClicked = {
                scope.launch {
                    drawerState.open()
                }
            },
            navigateToWrite = navigateToWrite,
            onDeleteAllClicked = {},
            onSignOutClicked = {
                signOutDialogOpened = true
            },
            drawerState = drawerState,
            navigateToWriteWithArgs = navigateToWriteWithArgs
        )

        DisplayAlertDialog(
            title = "Sign Out",
            message = "Are you sure you want to Sign Out from your Google Account?",
            dialogOpened = signOutDialogOpened,
            onDialogClosed = { signOutDialogOpened = false },
            onYesClicked = {
                scope.launch(Dispatchers.IO) {
                    val user = App.create(APP_ID).currentUser
                    if (user != null) {
                        user.logOut()
                        withContext(Dispatchers.Main) {
                            navigateToAuth()
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
fun NavGraphBuilder.writeRoute(
    onBackPressed: () -> Unit
) {
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(name = WRITE_SCREEN_ARGUMENT_KEY) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) {
        val pagerState = rememberPagerState()
        val viewModel: WriteViewModel = viewModel()
        val uiState = viewModel.uiState
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
            }
        )
    }
}