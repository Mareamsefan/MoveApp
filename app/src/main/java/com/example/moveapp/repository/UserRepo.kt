package com.example.moveapp.repository

import com.example.moveapp.data.UserData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.moveapp.utility.FirestoreService
import com.example.moveapp.utility.HelpFunctions
import com.google.rpc.Help

class UserRepo {

    // suspend is the keyword to make it async
    // with suspend it wont block other threads and can pause
    suspend fun addUserToDatabase(user: UserData): Boolean {
        return try {
            // Add user to the "users" collection using their userId as the document ID
            // Set ensures that if a document ith the same userId exists
            // it will overwrite the existing one
            // await ensures that it completes before moving forward in the function
            /* Todo:
                Context: Byttet fra user.userId -> user.userId.toString()
                         grunnet error som kræsjet programmet som kom av at
                         user.userId er nullable (bruker string? og ikke string)
                Hva må gjøres: Vurder å fikse dette på en annen måte.
                               Tror ikke dette er i nærheten av "Best Practice"
             */
            FirestoreService.getUsersCollection().document(user.userId.toString()).set(user).await()
            true  // Return true if successful
        } catch (e: Exception) {
            e.printStackTrace()  // Log the error
            false  // Return false if an error occurs
        }
    }
}