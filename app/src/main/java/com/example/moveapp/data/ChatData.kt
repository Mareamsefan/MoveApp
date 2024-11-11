package com.example.moveapp.data


import com.google.firebase.firestore.IgnoreExtraProperties

// IgnoreExtraProperties is a safety feature that prevents failure
// when retrieving data from firestore and deserialize into an object
// If there are fields in the database that is not listed here
// They will simply be ignored
@IgnoreExtraProperties
data class ChatData (
    var chatId: String,
    // List containing the userId of the users in the chat
    var users: List<String> = emptyList(),
    // Timestamp of the last message sent between the two users
    var lastMessageTimestamp: Long,
    // List containing alle the MessageObjects sent between the two users
    var messages: Map<String, MessageData> = emptyMap()
){}