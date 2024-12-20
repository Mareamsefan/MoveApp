package com.example.moveapp.ui.screens.messages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.data.ChatData
import com.example.moveapp.data.MessageData
import com.example.moveapp.repository.ChatRepo
import com.example.moveapp.ui.composables.MessageItem
import com.example.moveapp.utility.FireAuthService
import com.example.moveapp.utility.FirebaseRealtimeService
import kotlinx.coroutines.launch
import com.example.moveapp.utility.HelpFunctions.Companion.censorshipValidator
import com.example.moveapp.utility.HelpFunctions.Companion.validateMessageLength
import com.example.moveapp.utility.MessageTooLongException
import com.example.moveapp.utility.NetworkUtil
import com.example.moveapp.utility.ProhibitedContentException

@Composable
fun SpecificMessageScreen(navController: NavController, chatId: String) {
    val chat = remember { mutableStateOf<ChatData?>(null) }
    val messageText = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val currentUserId = FireAuthService.getUserId()
    var errorMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    val networkUtil = NetworkUtil()
    val context = LocalContext.current

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
                            try {
                                censorshipValidator(messageText.value)
                                validateMessageLength(messageText.value)
                                val receiverId = currentChat.users.first { it != currentUserId }
                                val newMessage = MessageData(
                                    messageId = FirebaseRealtimeService.db.child("chats/$chatId/messages").push().key ?: "",
                                    senderId = currentUserId ?: "",
                                    receiverId = receiverId,
                                    messageText = messageText.value,
                                    messageTimestamp = System.currentTimeMillis(),
                                    messageImageUrl = null,
                                    isRead = false,
                                )
                                if(!networkUtil.isUserConnectedToInternet(context)) {
                                    Toast.makeText(context, "No internet connection, message could not be sent", Toast.LENGTH_SHORT).show()
                                }else{
                                    ChatRepo.addMessageToChat(chatId, newMessage)
                                    chat.value = ChatRepo.getChatById(chatId)
                                    messageText.value = ""
                                }

                            } catch (e: ProhibitedContentException) {
                                errorMessage = e.message.toString()
                                showErrorDialog = true
                            }
                            catch (e: MessageTooLongException){
                                errorMessage = e.message.toString()
                                showErrorDialog = true
                            }

                        }
                    }
                }) {
                    Text(text = "Send")
                }
            }

            if (showErrorDialog) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog = false },
                    title = { Text("Error") },
                    text = { Text(errorMessage) },
                    confirmButton = {
                        Button(onClick = { showErrorDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

