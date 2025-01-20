package com.example.chitchatapp.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chitchatapp.R
import com.example.chitchatapp.activities.ScreenDestinations
import com.example.chitchatapp.others.CheckSignIn
import com.example.chitchatapp.others.CommonProgressBar
import com.example.chitchatapp.viewmodel.SignInViewmodel
@Composable
fun SignUpScreen(navController: NavController, vm: SignInViewmodel) {
    // State for input fields
    val nameState = remember { mutableStateOf(TextFieldValue()) }
    val numberState = remember { mutableStateOf(TextFieldValue()) }
    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }

    // Observe showProgressBar state from ViewModel
    val showProgressBar = vm.showProgressBar.collectAsState()
    CheckSignIn(navController,vm)

    // Outer Box to hold everything and apply a gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8F5E9), // Light green
                        Color(0xFF81C784) // Soft green
                    )
                )
            )
    ) {
        // Main Column for the UI elements
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Enables scrolling for small screens
                .padding(16.dp), // Padding around the content
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Chat icon
            Image(
                contentDescription = "Chat Icon",
                painter = painterResource(id = R.drawable.chat_icon),
                modifier = Modifier
                    .padding(top = 40.dp) // Add padding from top
                    .size(120.dp) // Size of the icon
            )

            // Title Text
            Text(
                text = "Create Your Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = Color(0xFF1B5E20), // Green color
                modifier = Modifier.padding(top = 16.dp) // Add spacing from the top
            )

            // Spacer to add some space between title and form fields
            Spacer(modifier = Modifier.height(24.dp))

            // Elevated Card for the form
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth() // Make the card fill the width
                    .wrapContentHeight(), // The height will wrap around the content
                shape = RoundedCornerShape(12.dp), // Rounded corners
                elevation = androidx.compose.material3.CardDefaults.cardElevation(8.dp) // Shadow effect for elevation
            ) {
                // Column for the form fields inside the card
                Column(
                    modifier = Modifier
                        .fillMaxWidth() // Fill the width of the parent container
                        .padding(16.dp) // Padding inside the card
                ) {
                    // Name input field
                    OutlinedTextField(
                        value = nameState.value,
                        onValueChange = { nameState.value = it },
                        label = { Text("Full Name") },
                        singleLine = true, // Restrict to a single line input
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp), // Bottom padding between fields
                        shape = RoundedCornerShape(8.dp), // Rounded corners for the input field
                        keyboardOptions = KeyboardOptions.Default
                    )
                    // Phone number input field
                    OutlinedTextField(
                        value = numberState.value,
                        onValueChange = { numberState.value = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    // Email input field
                    OutlinedTextField(
                        value = emailState.value,
                        onValueChange = { emailState.value = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    // Password input field
                    OutlinedTextField(
                        value = passwordState.value,
                        onValueChange = { passwordState.value = it },
                        label = { Text("Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }
            }

            // Sign Up Button
            Button(
                onClick = {
                    vm.signUp(
                        name = nameState.value.text,
                        email = emailState.value.text,
                        number = numberState.value.text,
                        password = passwordState.value.text
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF388E3C), // Green button color
                    contentColor = Color.White // White text color
                ),
                shape = RoundedCornerShape(8.dp), // Rounded corners for the button
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Button width adjusted to 80% of the screen
                    .padding(top = 32.dp) // Add padding above the button
            ) {
                Text("Sign Up", fontSize = 18.sp, fontWeight = FontWeight.Bold) // Button text
            }

            // Text Button for navigation to Login screen
            TextButton(
                onClick = { navController.navigate(ScreenDestinations.SignIn.route) },
                modifier = Modifier.padding(top = 16.dp) // Add padding above the button

            ) {
                Text(
                    text = "Already have an account? Log In",
                    color = Color(0xFF1B5E20), // Green text color for the link
                    fontSize = 16.sp
                )
            }
        }
    }

    // Show progress bar if required
    if (showProgressBar.value) {
        CommonProgressBar() // Custom progress bar while loading
    }
}
