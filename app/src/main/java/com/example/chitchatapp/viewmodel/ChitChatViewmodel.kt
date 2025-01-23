package com.example.chitchatapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.chitchatapp.data.UserData
import com.example.chitchatapp.others.handleException
import com.example.chitchatapp.repository.SignInRepository
import com.example.chitchatapp.repository.UpdateDataRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ChitChatViewmodel @Inject constructor(
    private val signInRepository: SignInRepository,
    private val authentication: FirebaseAuth,
    private val updateDataRepository: UpdateDataRepository
) : ViewModel() {

    // State to keep track of sign-in status
    val signIn = mutableStateOf(false)

    // Directly expose user data from the repository
    val userData: StateFlow<UserData?> = updateDataRepository.userData

    // Expose showProgressBar state from the repository
    val loginProgressBar: StateFlow<Boolean> = signInRepository.showProgressBar

    // Initialize the sign-in state and fetch user data if already authenticated
    init {
        val currentUser = authentication.currentUser
        signIn.value = currentUser != null
        if (signIn.value) {
            currentUser?.uid?.let { fetchUserData(it) }
        }
    }

    // Sign-up function that calls the repository and updates the sign-in state
    fun signUp(name: String, email: String, password: String, number: String) {
        signInRepository.signUp(name, email, password, number) { success, errorMessage ->
            if (success) {
                signIn.value = true
                fetchUserData(authentication.currentUser?.uid ?: "")
            } else {
                handleException(customMessage = errorMessage ?: "Unknown error occurred")
            }
        }
    }

    // Sign-in function that calls the repository and updates the sign-in state
    fun signIn(email: String, password: String) {
        signInRepository.signIn(email = email, password = password) { success, errorMessage ->
            if (success) {
                signIn.value = true
                fetchUserData(authentication.currentUser?.uid ?: "")
            } else {
                handleException(customMessage = errorMessage)
            }
        }
    }

    // Fetch user data from the repository
    private fun fetchUserData(uid: String) {
        updateDataRepository.getUserData(uid)
    }

    // Function to create or update the profile data
    fun createOrUpdateProfile(name: String, number: String, imageUrl: String? = null) {
        updateDataRepository.createOrUpdateProfile(name = name, number = number, imageUrl = imageUrl)
    }

    // Sign-out function that signs out the current user and clears user data
    fun signOut() {
        authentication.signOut()
        signIn.value = false
        updateDataRepository.userData.value = null // Clear cached user data
    }
}
