package com.example.chitchatapp.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.chitchatapp.ui.theme.ChitChatAppTheme
import com.example.chitchatapp.viewmodel.ChitChatViewmodel
import dagger.hilt.android.AndroidEntryPoint
import com.example.chitchatapp.screens.ChatListScreen
import com.example.chitchatapp.screens.SignInScreen
import com.example.chitchatapp.screens.SignUpScreen
import androidx.navigation.compose.composable
import androidx.compose.animation.* // For the animation transitions
import com.example.chitchatapp.screens.ProfileScreen
import com.example.chitchatapp.screens.StatusListScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost

sealed class ScreenDestinations(val route: String) {
    object SignUp : ScreenDestinations(route = "signUp")
    object SignIn : ScreenDestinations(route = "signIn")
    object Profile : ScreenDestinations(route = "profile")
    object ChatList : ScreenDestinations(route = "chatList")
    object StatusList : ScreenDestinations(route = "statusList")

    // Single chat route with dynamic chat ID
    object SingleChat : ScreenDestinations(route = "singleChat/{chatId}") {
        fun createRoute(id: String) = "singleChat/$id"
    }

    // Single status route with dynamic status ID
    object SingleStatus : ScreenDestinations(route = "singleStatus") {
        fun createRoute(id: String) = "singleStatus/$id"
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Set up the content of the activity using Jetpack Compose
        setContent {
            ChitChatAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Set up navigation between screens
                    ChatAppNavigation()
                }
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun ChatAppNavigation() {
        val navController = rememberNavController()
        val vm = hiltViewModel<ChitChatViewmodel>()

        // Determine the start destination based on sign-in state
        val startDestination = if (vm.signIn.value) {
            ScreenDestinations.ChatList.route
        } else {
            ScreenDestinations.SignIn.route
        }

        // Set up AnimatedNavHost
        AnimatedNavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            // SignUp screen with transition
            composable(
                route = ScreenDestinations.SignUp.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) {
                SignUpScreen(navController, vm)
            }

            // SignIn screen with transition
            composable(
                route = ScreenDestinations.SignIn.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) {
                SignInScreen(navController, vm)
            }

            // ChatList screen with transition
            composable(
                route = ScreenDestinations.ChatList.route,
                enterTransition = { slideInVertically(initialOffsetY = { 1000 }) + fadeIn() },
                exitTransition = { slideOutVertically(targetOffsetY = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInVertically(initialOffsetY = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutVertically(targetOffsetY = { 1000 }) + fadeOut() }
            ) {
                ChatListScreen(navController, vm)
            }
            composable(
                route = ScreenDestinations.StatusList.route,
                enterTransition = { slideInVertically(initialOffsetY = { 1000 }) + fadeIn() },
                exitTransition = { slideOutVertically(targetOffsetY = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInVertically(initialOffsetY = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutVertically(targetOffsetY = { 1000 }) + fadeOut() }
            ) {
                StatusListScreen(navController, vm)
            }
            composable(
                route = ScreenDestinations.Profile.route,
                enterTransition = { slideInVertically(initialOffsetY = { 1000 }) + fadeIn() },
                exitTransition = { slideOutVertically(targetOffsetY = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInVertically(initialOffsetY = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutVertically(targetOffsetY = { 1000 }) + fadeOut() }
            ) {
                ProfileScreen(navController, vm)
            }

        }
    }
}
