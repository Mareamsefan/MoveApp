import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.moveapp.data.AdData
import com.example.moveapp.data.ChatData
import com.example.moveapp.repository.ChatRepo.Companion.hasUnreadMessagesInChat
import com.example.moveapp.repository.UserRepo.Companion.getUserNameById
import com.example.moveapp.ui.navigation.AppScreens
import com.example.moveapp.utility.FireAuthService.getCurrentUser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatItemWithAd(navcontroller: NavController,
                   chat: ChatData,
                   ad: AdData?,
                   onClick: () -> Unit) {
    val adImageUrl = ad?.adImages?.firstOrNull()
    var username by remember { mutableStateOf<String?>(null) }
    val currentUser = getCurrentUser()
    val userNames = remember { mutableStateOf<List<String>>(emptyList()) }
    val adId = ad?.adId
    val unread = remember { mutableStateOf<Boolean?>(false) }

    LaunchedEffect(chat.users) {
        val names = chat.users.mapNotNull { userId ->
            getUserNameById(userId)
        }

        userNames.value = names
        if (currentUser != null) {
            username = names.find { it != getUserNameById(currentUser.uid) }

        }
    }

    LaunchedEffect(chat) {
        if (currentUser != null) {
            hasUnreadMessagesInChat(currentUser.uid, chat.chatId) { hasUnreadMessages ->
                if (hasUnreadMessages) {
                    unread.value = true
                }
            }
        }
    }

    val formattedTimestamp = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(chat.lastMessageTimestamp))


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        adImageUrl?.let {
            Image(

                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .clickable {
                        navcontroller.navigate("specific_ad/${adId}")
                    },
                contentScale = ContentScale.Crop
            )
        }
        Log.d("hasUndreadMessages:", unread.value.toString())
        if (unread.value == true) {
            Box(
                modifier = Modifier
                    .align(Alignment.Top)
                    .size(12.dp)
                    .background(color = Color.Red, shape = RoundedCornerShape(6.dp))
            )
        }

        val sortedChats = chat.messages.values.sortedByDescending { it.messageTimestamp }



        Column(modifier = Modifier.weight(2f)) {
            Text(
                text = (username ?: "Unknown user").replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                },
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
            )
            Text(
                text = "Last message: ${sortedChats.firstOrNull()?.messageText ?: "No messages"}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                color = Color.Gray
            )
            Text(
                text = formattedTimestamp,
                style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}
