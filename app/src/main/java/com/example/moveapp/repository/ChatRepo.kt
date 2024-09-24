package com.example.moveapp.repository

import com.example.moveapp.data.ChatData
import com.example.moveapp.data.MessageData
import com.example.moveapp.utility.FirestoreService
import kotlinx.coroutines.tasks.await

class ChatRepo {

    suspend fun addChatToDatabase(chat: ChatData): Boolean {
        return try {
            FirestoreService.getChatsCollection().document(chat.chatId).set(chat).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Logic for this function created by chatGPT
    // https://chatgpt.com/share/66f2a548-c918-8013-b52f-a6587a3db883
    suspend fun addMessageToChat(chatId: String, message: MessageData): Boolean {
        return try {
            // Step 1: Retrieve the chat document from Firestore
            val chatDocument = FirestoreService.getChatsCollection().document(chatId).get().await()

            if (chatDocument.exists()) {
                // Step 2: Convert the document snapshot to ChatData object
                val chatData = chatDocument.toObject(ChatData::class.java)

                if (chatData != null) {
                    // Step 3: Add the new message to the list of messages
                    val updatedMessages = chatData.messages.toMutableList()
                    updatedMessages.add(message)

                    // Step 4: Update the chat with the new message and timestamp
                    chatData.messages = updatedMessages
                    chatData.lastMessageTimestamp = message.messageTimestamp.toDate()

                    // Step 5: Update the chat document in Firestore
                    FirestoreService.getChatsCollection().document(chatId).set(chatData).await()

                    true
                } else {
                    false // If chatData object did not get converted correctly.
                }
            } else {
                false // If chat does not exist returns false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


}