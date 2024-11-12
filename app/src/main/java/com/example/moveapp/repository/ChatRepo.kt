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

        // Opprett en chat med adId (hvis relevant)
        suspend fun createChatWithAdId(userIds: List<String>, adId: String): String {
            val chatId = FirebaseRealtimeService.db.child("chats").push().key ?: return ""

            val chat = ChatData(
                chatId = chatId,
                users = userIds,
                lastMessageTimestamp = System.currentTimeMillis(),
                messages = emptyMap(),
                adId = adId // Legg til adId her
            )

            // Lagre chatten i Firebase
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
                    Log.d("CHAT FETCHED:", "${chat}")
                }
            }
            Log.d("CHATS FETCHED:", "$chats")
            return chats
        }

        // Hent chatter relatert til en spesifikk adId (hvis du trenger det)
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
                Log.e("ChatRepo", "Feil under henting av chatter for adId $adId: ${e.message}")
                emptyList() // Returner tom liste ved feil
            }
        }

        suspend fun getChatById(chatId: String): ChatData? {
            return try {
                val chatRef = FirebaseRealtimeService.db.child("chats").child(chatId)
                Log.d("ChatRepo", "Henter chat fra Firebase: $chatRef")
                val snapshot = chatRef.get().await()
                if (snapshot.exists()) {
                    snapshot.getValue(ChatData::class.java)
                } else {
                    Log.d("ChatRepo", "Chat ikke funnet for chatId: $chatId")
                    null
                }
            } catch (e: Exception) {
                Log.e("ChatRepo", "Feil under henting av chat: ${e.message}")
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

        fun startOrOpenChat(navController: NavController, sellerId: String, currentUserId: String?, adId: String?) {
            if (currentUserId != null && sellerId != currentUserId && adId != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Finn eksisterende chat mellom brukerne
                        val chat = findChatBetweenUsers(currentUserId, sellerId)
                        val chatId = chat?.chatId

                        // Hvis chat ikke finnes, opprett en ny chat
                        if (chatId == null) {
                            createChatWithAdId(listOf(currentUserId,sellerId), adId)

                            // Finn chatten igjen etter at den er lagt til
                            val newChat = findChatBetweenUsers(currentUserId, sellerId)
                            if (newChat != null) {
                                withContext(Dispatchers.Main) {  // Bytt til hovedtråden for navigering
                                    navController.navigate("specificMessageScreen/${newChat.chatId}")
                                }
                            }
                        } else {
                            // Chatten finnes allerede
                            withContext(Dispatchers.Main) {  // Bytt til hovedtråden for navigering
                                navController.navigate("specificMessageScreen/$chatId")
                            }
                        }
                    } catch (e: Exception) {
                        // Håndter feil her uten logging
                    }
                }
            }
        }


    }
}