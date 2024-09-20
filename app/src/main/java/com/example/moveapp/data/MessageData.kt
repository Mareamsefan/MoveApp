package com.example.moveapp.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class MessageData (
    var messageId: String,
    var senderId: String,
    var receiverId: String,
    var messageText: String,
    var messageTimestamp: Timestamp,
    var messageImageUrl: String,
)
{}