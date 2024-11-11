package com.example.moveapp.ui.screens.ad

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.compose.outlineDark
import com.example.moveapp.R
import com.example.moveapp.repository.AdRepo.Companion.getAd
import com.example.moveapp.data.AdData
import com.example.moveapp.data.ChatData
import com.example.moveapp.data.UserData
import com.example.moveapp.repository.ChatRepo
import com.example.moveapp.repository.UserRepo.Companion.getUser
import com.example.moveapp.repository.UserRepo.Companion.getUserNameById
import com.example.moveapp.ui.composables.AdMap
import com.example.moveapp.ui.composables.Image_swipe
import com.example.moveapp.ui.navigation.AppScreens
import com.example.moveapp.utility.FireAuthService.getCurrentUser
import com.example.moveapp.utility.FireAuthService.isUserLoggedIn
import com.example.moveapp.utility.FirebaseRealtimeService
import com.example.moveapp.viewModel.UserViewModel.Companion.addAdToFavorites
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random


@Composable
fun SpecificAdScreen(navController: NavController, adId: String?) {
    val scrollState = rememberScrollState()
    var ad by remember { mutableStateOf<AdData?>(null) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    val currentUser = getCurrentUser()
    LaunchedEffect(Unit) {
         ad = getAd(adId)
    }

    // Sjekker om annonsen tilhører den innloggede brukeren
    val isOwner = currentUser != null && ad?.userId == currentUser.uid
    var owner by remember { mutableStateOf<UserData?>(null) }
    LaunchedEffect(ad) {
        ad?.let {
            owner = getUser(it.userId)
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(25.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .wrapContentHeight()
                .fillMaxSize()
        ) {
            if (ad != null) {
                Image_swipe(imageList = ad!!.adImages)
                if (isOwner) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, // Ensures alignment of IconButton and Text
                        horizontalArrangement = Arrangement.Start
                    ) {
                        IconButton(
                            onClick = {
                                navController.navigate("editAd/${ad!!.adId}")
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.edit_ad)
                            )
                        }
                        Text(
                            text = stringResource(R.string.edit_ad),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                } else if (isUserLoggedIn()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        IconButton(
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    ad!!.adId?.let { addAdToFavorites(currentUser!!.uid, it) }
                                    showSuccessMessage = true
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FavoriteBorder,
                                contentDescription = stringResource(R.string.edit_ad)
                            )
                        }
                        Text(
                            text = stringResource(R.string.add_to_favorites),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    if (showSuccessMessage) {
                        Text(
                            text = stringResource(R.string.favorite_added),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text(
                    text = ad!!.adTitle,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(text = stringResource(R.string.Price))
                Text(
                    text = ad!!.adPrice.toInt().toString() + " " + stringResource(R.string.kr),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Button(
                    onClick = {
                        startOrOpenChat(navController, ad!!.userId, currentUser?.uid)
                        // TODO: open message with seller and current user
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.contact_seller))
                }
                Text(text = ad!!.adDescription, Modifier.padding(bottom = 10.dp))
                Text(
                    text = stringResource(R.string.category),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(text = ad!!.adCategory, Modifier.padding(bottom = 10.dp))
                Text(
                    text = stringResource(R.string.Seller),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Box(
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp) // Adds padding inside the box
                        .fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        owner?.profilePictureUrl?.let { url ->
                            Image(
                                painter = rememberImagePainter(url),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(70.dp)
                            )
                    }
                        Column {
                            owner?.let {
                                Text(text = it.username, fontWeight = FontWeight.Bold)
                            }
                            Text(text = stringResource(R.string.contact_seller_above), color = Color.Gray)
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Text(
                    text = stringResource(R.string.address),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = (ad!!.address) + ", " + ad!!.postalCode + ", " + ad!!.city,
                    Modifier.padding(bottom = 10.dp)
                )
                Spacer(modifier = Modifier.weight(1f))

                //if (ad!!.position != null)
                  //  AdMap(ad = ad!!)

            }
            else {
                Text(text = "ad not found")
            }
        }
    }
}

fun startOrOpenChat(navController: NavController, sellerId: String, currentUserId: String?) {
    if (currentUserId != null && sellerId != currentUserId) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("ChatDebug", "Attempting to find or create a chat between $currentUserId and $sellerId")

                val chatId = ChatRepo.findChatBetweenUsers(currentUserId, sellerId)

                Log.d("ChatDebug", "Attempting to to fin chat:::::: $chatId")

                if (chatId == null) {
                    Log.d("ChatDebug", "No existing chat found. Creating a new chat.")

                    val chatData = ChatData(
                        chatId = FirebaseRealtimeService.db.child("chats").push().key ?: return@launch,
                        users = listOf(currentUserId, sellerId),
                        messages = mapOf(),
                        lastMessageTimestamp = System.currentTimeMillis()
                    )

                    Log.d("ChatDebug", "Adding new chat to database with chatId: ${chatData.chatId}")
                    val chatAdded = ChatRepo.addChatToDatabase(chatData)

                    if (chatAdded) {
                        Log.d("ChatDebug", "Chat successfully added to the database")
                    } else {
                        Log.e("ChatError", "Failed to add chat to the database")
                    }

                    // Find the chat again after adding it
                    val newChat = ChatRepo.findChatBetweenUsers(currentUserId, sellerId)
                    if (newChat != null) {
                        Log.d("ChatDebug", "Successfully found chat with chatId: $newChat")
                        navController.navigate("specificMessageScreen/${newChat.chatId}")
                    } else {
                        Log.e("ChatError", "Failed to find the newly created chat")
                    }
                } else {
                    Log.d("ChatDebug", "Chat already exists with chatId: $chatId")
                    navController.navigate("specificMessageScreen/$chatId")
                }
            } catch (e: Exception) {
                Log.e("ChatError", "Error occurred while trying to start or open chat: ${e.message}", e)
            }
        }
    } else {
        Log.e("ChatError", "Invalid user IDs: currentUserId: $currentUserId, sellerId: $sellerId")
    }
}
