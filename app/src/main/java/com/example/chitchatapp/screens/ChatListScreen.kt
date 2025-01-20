package com.example.chitchatapp.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
//import com.example.chitchatapp.others.CheckSignInAndNavigate
import com.example.chitchatapp.viewmodel.SignInViewmodel
@Composable
fun ChatListScreen(navController: NavController, vm: SignInViewmodel) {
    //CheckSignInAndNavigate(navController,vm)
    Text(
        text = "Create Account",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Serif,
        color = Color(0xFF2E7D32) // Green shade
    )
    Column() {
        Button(onClick = {vm.signOut()}) { }
    }


}