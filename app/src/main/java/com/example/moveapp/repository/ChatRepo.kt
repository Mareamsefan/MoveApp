package com.example.moveapp.repository

import com.example.moveapp.data.ChatData
import com.example.moveapp.data.MessageData
import com.example.moveapp.data.ReportData
import com.example.moveapp.utility.FirestoreService
import kotlinx.coroutines.tasks.await

class ChatRepo {

    companion object {
        suspend fun addChatToDatabase(chat: ChatData): Boolean {
            return try {
                val chatId = chat.chatId
                FirestoreService.createDocument("chats", chatId, chat)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }


        suspend fun addMessageToChat(chatId: String, message: MessageData): Boolean {
            return try {
                var chat = FirestoreService.readDocument("chats", chatId, ChatData::class.java)
                chat?.let {
                    // Create a mutable copy because List is immutable
                    val updatedMessages = it.messages.toMutableList()
                    // Add the new adId to the copy
                    updatedMessages.add(message)
                    // Update the favorites field with the modified list
                    it.messages = updatedMessages

                    // Update the document in Firestore
                    FirestoreService.updateDocument("chats", chatId, it)
                    true
                } ?: false // If user is null, return false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    }
}