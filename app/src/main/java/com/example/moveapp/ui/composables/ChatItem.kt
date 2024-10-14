package com.example.moveapp.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.data.ChatData
import com.example.moveapp.repository.UserRepo.Companion.getUserNameById
import com.example.moveapp.ui.navigation.AppScreens
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatItem(chat: ChatData, navController: NavController) {
    // State for holding the user names
    val userNames = remember { mutableStateOf<List<String>>(emptyList()) }

    // Coroutine scope to load user names asynchronously
    val scope = rememberCoroutineScope()

    // Load the user names when the composable is first created
    LaunchedEffect(chat.users) {
        scope.launch {
            // Fetch the names of the users involved in the chat
            val names = chat.users.mapNotNull { userId ->
                getUserNameById(userId) // Fetch the user's name by their ID using the readDocument function
            }
            userNames.value = names
        }
    }

    // Format the last message timestamp to a more readable format
    val formattedTimestamp = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(chat.lastMessageTimestamp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                navController.navigate("${AppScreens.SPECIFIC_MESSAGE.name}/${chat.chatId}")
            }
    ) {
        // Container for each chat bubble, with background and rounded corners
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFFF0F0F0), // Light grey background color for the chat item
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            // Chat participants list
            Text(
                text = "Chat with: ${userNames.value.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Last message preview
            Text(
                text = "Last message: ${chat.messages.lastOrNull()?.messageText ?: "No messages"}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Timestamp of last message
            Text(
                text = "Last message at: $formattedTimestamp",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
            )
        }
    }
}
