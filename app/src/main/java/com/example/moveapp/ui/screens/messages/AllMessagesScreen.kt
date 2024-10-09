package com.example.moveapp.ui.screens.messages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.moveapp.data.ChatData
import com.example.moveapp.utility.FireAuthService
import com.example.moveapp.repository.ChatRepo
import com.example.moveapp.ui.navigation.AppScreens


@Composable
fun AllMessagesScreen(navController: NavController) {
    val chats = remember { mutableStateListOf<ChatData>() }
    val userId = FireAuthService.getUserId()
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        userId?.let {
            // Retrieve all chats for the logged-in user
            scope.launch {
                val userChats = ChatRepo.getUserChats(it)
                chats.addAll(userChats)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // If there are no chats, show the "No chats found" text centered
        if (chats.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No chats found")
            }
        } else {
            // List of chats
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(chats.size) { index ->
                    val chat = chats[index]
                    ChatItem(chat, navController)
                }
            }
        }

        // Spacer to push the button to the bottom
        Spacer(modifier = Modifier.height(16.dp))

        // Start New Chat button at the bottom
        Button(
            onClick = {
                // Navigate to the StartNewChatScreen
                navController.navigate(AppScreens.START_NEW_CHAT.name)
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text(text = "Start new chat")
        }
    }

}

@Composable
fun ChatItem(chat: ChatData, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                navController.navigate("${AppScreens.SPECIFIC_MESSAGE.name}/${chat.chatId}")
            }
    ) {
        // Display the user IDs or you can customize this to show usernames
        Text(text = "Chat with: ${chat.users.joinToString(", ")}")
        chat.lastMessageTimestamp?.let {
            Text(text = "Last message at: $it")
        }
    }
}