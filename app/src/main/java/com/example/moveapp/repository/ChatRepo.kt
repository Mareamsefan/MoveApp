package com.example.moveapp.repository

import android.util.Log
import androidx.navigation.NavController
import com.example.moveapp.data.ChatData
import com.example.moveapp.data.MessageData
import com.example.moveapp.utility.FirebaseRealtimeService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
                val messageId = FirebaseRealtimeService.db.child("chats/$chatId/messages").push().key ?: return false

                FirebaseRealtimeService.db.child("chats/$chatId/messages/$messageId").setValue(message).await()

                FirebaseRealtimeService.db.child("chats/$chatId/lastMessageTimestamp").setValue(message.messageTimestamp).await()

                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        suspend fun createChatWithAdId(userIds: List<String>, adId: String): String {
            val chatId = FirebaseRealtimeService.db.child("chats").push().key ?: return ""

            val chat = ChatData(
                chatId = chatId,
                users = userIds,
                lastMessageTimestamp = System.currentTimeMillis(),
                messages = emptyMap(),
                adId = adId // Legg til adId her
            )

            addChatToDatabase(chat)

            return chatId
        }


        suspend fun getUserChats(userId: String): List<ChatData> {
            val chatsSnapshot = FirebaseRealtimeService.getData("chats")?.children
            val chats = mutableListOf<ChatData>()


            chatsSnapshot?.forEach { chatSnapshot ->
                val chat = chatSnapshot.getValue(ChatData::class.java)
                if (chat != null && chat.users.contains(userId)) {
                    chats.add(chat)
                }
            }
            return chats
        }

        suspend fun getChatsByAdId(adId: String): List<ChatData> {
            return try {
                val chatsRef = FirebaseRealtimeService.db.child("chats")
                val snapshot = chatsRef.orderByChild("adId").equalTo(adId).get().await()

                val chats = mutableListOf<ChatData>()
                snapshot.children.forEach { chatSnapshot ->
                    val chat = chatSnapshot.getValue(ChatData::class.java)
                    if (chat != null) {
                        chats.add(chat)
                    }
                }
                chats
            } catch (e: Exception) {
                Log.e("ChatRepo", "Error while fetching chats for adId $adId: ${e.message}")
                emptyList()
            }
        }

        suspend fun getChatById(chatId: String): ChatData? {
            return try {
                val chatRef = FirebaseRealtimeService.db.child("chats").child(chatId)
                val snapshot = chatRef.get().await()
                if (snapshot.exists()) {
                    snapshot.getValue(ChatData::class.java)
                } else {
                    Log.d("ChatRepo", "Chat not found for chatId: $chatId")
                    null
                }
            } catch (e: Exception) {
                Log.e("ChatRepo", ",Error while fetching chat: ${e.message}")
                null
            }
        }

        suspend fun findChatBetweenUsers(currentUserId: String, receiverUserId: String, adId: String): ChatData? {
            val chatsSnapshot = FirebaseRealtimeService.getData("chats")?.children
            chatsSnapshot?.forEach { chatSnapshot ->
                val chat = chatSnapshot.getValue(ChatData::class.java)
                if (chat != null && chat.users.containsAll(listOf(currentUserId, receiverUserId)) && chat.adId == adId) {
                    return chat
                }
            }
            return null
        }


        fun startOrOpenChat(navController: NavController, sellerId: String, currentUserId: String?, adId: String?) {
            if (currentUserId != null && sellerId != currentUserId && adId != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val chat = findChatBetweenUsers(currentUserId, sellerId, adId)
                        val chatId = chat?.chatId
                        val chatAdId = chat?.adId

                        if (chatId == null || chatAdId != adId) {
                            createChatWithAdId(listOf(currentUserId, sellerId), adId)

                            val newChat = findChatBetweenUsers(currentUserId, sellerId, adId)
                            if (newChat != null) {
                                withContext(Dispatchers.Main) {
                                    navController.navigate("specificMessageScreen/${newChat.chatId}")
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                navController.navigate("specificMessageScreen/$chatId")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ChatRepo", ",Error while fetching or creating chat: ${e.message}")
                    }
                }
            }
        }



    }
}