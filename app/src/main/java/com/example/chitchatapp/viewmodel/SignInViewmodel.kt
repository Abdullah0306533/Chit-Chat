package com.example.chitchatapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.chitchatapp.others.handleException
import com.example.chitchatapp.repository.SignInRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SignInViewmodel @Inject constructor(
    private val repository: SignInRepository,
    private val authentication: FirebaseAuth
) : ViewModel() {

    // State to keep track of sign-in status
    val signIn = mutableStateOf(false)

    // Initialize the sign-in state based on the current user
    init {
        val currentUser = authentication.currentUser
        signIn.value = currentUser != null
        // You can retrieve user data here if needed (commented out for now)
        // currentUser?.uid?.let { getUserData(it) }
    }

    // Expose showProgressBar state from the repository
    var showProgressBar: StateFlow<Boolean> = repository.showProgressBar

    // Sign-up function that calls the repository and updates the sign-in state
    fun signUp(name: String, email: String, password: String, number: String) {
        repository.signUp(name, email, password, number) { success, errorMessage ->
            if (success) {
                signIn.value = true // Update sign-in state upon successful sign-up
            } else {
                handleException(customMessage = errorMessage ?: "Unknown error occurred")
            }
        }
    }

    fun signIn(email: String, password: String) {
        repository.signIn(email = email, password = password,){
            sucess,errorMessage ->
            if (sucess){
                signIn.value = true // Update sign-in state upon successful sign-in
            }
            else{
                handleException(customMessage = errorMessage)
            }
        }
    }


    // Sign-out function that signs out the current user
    fun signOut() {
        authentication.signOut()
    }
}
