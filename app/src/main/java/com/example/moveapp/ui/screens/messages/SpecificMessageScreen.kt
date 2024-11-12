package com.example.moveapp.ui.screens.messages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.data.ChatData
import com.example.moveapp.data.MessageData
import com.example.moveapp.repository.ChatRepo
import com.example.moveapp.ui.composables.MessageItem
import com.example.moveapp.utility.FireAuthService
import com.example.moveapp.utility.FirebaseRealtimeService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
@Composable
fun SpecificMessageScreen(navController: NavController, chatId: String) {
    val chat = remember { mutableStateOf<ChatData?>(null) }
    val messageText = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val currentUserId = FireAuthService.getUserId()

    LaunchedEffect(chatId) {
        scope.launch {
            chat.value = ChatRepo.getChatById(chatId)
            Log.d("ChatDebug", "ChatData hentet: ${chat.value}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (chat.value == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            val currentChat = chat.value!!
            val sortedMessages = currentChat.messages.values.sortedBy { it.messageTimestamp }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortedMessages.size) { index ->
                    val message = sortedMessages[index]
                    MessageItem(message, currentUserId == message.senderId)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText.value,
                    onValueChange = { messageText.value = it },
                    label = { Text(text = "Type a message...") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )

                Button(onClick = {
                    scope.launch {
                        if (messageText.value.isNotEmpty()) {
                            val receiverId = currentChat.users.first { it != currentUserId }

                            val newMessage = MessageData(
                                messageId = FirebaseRealtimeService.db.child("chats/$chatId/messages").push().key ?: "",
                                senderId = currentUserId ?: "",
                                receiverId = receiverId,
                                messageText = messageText.value,
                                messageTimestamp = System.currentTimeMillis(),
                                messageImageUrl = null
                            )

                            ChatRepo.addMessageToChat(chatId, newMessage)

                            chat.value = ChatRepo.getChatById(chatId)

                            messageText.value = ""
                        }
                    }
                }) {
                    Text(text = "Send")
                }
            }
        }
    }
}

