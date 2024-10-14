package com.example.moveapp.ui.screens.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
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

    // Fetch the specific chat by chatId
    LaunchedEffect(chatId) {
        scope.launch {
            chat.value = ChatRepo.getChatById(chatId)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        chat.value?.let { currentChat ->
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(currentChat.messages.size) { index ->
                    val message = currentChat.messages[index]
                    MessageItem(message, currentUserId == message.senderId)
                }
            }

            // Spacer to push the content up and reserve space for the input field
            Spacer(modifier = Modifier.height(16.dp))

            // Input field for new messages
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText.value,
                    onValueChange = { messageText.value = it },
                    label = { Text(text = "Type a message...") },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )

                Button(onClick = {
                    scope.launch {
                        if (messageText.value.isNotEmpty()) {
                            val receiverId = currentChat.users.first { it != currentUserId }

                            // Create a new message object
                            val newMessage = MessageData(
                                messageId = FirebaseRealtimeService.db.child("chats/$chatId/messages").push().key ?: "",
                                senderId = currentUserId ?: "",
                                receiverId = receiverId,
                                messageText = messageText.value,
                                messageTimestamp = System.currentTimeMillis(),
                                messageImageUrl = null
                            )

                            // Add the message to the chat
                            ChatRepo.addMessageToChat(chatId, newMessage)

                            // Refresh the chat to display the new message
                            chat.value = ChatRepo.getChatById(chatId)

                            // Clear the message input field
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

@Composable
fun MessageItem(message: MessageData, isFromCurrentUser: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .wrapContentWidth()
                .background(if (isFromCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                .padding(12.dp)
        ) {
            Text(text = message.messageText)
        }
    }
}