package com.example.chitchatapp.repository

import android.util.Log
import androidx.core.text.isDigitsOnly
import com.example.chitchatapp.data.*
import com.example.chitchatapp.others.handleException
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val updateDataRepository: UpdateDataRepository
) {
    val userData = updateDataRepository.userData
    var chats = MutableStateFlow<List<ChatData>>(emptyList())
    var chatsProgressBar = MutableStateFlow(false)

    /**
     * Handles the addition of a new chat with the provided number.
     */
    fun onAddChat(number: String) {
        if (number.isBlank() || !number.isDigitsOnly()) {
            handleException(customMessage = "Invalid number. Please enter a valid phone number consisting of digits only.")
            return
        }

        val currentUser = userData.value ?: run {
            handleException(customMessage = "User data is unavailable. Please log in again.")
            return
        }

        chatsProgressBar.value = true // Show progress bar

        // Check if a chat already exists with this number
        db.collection(CHATS).where(
            Filter.or(
                Filter.and(
                    Filter.equalTo("user1.userNumber", number),
                    Filter.equalTo("user2.userNumber", currentUser.userNumber)
                ),
                Filter.and(
                    Filter.equalTo("user2.userNumber", number),
                    Filter.equalTo("user1.userNumber", currentUser.userNumber)
                )
            )
        ).get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                // Chat already exists
                handleException(customMessage = "Chat already exists with this user.")
                chatsProgressBar.value = false // Hide progress bar
                return@addOnSuccessListener
            }

            // Check if the user exists
            db.collection(USER_NODE).whereEqualTo("userNumber", number).get()
                .addOnSuccessListener { userSnapshot ->
                    if (userSnapshot.isEmpty) {
                        handleException(customMessage = "This number is not registered in our system.")
                        chatsProgressBar.value = false // Hide progress bar
                        return@addOnSuccessListener
                    }

                    // Get chat partner details
                    val chatPartner = userSnapshot.toObjects<UserData>().firstOrNull()
                    if (chatPartner == null) {
                        handleException(customMessage = "Failed to fetch the chat partner's data.")
                        chatsProgressBar.value = false // Hide progress bar
                        return@addOnSuccessListener
                    }

                    val chatId = db.collection(CHATS).document().id
                    val chatData = ChatData(
                        chatId = chatId,
                        user1 = ChatUser(
                            userId = currentUser.userId,
                            userName = currentUser.name,
                            image = currentUser.imageUrl,
                            number = currentUser.userNumber
                        ),
                        user2 = ChatUser(
                            userId = chatPartner.userId,
                            userName = chatPartner.name,
                            image = chatPartner.imageUrl,
                            number = chatPartner.userNumber
                        )
                    )

                    // Store chat in Firestore
                    db.collection(CHATS).document(chatId).set(chatData)
                        .addOnSuccessListener {
                            Log.d("ChatRepository", "Chat created successfully.")
                            populateChats() // Refresh chat list
                        }
                        .addOnFailureListener { exception ->
                            handleException(exception, "Failed to create chat. Please try again.")
                            chatsProgressBar.value = false // Hide progress bar
                        }
                }
                .addOnFailureListener { exception ->
                    handleException(exception, "Failed to verify the provided number. Please try again.")
                    chatsProgressBar.value = false // Hide progress bar
                }
        }.addOnFailureListener { exception ->
            handleException(exception, "Failed to check existing chats. Please try again.")
            chatsProgressBar.value = false // Hide progress bar
        }
    }

    fun populateChats() {
        chatsProgressBar.value = true // Show progress bar
        val currentUser = userData.value ?: run {
            handleException(customMessage = "User data is unavailable. Unable to fetch chats.")
            chatsProgressBar.value = false // Hide progress bar
            return
        }

        // Fetch chats only for the current user
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", currentUser.userId),
                Filter.equalTo("user2.userId", currentUser.userId)
            )
        ).addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleException(error, "Failed to load chats. Please try again.")
                chatsProgressBar.value = false // Hide progress bar
                return@addSnapshotListener
            }

            // Get the updated list of chats
            val chatList = snapshot?.toObjects<ChatData>()?.map { chat ->
                val otherUser = if (chat.user1.userId == currentUser.userId) {
                    chat.user2 // The other participant if the current user is user1
                } else {
                    chat.user1 // The other participant if the current user is user2
                }

                ChatData(
                    chatId = chat.chatId,
                    user1 = chat.user1,
                    user2 = chat.user2,
                )
            } ?: emptyList()

            // Update the state to reflect the current chat list
            Log.d("ChatRepository", "Chats updated: ${chatList.size} chats")
            chats.value = chatList
            chatsProgressBar.value = false // Hide progress bar
        }
    }

    fun signOut() {
        chats.value = emptyList()
        Log.d("Tag", "list")
        updateDataRepository.userData.value = null // Reset user data
    }

    fun signIn(newUser: UserData) {
        // Update user data with the new user's information
        updateDataRepository.userData.value = newUser

        // Fetch and populate chats for the new user
        populateChats()
    }
}
