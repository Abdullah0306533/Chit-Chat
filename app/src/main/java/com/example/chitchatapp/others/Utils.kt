package com.example.chitchatapp.others

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.chitchatapp.R
import com.example.chitchatapp.activities.ScreenDestinations
import com.example.chitchatapp.screens.ChatListScreen
import com.example.chitchatapp.screens.ProfileScreen
import com.example.chitchatapp.screens.StatusListScreen
import com.example.chitchatapp.viewmodel.ChitChatViewmodel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID


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
    Log.d("TAG", "Exception occurred in Chit Chat App $customMessage", exception)
}

// Checks sign-in status and navigates accordingly
@Composable
fun CheckSignIn(navController: NavController, vm: ChitChatViewmodel) {
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

@Composable
fun rememberImagePicker(onImageSelect: (Uri?) -> Unit): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelect(uri)
    }
    return { launcher.launch("image/*") }
}

suspend fun uploadImageToFirebase(uri: Uri): String? {
    return try {
        // Create a unique file name for the image
        val fileName = "images/${UUID.randomUUID()}.jpg"

        // Get a reference to Firebase Storage
        val storageReference = FirebaseStorage.getInstance().reference.child(fileName)

        // Upload the image
        val uploadTask = storageReference.putFile(uri).await()

        // Get the image download URL
        storageReference.downloadUrl.await().toString()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun createClickHandler(
    exitButton: Boolean = false,
    saveButton: Boolean = false,
    firstAction: (() -> Unit)? = null,
    secondAction: (() -> Unit)? = null
): () -> Unit {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp,end=8.dp), horizontalAlignment = Alignment.Start) {
        if (exitButton) {
            androidx.compose.material3.IconButton(
                onClick = { firstAction?.invoke() }
            ) {
                Icon(modifier = Modifier.size(50.dp),
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Exit"
                )
            }
        }
    }
    Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp,end=8.dp), horizontalAlignment = Alignment.End) {
        if (saveButton) {
            TextButton(
                onClick = { secondAction?.invoke() },
                modifier = Modifier.padding(start = 8.dp).background(Color.Transparent)
            ) {
                Text(text = "Save", fontSize = 18.sp)
            }
        }
    }

    // Return the combined lambda
    return {
        firstAction?.invoke()
        secondAction?.invoke()
    }
}




