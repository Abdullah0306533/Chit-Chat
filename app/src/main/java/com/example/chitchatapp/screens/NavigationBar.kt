package com.example.chitchatapp.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.chitchatapp.R
import com.example.chitchatapp.activities.ScreenDestinations
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.composable
import androidx.compose.animation.*
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.example.chitchatapp.viewmodel.ChitChatViewmodel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

// Enum for bottom navigation items
enum class BottomNavigationItems(
    val resource: Int,
    val screenDestination: ScreenDestinations,
    val description: String
) {
    CHAT_LIST(R.drawable.chat_icon, ScreenDestinations.ChatList, "Chat"),
    STATUS(R.drawable.status, ScreenDestinations.StatusList, "Status"),
    PROFILE(R.drawable.profile, ScreenDestinations.Profile, "Profile")
}

@Composable
fun NavigationBar(navController: NavController, selectedItem: BottomNavigationItems) {
    NavigationBar(
        containerColor = Color(0xFFF5F5F5),
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        BottomNavigationItems.values().forEach { item ->
            val selected = item == selectedItem
            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(item.resource),
                            contentDescription = item.description,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                label = {
                    Text(
                        item.description,
                        fontSize = 12.sp,
                        color = if (selected) Color(0xFF000000) else Color(0xFF757575)
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(item.screenDestination.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}


