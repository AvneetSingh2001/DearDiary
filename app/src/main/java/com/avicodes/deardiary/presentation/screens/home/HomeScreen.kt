package com.avicodes.deardiary.presentation.screens.home

import android.annotation.SuppressLint
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.avicodes.deardiary.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    onMenuClicked: () -> Unit,
    navigateToWrite: () -> Unit,
    onSignOutClicked: () -> Unit,
    drawerState: DrawerState,
) {
    NavigationDrawer(drawerState = drawerState, onSignOutClicked = onSignOutClicked) {
        Scaffold(
            topBar = {
                HomeTopBar(
                    onMenuClicked = onMenuClicked
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = navigateToWrite) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "New Diary Icon")
                }
            },
            content = {}
        )
    }
}

@Composable
fun NavigationDrawer(
    drawerState: DrawerState,
    onSignOutClicked: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
           ModalDrawerSheet(
               content = {
                   Box(
                       modifier = Modifier
                           .fillMaxWidth()
                           .height(250.dp),
                       contentAlignment = Alignment.Center,
                   ) {
                       Image(
                           painter = painterResource(id = R.drawable.app_logo),
                           contentDescription = "Logo Image",
                           modifier = Modifier.size(250.dp)
                       )
                   }

                   NavigationDrawerItem(label = {
                       Row(modifier = Modifier.padding(horizontal = 12.dp)) {
                           Image(
                               painter = painterResource(id = R.drawable.google_logo),
                               contentDescription = "Google Logo"
                           )
                           Spacer(modifier = Modifier.width(12.dp))
                           Text(text = "Sign Out", color = MaterialTheme.colorScheme.onSurface)
                       }
                   },
                       selected = false,
                       onClick = { onSignOutClicked })
               }
           )
        },
        content = content
    )
}