package com.example.moveapp.data

import com.example.moveapp.utility.UserTypeEnum
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@IgnoreExtraProperties
data class UserData (
    // Id of the user
    var id: String = "",
    // The username the user is registred with
    var username: String = "",
    // The email the user is registred with
    var email: String = "",
    // Encrypted password
    var password: String = "",
    // Location of the user
    var location: String = "",
    // Date the user registered
    @ServerTimestamp
    var dateRegistered: Date? = null, //Retireves the timestamp from firebase
    // Url to their profile picture. Url leads to Firestore
    var profilePictureUrl: String= "",
    // List of adId's
    var favorites: List<String> = emptyList(),
    // Type of user
    var userType: UserTypeEnum = UserTypeEnum.REGULAR //Default usertype is regular
)
{}