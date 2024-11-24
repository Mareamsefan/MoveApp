package com.example.moveapp.repository

import android.util.Log
import androidx.navigation.NavController
import com.example.moveapp.data.AdData
import com.example.moveapp.data.ChatData
import com.example.moveapp.data.MessageData
import com.example.moveapp.ui.navigation.AppScreens
import com.example.moveapp.utility.FireAuthService.getCurrentUser
import com.example.moveapp.utility.FirebaseRealtimeService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
                adId = adId
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
                val currentUserId = getCurrentUser()?.uid
                val chatRef = FirebaseRealtimeService.db.child("chats").child(chatId)
                val snapshot = chatRef.get().await()
                if (snapshot.exists()) {
                    if (currentUserId != null) {
                        Log.d("KJØRER DENNE?:", chatId)
                        markMessagesAsRead(chatId, currentUserId)
                    }
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

        suspend fun fetchChatsWithAds(userId: String): Pair<List<ChatData>, Map<String, AdData?>> {
            return try {
                val chats = ChatRepo.getUserChats(userId)
                val adIds = chats.map { it.adId }
                val adsMap = adIds.associateWith { adId -> AdRepo.getAd(adId) }
                Pair(chats, adsMap)
            } catch (e: Exception) {
                Pair(emptyList(), emptyMap()) // Returner tomme verdier ved feil
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
                                    navController.navigate("${AppScreens.SPECIFIC_MESSAGE_SCREEN}/${newChat.chatId}")
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                markMessagesAsRead(chatId, currentUserId)
                                Log.d("KJØRER DENNE:", chatId)
                                navController.navigate("${AppScreens.SPECIFIC_MESSAGE_SCREEN}/$chatId")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ChatRepo", ",Error while fetching or creating chat: ${e.message}")
                    }
                }
            }
        }

        private suspend fun markMessagesAsRead(chatId: String, userId: String) {
            try {
                val messagesSnapshot = FirebaseRealtimeService.getData("chats/$chatId/messages")

                if (messagesSnapshot == null) {
                    return
                }

                messagesSnapshot.children.forEach { messageSnapshot ->
                    val receiverId = messageSnapshot.child("receiverId").getValue(String::class.java)
                    val isRead = messageSnapshot.child("read").getValue(Boolean::class.java) ?: false
                    val messageId = messageSnapshot.key

                    if (receiverId?.trim() == userId.trim() && !isRead) {
                        if (messageId != null) {
                            val path = "chats/$chatId/messages/$messageId/read"
                            FirebaseRealtimeService.updateData(path, true)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatRepo", ",Error while setting messages as read: ${e.message}")
            }
        }

        fun listenForUnreadMessages(userId: String, onUnreadMessagesFound: (Boolean) -> Unit) {
            val chatsRef = FirebaseRealtimeService.db.child("chats")

            chatsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var hasUnreadMessages = false


                    snapshot.children.forEach { chatSnapshot ->
                        val messagesSnapshot = chatSnapshot.child("messages")


                        messagesSnapshot.children.forEach { messageSnapshot ->
                            val receiverId = messageSnapshot.child("receiverId").getValue(String::class.java)
                            val isRead = messageSnapshot.child("read").getValue(Boolean::class.java) ?: false

                            if (receiverId == userId && !isRead) {
                                hasUnreadMessages = true
                                return@forEach
                            }
                        }
                    }


                    onUnreadMessagesFound(hasUnreadMessages)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatRepo", "Database operation cancelled: ${error.message}")
                }
            })
        }

        fun hasUnreadMessagesInChat(userId: String, chatId: String, onUnreadMessagesFound: (Boolean) -> Unit) {
            val chatRef = FirebaseRealtimeService.db.child("chats").child(chatId).child("messages")

            chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var hasUnreadMessages = false


                    snapshot.children.forEach { messageSnapshot ->
                        val receiverId = messageSnapshot.child("receiverId").getValue(String::class.java)
                        val isRead = messageSnapshot.child("read").getValue(Boolean::class.java) ?: false


                        if (receiverId == userId && !isRead) {
                            hasUnreadMessages = true
                            return@forEach
                        }
                    }


                    onUnreadMessagesFound(hasUnreadMessages)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatRepo", "Database operation cancelled: ${error.message}")
                    onUnreadMessagesFound(false)
                }
            })
        }





    }
}