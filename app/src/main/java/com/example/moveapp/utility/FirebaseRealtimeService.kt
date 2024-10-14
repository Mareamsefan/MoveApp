package com.example.moveapp.utility

import android.util.Log
import com.example.moveapp.data.ChatData
import com.example.moveapp.data.MessageData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object FirebaseRealtimeService {
    val db: DatabaseReference by lazy {
        FirebaseDatabase.getInstance("https://moveapp-750c5-default-rtdb.europe-west1.firebasedatabase.app/").reference
    }
    suspend fun getData(path: String): DataSnapshot? {
        return try {
            db.child(path).get().await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    fun getChatsFlow(): Flow<List<ChatData>> = callbackFlow {
        val chatsRef = FirebaseRealtimeService.db.child("chats")

        // Attach a ValueEventListener for real-time updates
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chats = snapshot.children.mapNotNull { chatSnapshot ->
                    // Manually extract fields from the snapshot

                    // Extract the chatId (use the snapshot's key)
                    val chatId = chatSnapshot.key.orEmpty()

                    // Manually get the 'users' List
                    val users = chatSnapshot.child("users").children.map { it.value.toString() }

                    // Extract the last message timestamp
                    val lastMessageTimestamp = chatSnapshot.child("lastMessageTimestamp").getValue(Long::class.java) ?: 0L

                    // Manually get the list of messages from the snapshot (if it exists)
                    val messageList = chatSnapshot.child("messages").children.mapNotNull { messageSnapshot ->
                        // Manually extract fields for each message in the chat
                        val messageId = messageSnapshot.key.orEmpty()
                        val senderId = messageSnapshot.child("senderId").getValue(String::class.java).orEmpty()
                        val receiverId = messageSnapshot.child("receiverId").getValue(String::class.java).orEmpty()
                        val messageText = messageSnapshot.child("messageText").getValue(String::class.java).orEmpty()
                        val messageTimestamp = messageSnapshot.child("messageTimestamp").getValue(Long::class.java) ?: 0L
                        val messageImageUrl = messageSnapshot.child("messageImageUrl").getValue(String::class.java)

                        // Create and return a MessageData object
                        MessageData(
                            messageId = messageId,
                            senderId = senderId,
                            receiverId = receiverId,
                            messageText = messageText,
                            messageTimestamp = messageTimestamp,
                            messageImageUrl = messageImageUrl
                        )
                    }

                    // Create and return the ChatData object with the list of MessageData
                    ChatData(
                        chatId = chatId,
                        users = users,
                        lastMessageTimestamp = lastMessageTimestamp,
                        messages = messageList  // Assign the list of messages
                    )
                }

                // Emit the list of chats into the flow
                trySend(chats).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseRealtimeService", "Error fetching chats: ${error.message}", error.toException())
                close(error.toException()) // Close the flow in case of an error
            }
        }

        // Attach the listener to the chats reference
        chatsRef.addValueEventListener(valueEventListener)

        // Ensure the listener is removed when the flow is closed
        awaitClose { chatsRef.removeEventListener(valueEventListener) }
    }


    suspend fun <T: Any> createData(path: String, data: T) {
        try {
            db.child(path).setValue(data).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun <T: Any> updateData(path: String, data: T) {
        try {
            db.child(path).setValue(data).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteData(path: String) {
        try {
            db.child(path).removeValue().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}