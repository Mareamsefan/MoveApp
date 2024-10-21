package com.example.moveapp.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.ui.navigation.AppScreens

@Composable
fun SearchBar(onApplySearch: MutableState<String?>, navController: NavController) {
    val searchQuery = remember { mutableStateOf("") }

    Row(modifier = Modifier.padding(top = 2.dp, bottom = 15.dp)) {
        OutlinedTextField(
            modifier = Modifier.height(56.dp),
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            label = { Text(text = stringResource(R.string.search)) }
        )

        IconButton(onClick = {
            onApplySearch.value = searchQuery.value
            navController.navigate(AppScreens.HOME.name)
        }) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(R.string.search),
            )
        }
    }
}