package com.example.chitchatapp.repository


import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.chitchatapp.data.USER_NODE
import com.example.chitchatapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class SignInRepository @Inject constructor(
    private val authentication: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val updateDataRepository: UpdateDataRepository
) {

    // State to control the progress bar visibility
    var showProgressBar = MutableStateFlow(false)


    // Sign-up method with validation and Firebase authentication
    fun signUp(
        name: String,
        email: String,
        password: String,
        number: String,
        onResult: (Boolean, String?) -> Unit // Callback for success/failure
    ) {
        showProgressBar.value = true

        // Input validation for fields
        when {
            name.isEmpty() -> {
                onResult(false, "Name can't be empty")
                showProgressBar.value = false
                return
            }
            number.isEmpty() -> {
                onResult(false, "Number can't be empty")
                showProgressBar.value = false
                return
            }
            email.isEmpty() -> {
                onResult(false, "Email can't be empty")
                showProgressBar.value = false
                return
            }
            password.length < 6 -> {
                onResult(false, "Password length must be at least 6 characters")
                showProgressBar.value = false
                return
            }
        }

        // Checking if the number already exists in the database
        db.collection(USER_NODE).whereEqualTo("number", number).get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Number already exists
                    onResult(false, "Number already exists")
                    showProgressBar.value = false
                    return@addOnSuccessListener
                }

                // Proceed with user creation if the number doesn't exist
                authentication.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Create or update user profile after successful sign-up
                            updateDataRepository.createOrUpdateProfile(name = name, number = number)
                            onResult(true, null) // Success
                        } else {
                            onResult(false, "SignUp failed: ${task.exception?.message}")
                        }
                        showProgressBar.value = false
                    }
            }
            .addOnFailureListener { exception ->
                onResult(false, "Error checking number: ${exception.message}")
                showProgressBar.value = false
            }
    }
    fun signIn(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        // Show the progress bar when the sign-in process starts
        showProgressBar.value = true

        // Check if the inputs are valid
        when {
            email.isEmpty() -> {
                // Invalid email
                onResult(false, "Email can't be empty")
                showProgressBar.value = false
                return
            }
            password.length < 6 -> {
                // Invalid password length
                onResult(false, "Password length must be at least 6 characters")
                showProgressBar.value = false
                return
            }
        }

        // Firebase Authentication sign-in attempt
        authentication.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            // Ensure the task is completed on the main thread for UI updates
            if (task.isSuccessful) {
                // If sign-in is successful
                val userId = authentication.currentUser?.uid
                if (userId != null) {
                    // Proceed with fetching user data if the user ID is available
                    updateDataRepository.getUserData(userId)
                    // Notify success
                    onResult(true, null)
                } else {
                    // If no user ID is found, inform the user about the issue
                    onResult(false, "User not found")
                }
            } else {
                // If sign-in failed, notify the user with an appropriate error message
                onResult(false, "Error in logging you in: ${task.exception?.message}")
            }
            // Hide the progress bar after the task is completed
            showProgressBar.value = false
        }
    }
}
