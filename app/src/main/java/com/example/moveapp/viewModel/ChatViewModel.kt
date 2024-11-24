package com.example.moveapp.viewModel


import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.example.moveapp.data.MessageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ChatViewModel {

    private val database: DatabaseReference = FirebaseDatabase
        .getInstance("https://moveapp-750c5-default-rtdb.europe-west1.firebasedatabase.app/")
        .reference

    suspend fun sendMessage(chatId: String, messageText: String, receiverId: String): Boolean {
        return try {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return false
            val messageId = database.child("chats").child(chatId).child("messages").push().key ?: return false
            val timestamp = System.currentTimeMillis()

            // Create MessageData object
            val message = MessageData(
                messageId = messageId,
                senderId = currentUserId,
                receiverId = receiverId,
                messageText = messageText,
                messageTimestamp = timestamp,
                messageImageUrl = null
            )

            // Update the message in the chat
            withContext(Dispatchers.IO) {
                // Add the message to the messages node
                database.child("chats").child(chatId).child("messages").child(messageId).setValue(message)

                // Update the lastMessageTimestamp
                database.child("chats").child(chatId).child("lastMessageTimestamp").setValue(timestamp)
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}