package com.example.chitchatapp.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.example.chitchatapp.R
import com.example.chitchatapp.data.ChatData
import com.example.chitchatapp.others.CommonProgressBar
import com.example.chitchatapp.viewmodel.ChitChatViewmodel
import kotlinx.coroutines.delay

@Composable
fun ChatListScreen(navController: NavController, vm: ChitChatViewmodel) {
    val showDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {

        delay(700)
        vm.populateChats()
    }

    val chatList by vm.chatList.collectAsState()
    var progressBar=vm.chatProgressBar.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(navController, BottomNavigationItems.CHAT_LIST)
        },
        floatingActionButton = {
            Fab(onFabClick = { showDialog.value = true
                val x=if(chatList.isEmpty()) "False" else "true"
                Log.d("LL",x)

            })
        }
    ) { innerPadding ->
        ChatListContent(
            modifier = Modifier.padding(innerPadding),
            chatList = chatList.reversed(),
            onAddChat = { chatNumber -> vm.addChat(chatNumber) },
            onStartChat = { showDialog.value = true }
        )

        if (showDialog.value) {
            AddChatDialog(
                onDismiss = { showDialog.value = false },
                onAddChat = { chatNumber ->
                    vm.addChat(chatNumber)
                    showDialog.value = false
                }
            )
        }
    }

    if (progressBar.value){
        CommonProgressBar()
    }
}

@Composable
fun ChatListContent(
    modifier: Modifier = Modifier,
    chatList: List<ChatData>,
    onAddChat: (String) -> Unit,
    onStartChat: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        if (chatList.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chatList) { chat ->
                    ChatItem(chat = chat)
                }
            }
        } else {
            EmptyStateContent(onStartChat = onStartChat)

        }
    }
}

@Composable
fun ChatItem(chat: ChatData) {
    //pending functionality
    val isUnread = 1 > 0 // Assuming `unreadMessages` is a property in `ChatData`

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isUnread) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(if (isUnread) 8.dp else 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Picture Placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile Picture",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Chat with: ${chat.user2.userName}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = chat.user2.number ?: "No messages yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Last message: 2:30 PM", // Replace with actual timestamp
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Unread Message Badge
            if (isUnread) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
fun EmptyStateContent(onStartChat: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty_chat_animation))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever
        )

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Start your first chat!",
            fontSize = 20.sp,
            fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onStartChat,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Start Chat")
        }
    }
}

@Composable
fun AddChatDialog(onDismiss: () -> Unit, onAddChat: (String) -> Unit) {
    var chatNumber by remember { mutableStateOf("") }
    val isInputValid = chatNumber.isNotBlank()
    var showError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Add New Chat",
                    fontSize = 22.sp,
                    fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = chatNumber,
                    onValueChange = { chatNumber = it },
                    label = { Text("Enter Chat Number") },
                    placeholder = { Text("e.g., 12345") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = showError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        errorBorderColor = MaterialTheme.colorScheme.error
                    )
                )

                if (showError) {
                    Text(
                        text = "Please enter a valid chat number",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (isInputValid) {
                                onAddChat(chatNumber)
                            } else {
                                showError = true
                            }
                        },
                        enabled = isInputValid,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@Composable
fun Fab(onFabClick: () -> Unit) {
    FloatingActionButton(
        onClick = onFabClick,
        containerColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(16.dp),
        elevation = FloatingActionButtonDefaults.elevation(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add Chat",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
