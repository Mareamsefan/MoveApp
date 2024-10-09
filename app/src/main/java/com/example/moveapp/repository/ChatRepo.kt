package com.example.moveapp.repository

import com.example.moveapp.data.ChatData
import com.example.moveapp.data.MessageData
import com.example.moveapp.utility.FirebaseRealtimeService
import kotlinx.coroutines.tasks.await

class ChatRepo {

    companion object {
        suspend fun addChatToDatabase(chat: ChatData): Boolean {
            return try {
                val chatId = chat.chatId
                FirebaseRealtimeService.createData("chats/$chatId", chat)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }


        suspend fun addMessageToChat(chatId: String, message: MessageData): Boolean {
            return try {
                // Create a unique message ID
                val messageId = FirebaseRealtimeService.db.child("chats/$chatId/messages").push().key ?: return false

                // Add the new message under the "messages" node in the specified chat
                FirebaseRealtimeService.db.child("chats/$chatId/messages/$messageId").setValue(message).await()

                // Update the lastMessageTimestamp for the chat
                FirebaseRealtimeService.db.child("chats/$chatId/lastMessageTimestamp").setValue(message.messageTimestamp).await()

                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        suspend fun getUserChats(userId: String): List<ChatData> {
            val userChats = mutableListOf<ChatData>()
            try {
                // Retrieve all chats from the database
                val snapshot = FirebaseRealtimeService.getData("chats")?.children
                snapshot?.forEach { chatSnapshot ->
                    val chat = chatSnapshot.getValue(ChatData::class.java)
                    chat?.let {
                        // Check if the userId is part of the users list
                        if (it.users.contains(userId)) {
                            userChats.add(it)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return userChats
        }

        suspend fun getChatById(chatId: String): ChatData? {
            return try {
                val snapshot = FirebaseRealtimeService.getData("chats/$chatId")
                snapshot?.getValue(ChatData::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        suspend fun findChatBetweenUsers(currentUserId: String, receiverUserId: String): ChatData? {
            val chatsSnapshot = FirebaseRealtimeService.getData("chats")?.children
            chatsSnapshot?.forEach { chatSnapshot ->
                val chat = chatSnapshot.getValue(ChatData::class.java)
                if (chat != null && chat.users.containsAll(listOf(currentUserId, receiverUserId))) {
                    return chat
                }
            }
            return null
        }

    }
}