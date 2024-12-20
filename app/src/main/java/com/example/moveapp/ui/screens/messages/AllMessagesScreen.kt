package com.example.moveapp.ui.screens.messages
import ChatItemWithAd
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.data.AdData
import com.example.moveapp.data.ChatData
import com.example.moveapp.repository.AdRepo
import com.example.moveapp.utility.FireAuthService
import com.example.moveapp.repository.ChatRepo
import com.example.moveapp.ui.navigation.AppScreens
import com.example.moveapp.utility.NetworkUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllMessagesScreen(navController: NavController) {
    val userId = FireAuthService.getCurrentUser()?.uid
    var chats by remember { mutableStateOf<List<ChatData>>(emptyList()) }
    var adsMap by remember { mutableStateOf<Map<String, AdData?>>(emptyMap()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val refreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val networkUtil = NetworkUtil()



   fun fetchChats() {
        coroutineScope.launch {
            if(networkUtil.isUserConnectedToInternet(context)) {
                isRefreshing = true
                if (userId != null) {
                    try {
                        val fetchedChats = ChatRepo.getUserChats(userId)

                        chats = fetchedChats.sortedByDescending { chat ->
                            chat.messages.values.maxOfOrNull { it.messageTimestamp } ?: 0L
                        }

                        loading = false
                        errorMessage = null

                        val adsIds = chats.map { chat -> chat.adId }
                        val ads = adsIds.associateWith { adId -> AdRepo.getAd(adId) }
                        adsMap = ads
                    } catch (e: Exception) {
                        errorMessage = "Error fetching chats or ads: ${e.message}"
                        loading = false
                    } finally {
                        isRefreshing = false
                    }
                } else {
                    loading = false
                    errorMessage = "User not logged in"
                }
            }else{
                Toast.makeText(context, "Could not get messages, no internet connection", Toast.LENGTH_SHORT).show()
            }
        }

    }


    LaunchedEffect(userId) {
        fetchChats()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...")
            }
        }

        errorMessage?.let {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: $it")
            }
        }

        if (chats.isNotEmpty() && userId != null) {
            PullToRefreshBox(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.TopEnd,
                state = refreshState,
                isRefreshing = isRefreshing,
                onRefresh = {
                    fetchChats()

                }) {

                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(chats) { chat ->
                        val ad = adsMap[chat.adId]

                        val chatId = chat.chatId
                        val chatMessages = chat.messages

                        if(chatMessages.isNotEmpty()){
                            ChatItemWithAd(navController, chat = chat, ad = ad, onClick = {
                                navController.navigate("${AppScreens.SPECIFIC_MESSAGE_SCREEN}/$chatId")
                            })
                        }

                    }
                }
            }
        }
    }
}
