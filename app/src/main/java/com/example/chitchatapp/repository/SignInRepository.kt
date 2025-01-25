package com.example.chitchatapp.repository

import android.util.Log
import com.example.chitchatapp.data.USER_NODE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInRepository @Inject constructor(
    private val authentication: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val updateDataRepository: UpdateDataRepository
) {

    // State to control the progress bar visibility
    var showProgressBar = MutableStateFlow(false)

    // Sign-up method with validation and Firebase authentication
    suspend fun signUp(
        name: String,
        email: String,
        password: String,
        number: String,
        onResult: (Boolean, String?) -> Unit // Callback for success/failure
    ) {
        showProgressBar.value = true

        // Input validation for fields
        when {
            name.isEmpty() -> return onResult(false, "Name can't be empty")
            number.isEmpty() -> return onResult(false, "Number can't be empty")
            email.isEmpty() -> return onResult(false, "Email can't be empty")
            password.length < 6 -> return onResult(false, "Password length must be at least 6 characters")
        }

        try {
            // Move database and authentication calls to background thread
            val querySnapshot = withContext(Dispatchers.IO) {
                db.collection(USER_NODE).whereEqualTo("number", number).get().await()
            }

            if (!querySnapshot.isEmpty) {
                onResult(false, "Number already exists")
                showProgressBar.value = false
                return
            }

            // Proceed with user creation if the number doesn't exist
            val task = withContext(Dispatchers.IO) {
                authentication.createUserWithEmailAndPassword(email, password).await()
            }

            if (task.user != null) {
                // Create or update user profile after successful sign-up
                updateDataRepository.createOrUpdateProfile(name = name, number = number)
                onResult(true, null) // Success
            } else {
                onResult(false, "SignUp failed")
            }
        } catch (e: Exception) {
            onResult(false, "Error: ${e.message}")
        } finally {
            showProgressBar.value = false
        }
    }

    // Sign-in method with validation and Firebase authentication
    suspend fun signIn(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        showProgressBar.value = true

        // Check if the inputs are valid
        when {
            email.isEmpty() -> return onResult(false, "Email can't be empty")
            password.length < 6 -> return onResult(false, "Password length must be at least 6 characters")
        }

        try {
            // Firebase Authentication sign-in attempt off the main thread
            val task = withContext(Dispatchers.IO) {
                authentication.signInWithEmailAndPassword(email, password).await()
            }

            if (task.user != null) {
                // If sign-in is successful
                val userId = task.user?.uid
                if (userId != null) {
                    // Proceed with fetching user data if the user ID is available
                    updateDataRepository.getUserData(userId)
                    onResult(true, null)
                } else {
                    onResult(false, "User not found")
                }
            } else {
                onResult(false, "Error in logging you in")
            }
        } catch (e: Exception) {
            onResult(false, "Error: ${e.message}")
        } finally {
            showProgressBar.value = false
        }
    }
}
