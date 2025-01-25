package com.example.chitchatapp.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chitchatapp.data.ChatData
import com.example.chitchatapp.data.UserData
import com.example.chitchatapp.others.handleException
import com.example.chitchatapp.repository.ChatRepository
import com.example.chitchatapp.repository.SignInRepository
import com.example.chitchatapp.repository.UpdateDataRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChitChatViewmodel @Inject constructor(
    private val signInRepository: SignInRepository,
    private val authentication: FirebaseAuth,
    private val updateDataRepository: UpdateDataRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    // State to keep track of sign-in status
    val signIn = mutableStateOf(false)

    // Directly expose user data from the repository
    val userData: StateFlow<UserData?> = updateDataRepository.userData

    // Expose showProgressBar state from the repository
    val loginProgressBar: StateFlow<Boolean> = signInRepository.showProgressBar
    var chatProgressBar:StateFlow<Boolean> = chatRepository.chatsProgressBar
    val chatList: StateFlow<List<ChatData>> = chatRepository.chats

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
        viewModelScope.launch {
            signInRepository.signUp(name, email, password, number) { success, errorMessage ->
                if (success) {
                    signIn.value = true
                    fetchUserData(authentication.currentUser?.uid ?: "")
                } else {
                    handleException(customMessage = errorMessage ?: "Unknown error occurred")
                }
            }
        }
    }

    // Sign-in function that calls the repository and updates the sign-in state
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            signInRepository.signIn(email = email, password = password) { success, errorMessage ->
                if (success) {
                    signIn.value = true
                    fetchUserData(authentication.currentUser?.uid ?: "")
                } else {
                    handleException(customMessage = errorMessage)
                }
            }
        }
    }

    // Fetch user data from the repository
    private fun fetchUserData(uid: String){
        viewModelScope.launch {
            try {
                updateDataRepository.getUserData(uid)
                Log.d("ChitChatViewmodel", "User data successfully loaded.")
            } catch (e: Exception) {
                handleException(customMessage = "Error loading user data: ${e.message}")
            }
        }
    }

    // Function to create or update the profile data
    fun createOrUpdateProfile(name: String? = null, number: String? = null, imageUrl: String? = null) {
        viewModelScope.launch {
            try {
                updateDataRepository.createOrUpdateProfile(name = name, number = number, imageUrl = imageUrl)
                Log.d("ChitChatViewmodel", "Profile updated successfully.")
            } catch (e: Exception) {
                handleException(customMessage = "Error updating profile: ${e.message}")
            }
        }
    }

    // Sign-out function that signs out the current user and clears user data
    fun signOut() {
        authentication.signOut()
        chatRepository.signOut()
        signIn.value = false
        updateDataRepository.userData.value = null // Clear cached user data
    }

    // Load user data in case it's needed explicitly
    fun loadUserData() {
        viewModelScope.launch {
            try {
                val currentUser = authentication.currentUser
                if (currentUser != null) {
                    fetchUserData(currentUser.uid)
                    Log.d("ChitChatViewmodel", "User data successfully loaded.")
                } else {
                    Log.d("ChitChatViewmodel", "No authenticated user found.")
                }
            } catch (e: Exception) {
                handleException(customMessage = "Error loading user data: ${e.message}")
            }
        }
    }

    fun addChat(chatNumber: String) {
        chatRepository.onAddChat(number = chatNumber)

    }
    fun populateChats(){
        chatRepository.populateChats()

    }
}
