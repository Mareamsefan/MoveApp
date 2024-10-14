package com.example.moveapp.ui.screens.messages

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.data.ChatData
import com.example.moveapp.data.MessageData
import com.example.moveapp.repository.ChatRepo
import com.example.moveapp.repository.UserRepo.Companion.findUserIdByEmailOrUsername
import com.example.moveapp.ui.navigation.AppScreens
import com.example.moveapp.utility.FireAuthService
import com.example.moveapp.utility.FirebaseRealtimeService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun StartNewChatScreen(navController: NavController) {
    val userInput = remember { mutableStateOf("") }
    val messageText = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title of the screen
        Text(text = "Start a New Chat", modifier = Modifier.padding(bottom = 16.dp))

        // Input field for entering the receiver's email or username
        OutlinedTextField(
            value = userInput.value,
            onValueChange = { userInput.value = it },
            label = { Text(text = "Receiver's Email or Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Input field for entering the first message
        OutlinedTextField(
            value = messageText.value,
            onValueChange = { messageText.value = it },
            label = { Text(text = "Message") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)  // Set height for the message box
                .padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button to send the message and create a new chat
        Button(
            onClick = {
                scope.launch {
                    val currentUserId = FireAuthService.getUserId() ?: return@launch

                    // Query Firestore to find the userId by email or username
                    val receiverUserId = findUserIdByEmailOrUsername(userInput.value)
                    if (receiverUserId.isNullOrEmpty()) {
                        // Handle case where the user is not found
                        return@launch
                    }

                    // Create a new chat object
                    val newChatId = FirebaseRealtimeService.db.child("chats").push().key ?: return@launch
                    val newChat = ChatData(
                        chatId = newChatId,
                        users = listOf(currentUserId, receiverUserId),
                        lastMessageTimestamp = System.currentTimeMillis(),
                        messages = listOf()
                    )

                    FirebaseRealtimeService.createData("chats/$newChatId", newChat)

                    val firstMessage = MessageData(
                        messageId = FirebaseRealtimeService.db.child("chats/$newChatId/messages").push().key ?: "",
                        senderId = currentUserId,
                        receiverId = receiverUserId,
                        messageText = messageText.value,
                        messageTimestamp = System.currentTimeMillis(),
                        messageImageUrl = null
                    )

                    ChatRepo.addMessageToChat(newChatId, firstMessage)

                    navController.navigate("${AppScreens.SPECIFIC_MESSAGE.name}/${newChat.chatId}")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Send Message")
        }
    }
}