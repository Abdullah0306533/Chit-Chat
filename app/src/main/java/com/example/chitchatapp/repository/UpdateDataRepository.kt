package com.example.chitchatapp.repository

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.chitchatapp.data.USER_NODE
import com.example.chitchatapp.data.UserData
import com.example.chitchatapp.others.handleException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class UpdateDataRepository @Inject constructor(
    private val authentication: FirebaseAuth,
    private val db: FirebaseFirestore

) {

    var showProgressBar = MutableStateFlow(false)

    var userData = MutableStateFlow<UserData?>(null)

    init {
        val currentUser = authentication.currentUser
        currentUser?.uid?.let {
            getUserData(it)
        }
    }

    fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        password: String? = null,
        imageUrl: String? = null
    ) {
        if (name.isNullOrEmpty() or number.isNullOrEmpty()) {
            handleException(customMessage = "number or name can not be empty")
            return
        }
        val uid = authentication.currentUser?.uid
        if (uid == null) {
            handleException(
                customMessage = "User Not Authenticated \n" +
                        " try LogOut and then signIn again"
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

        userDocument.get().addOnSuccessListener { documentSnapShots ->
            val task = if (documentSnapShots.exists()) {
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

            task.addOnSuccessListener {
                getUserData(uid)
                showProgressBar.value = false
            }.addOnFailureListener {
                handleException(customMessage = "failed to update the profile")
                showProgressBar.value = false
            }

        }


    }

    fun getUserData(uid: String) {
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(customMessage = "Error retrieving user", exception = error)
                return@addSnapshotListener
            }
            if (value == null || !value.exists()) {
                Log.e("GetUserData", "No document found for user ID: $uid")
                handleException(customMessage = "User data not found")
                return@addSnapshotListener
            }

            val user = value.toObject<UserData>()
            if (user == null) {
                Log.e("GetUserData", "Failed to map Firestore data to UserData")
                handleException(customMessage = "Failed to parse user data")
            } else {
                userData.value = user
                Log.d("GetUserData", "Successfully retrieved and stored user data: ${userData.value!!.name}")
            }
        }
    }


}