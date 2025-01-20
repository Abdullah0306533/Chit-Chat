package com.example.chitchatapp.others

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.chitchatapp.R
import com.example.chitchatapp.activities.ScreenDestinations
import com.example.chitchatapp.viewmodel.SignInViewmodel

// Displays a progress bar with Lottie animation
@Composable
fun CommonProgressBar() {
    Row(
        modifier = Modifier
            .alpha(.5f)
            .background(Color.LightGray)
            .clickable(enabled = false) { }
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.progress_bar_animation)
        )
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever
        )
        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier.size(100.dp)
        )
    }
}

// Logs exceptions and custom messages
fun handleException(exception: Exception? = null, customMessage: String? = "") {
    exception?.printStackTrace()
    val errorMsg = exception?.localizedMessage ?: ""
    val message = if (errorMsg.isEmpty()) errorMsg else customMessage
    Log.d("", "Exception occurred in Chit Chat App $customMessage", exception)
}

// Checks sign-in status and navigates accordingly
@Composable
fun CheckSignIn(navController: NavController, vm: SignInViewmodel) {
    val alreadySignedIn = remember { mutableStateOf(false) }
    val signedIn = vm.signIn.value

    LaunchedEffect(signedIn) {
        if (signedIn && !alreadySignedIn.value) {
            alreadySignedIn.value = true
            navController.navigate(ScreenDestinations.ChatList.route) {
                popUpTo(0) // Clears the back stack
            }
        }
    }
}
