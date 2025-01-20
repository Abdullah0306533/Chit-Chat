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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.chitchatapp.ui.theme.ChitChatAppTheme
import com.example.chitchatapp.viewmodel.SignInViewmodel
import dagger.hilt.android.AndroidEntryPoint
import com.example.chitchatapp.screens.ChatListScreen
import com.example.chitchatapp.screens.SignInScreen
import com.example.chitchatapp.screens.SignUpScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.composable

// Sealed class for managing screen destinations and routes
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

    // Composable function to handle navigation logic
    @Composable
    fun ChatAppNavigation() {
        // Create a NavController to handle navigation between screens
        val navController = rememberNavController()

        // Obtain the SignInViewModel to manage sign-in state
        val vm = hiltViewModel<SignInViewmodel>()

        // Determine the start destination based on the sign-in state
        val startDestination = if (vm.signIn.value) {
            ScreenDestinations.ChatList.route // User is signed in
        } else {
            ScreenDestinations.SignIn.route // User is not signed in
        }

        // Set up the navigation host with composable routes
        NavHost(navController = navController, startDestination = startDestination) {
            composable(ScreenDestinations.SignUp.route) {
                // Navigate to the SignUp screen
                SignUpScreen(navController, vm)
            }
            composable(ScreenDestinations.SignIn.route) {
                // Navigate to the SignIn screen
                SignInScreen(navController, vm)
            }
            composable(ScreenDestinations.ChatList.route) {
                // Navigate to the ChatList screen
                ChatListScreen(navController, vm)
            }
        }
    }
}
