package com.avicodes.deardiary.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.avicodes.deardiary.data.repository.MongoDB
import com.avicodes.deardiary.model.Mood
import com.avicodes.deardiary.presentation.components.DisplayAlertDialog
import com.avicodes.deardiary.presentation.screens.auth.AuthenticationScreen
import com.avicodes.deardiary.presentation.screens.auth.AuthenticationViewModel
import com.avicodes.deardiary.presentation.screens.home.HomeScreen
import com.avicodes.deardiary.presentation.screens.home.HomeViewModel
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
import kotlinx.coroutines.withContext
import java.lang.Exception

@Composable
fun SetUpNavGraph(
    startDestination: String,
    navController: NavHostController,
    onDataLoaded: () -> Unit
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
            navigationToAuth = {
                navController.popBackStack()
                navController.navigate(Screen.Authentication.route)
            },
            onDataLoaded = onDataLoaded,
            navigateToWriteWithArgs = {
                navController.navigate(Screen.Write.passDiaryId(diaryId = it))
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
    composable(route = Screen.Authentication.route) {
        val messageBarState = rememberMessageBarState()
        val oneTapState = rememberOneTapSignInState()
        val viewModel: AuthenticationViewModel = viewModel()
        val loadingState by viewModel.loadingState
        val authenticated by viewModel.authenticated

        LaunchedEffect(key1 = Unit) {
            onDataLoaded()
        }

        AuthenticationScreen(
            oneTapState = oneTapState,
            loadingState = loadingState,
            messageBarState = messageBarState,
            onButtonClicked = {
                oneTapState.open()
                viewModel.setLoadingState(loading = true)
            },
            onTokenIdReceived = {
                viewModel.signInMongoAtlas(
                    tokenId = it,
                    onSuccess = {
                        messageBarState.addSuccess("Success")
                        viewModel.setLoadingState(false)
                    },
                    onError = {
                        messageBarState.addError(Exception(it))
                        viewModel.setLoadingState(false)
                    }
                )
            },
            onDialogDismissed = {
                messageBarState.addError(Exception(it))
                viewModel.setLoadingState(false)
            },
            authenticated = authenticated,
            navigateToHome = {
                navigateToHome()
            }
        )
    }
}

fun NavGraphBuilder.homeRoute(
    navigateToWrite: () -> Unit,
    navigateToWriteWithArgs: (String) -> Unit,
    navigationToAuth: () -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(route = Screen.Home.route) {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val viewModel: HomeViewModel = viewModel()
        val diaries by viewModel.diaries

        var signOutDialogOpened by remember {
            mutableStateOf(false)
        }

        LaunchedEffect(key1 = diaries) {
            if (diaries !is RequestState.Loading) {
                onDataLoaded()
            }
        }
        val scope = rememberCoroutineScope()
        HomeScreen(
            navigateToWriteWithArgs = navigateToWriteWithArgs,
            diaries = diaries,
            onMenuClicked = {
                scope.launch {
                    drawerState.open()
                }
            },
            navigateToWrite = navigateToWrite,
            drawerState = drawerState,
            onSignOutClicked = {
                signOutDialogOpened = true
            }
        )

        LaunchedEffect(key1 = Unit) {
            MongoDB.configureTheRealm()
        }

        DisplayAlertDialog(
            title = "Sign Out",
            message = "Are you sure you want to sign out of from your google account",
            dialogOpened = signOutDialogOpened,
            onCloseDialog = {
                signOutDialogOpened = false
            },
            onYesClicked = {
                scope.launch(Dispatchers.IO) {
                    val user = App.create(APP_ID).currentUser
                    user?.logOut()
                    withContext(Dispatchers.Main) {
                        navigationToAuth()
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
        val viewModel: WriteViewModel = viewModel()
        val uiState = viewModel.uiState
        val pagerState = rememberPagerState()
        val pageNumber by remember {
            derivedStateOf {
                pagerState.currentPage
            }
        }

        WriteScreen(
            uiState = uiState,
            pagerState = pagerState,
            onBackPressed = onBackPressed,
            onDeleteConfirmed = {},
            onDescriptionChanged = {
                viewModel.setDescription(description = it)
            },
            onTitleChanged = {
                viewModel.setTitle(title = it)
            },
            moodName = { Mood.values()[pageNumber].name }
        )
    }
}
