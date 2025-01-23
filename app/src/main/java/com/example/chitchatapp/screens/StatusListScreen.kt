package com.example.chitchatapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.chitchatapp.viewmodel.ChitChatViewmodel

@Composable
fun StatusListScreen (navController: NavController,vm:ChitChatViewmodel){


    NavigationBar(navController,BottomNavigationItems.STATUS)

}