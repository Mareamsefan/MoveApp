package com.example.moveapp.ui.screens.messages

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.moveapp.ui.composables.ChatItem
import com.example.moveapp.ui.navigation.AppScreens
/*@Composable
fun AllMessagesScreen(navController: NavController) {
    val userId = FireAuthService.getUserId()
    val scope = rememberCoroutineScope()

    // State to hold the list of chats
    var chats by remember { mutableStateOf<List<ChatData>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Use LaunchedEffect to trigger the data fetching
    LaunchedEffect(userId) {
        if (userId != null) {
            try {
                // Fetch chats asynchronously
                ChatRepogetUserChatsFlow(userId).collect { fetchedChats ->
                    chats = fetchedChats
                    loading = false
                    Log.d("AllMessagesScreen", "Fetched chats: $fetchedChats")
                }
            } catch (e: Exception) {
                errorMessage = "Error fetching chats: ${e.message}"
                loading = false
                Log.e("AllMessagesScreen", "Error fetching chats", e)
            }
        } else {
            loading = false
            errorMessage = "User not logged in"
            Log.e("AllMessagesScreen", "User is not logged in")
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Show loading indicator if still loading
        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...")
            }
        }

        // Show error message if there's an error
        errorMessage?.let {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: $it")
            }
        }

        // Show the list of chats if no error
        if (chats.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(chats) { chat ->
                    ChatItem(chat, navController)
                }
            }
        }

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


*/