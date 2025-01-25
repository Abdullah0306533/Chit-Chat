package com.example.chitchatapp.repository

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.chitchatapp.data.USER_NODE
import com.example.chitchatapp.data.UserData
import com.example.chitchatapp.others.handleException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateDataRepository @Inject constructor(
    private val authentication: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    var showProgressBar = MutableStateFlow(false)
    var userData = MutableStateFlow<UserData?>(null)

    suspend fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        password: String? = null,
        imageUrl: String? = null
    ) {
        if (name.isNullOrEmpty() || number.isNullOrEmpty()) {
            handleException(customMessage = "Number or name cannot be empty")
            return
        }

        val uid = authentication.currentUser?.uid
        if (uid == null) {
            handleException(
                customMessage = "User not authenticated. Try logging out and signing in again."
            )
            return
        }

        showProgressBar.value = true

        val updateUserData = UserData(
            userId = uid,
            name = name,
            userNumber = number,
            imageUrl = imageUrl ?: userData.value?.imageUrl
        )

        val userDocument = db.collection(USER_NODE).document(uid)

        try {
            // Perform Firestore operations on a background thread
            val documentSnapshot = withContext(Dispatchers.IO) {
                userDocument.get().await()
            }

            val task = if (documentSnapshot.exists()) {
                userDocument.update(
                    mapOf(
                        "name" to name,
                        "number" to number,
                        "imageUrl" to updateUserData.imageUrl
                    )
                )
            } else {
                userDocument.set(updateUserData)
            }

            // Wait for the task to finish
            task.await()

            // After success, get updated user data
            getUserData(uid)
            showProgressBar.value = false

        } catch (e: Exception) {
            handleException(customMessage = "Failed to update the profile", exception = e)
            showProgressBar.value = false
        }
    }

     suspend fun getUserData(uid: String):UserData? {
        try {
            val snapshot = withContext(Dispatchers.IO) {
                db.collection(USER_NODE).document(uid).get().await()
            }

            if (snapshot.exists()) {
                val user = snapshot.toObject<UserData>()
                if (user != null) {
                    userData.value = user
                    Log.d("GetUserData", "Successfully retrieved user data: ${userData.value!!.name}")
                    return user
                } else {
                    handleException(customMessage = "Failed to parse user data")
                }
            } else {
                handleException(customMessage = "User data not found")
            }
        } catch (e: Exception) {
            handleException(customMessage = "Error retrieving user", exception = e)
        }
         return null
    }
}
