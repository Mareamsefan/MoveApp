package com.example.moveapp.data

import com.example.moveapp.utility.UserTypeEnum
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date
import java.util.UUID

// IgnoreExtraProperties is a safety feature that prevents failure
// when retrieving data from firestore and deserialize into an object
// If there are fields in the database that is not listed here
// They will simply be ignored
@IgnoreExtraProperties
data class UserData (
    // Id of the user, generates a unique ID when the object is instansiated
    // Using val instead of var to make userId immutable
    val userId: String = UUID.randomUUID().toString(),
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