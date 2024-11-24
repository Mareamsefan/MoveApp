package com.example.moveapp.data


import com.google.firebase.firestore.IgnoreExtraProperties

// IgnoreExtraProperties is a safety feature that prevents failure
// when retrieving data from firestore and deserialize into an object
// If there are fields in the database that is not listed here
// They will simply be ignored
@IgnoreExtraProperties
data class ChatData (
    var chatId: String,
    var users: List<String>,
    val adId: String,
    var lastMessageTimestamp: Long,
    var messages: Map<String, MessageData>,
) {
    // No-argument constructor with default values
    constructor() : this("", emptyList(), "", 0L, emptyMap())
}
