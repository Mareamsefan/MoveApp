package com.example.moveapp.data

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class MessageData (
    var messageId: String,
    var senderId: String,
    var receiverId: String,
    var messageText: String,
    var messageTimestamp: Long,
    var messageImageUrl: String?,  // ? -> you likely will not need an img for every message.
)
{
    constructor() : this("", "", "", "", 0L, null)

    fun compareTo(other: MessageData): Int {
        return other.messageTimestamp.compareTo(this.messageTimestamp) // Nyeste først
    }
}