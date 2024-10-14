package com.example.moveapp.repository

import android.util.Log
import com.example.moveapp.data.ChatData
import com.example.moveapp.data.MessageData
import com.example.moveapp.utility.FirebaseRealtimeService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

        fun getUserChatsFlow(userId: String): Flow<List<ChatData>> = FirebaseRealtimeService.getChatsFlow().map { chats ->
            // Filter chats to only include those where the userId is present in the users list
            val userChats = chats.filter { chat ->
                chat.users.contains(userId)
            }
            Log.d("getUserChatsFlow", "Number of chats for userId $userId: ${userChats.size}")
            userChats
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