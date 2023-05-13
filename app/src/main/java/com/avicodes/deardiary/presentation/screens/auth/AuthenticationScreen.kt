package com.avicodes.deardiary.presentation.screens.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.avicodes.deardiary.utils.Constants.CLIENT_ID
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.MessageBarState
import com.stevdzasan.onetap.OneTapSignInState
import com.stevdzasan.onetap.OneTapSignInWithGoogle
import java.lang.Exception


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AuthenticationScreen(
    loadingState: Boolean,
    messageBarState: MessageBarState,
    oneTapState: OneTapSignInState,
    onButtonClicked: () -> Unit,
    onTokenIdReceived: (String) -> Unit,
    onDialogDismissed: (String) -> Unit,
    authenticated: Boolean = false,
    navigateToHome: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        content = {
            ContentWithMessageBar(messageBarState = messageBarState) {
                AuthenticationContent(
                    loadingState = loadingState,
                    onButtonClicked = onButtonClicked
                )
            }
        }
    )

    OneTapSignInWithGoogle(
        state = oneTapState,
        clientId = CLIENT_ID,
        onTokenIdReceived = { tokenId ->
            onTokenIdReceived(tokenId)
        },
        onDialogDismissed = { message ->
            onDialogDismissed(message)
        }
    )

    LaunchedEffect(key1 = authenticated) {
        if (authenticated) {
            navigateToHome()
        }
    }
}

