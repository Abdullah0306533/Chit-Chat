package com.example.chitchatapp.screens



import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.chitchatapp.screens.*
import androidx.navigation.NavController
//import com.example.chitchatapp.others.CheckSignInAndNavigate
import com.example.chitchatapp.viewmodel.ChitChatViewmodel
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ChatListScreen(navController: NavController, vm: ChitChatViewmodel) {
    //CheckSignInAndNavigate(navController,vm)
    val userData by vm.userData.collectAsState()

    if (userData != null) {
        Text(text = "User: ${userData?.name}")
    } else {
        Text(text = "Loading user data...")
    }
}
    //NavigationBar(navController,BottomNavigationItems.CHAT_LIST)


