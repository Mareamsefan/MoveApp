package com.example.moveapp.ui.screens.ad

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.moveapp.data.AdData

@Composable
fun EditAdScreen(navController: NavController, adId: String?){

    // Dummy ad data for preview purposes, replace with actual data retrieval logic
    var adData = AdData(
        adTitle = "Eksempel Tittel",
        adPrice = 1000.0,
        adCategory = "Bilutleie",
        adUnderCategory = "Liten bil",
        adDescription = "Detaljert beskrivelse her...",
        address = "Eksempelveien 123",
        postalCode = "0123",
        city = "Oslo"
    )

    var title by remember { mutableStateOf(adData.adTitle) }
    var price by remember { mutableStateOf(adData.adPrice.toString()) }
    var category by remember { mutableStateOf(adData.adCategory) }
    var underCategory by remember { mutableStateOf(adData.adUnderCategory) }
    var description by remember { mutableStateOf(adData.adDescription) }
    var address by remember { mutableStateOf(adData.address) }
    var postalCode by remember { mutableStateOf(adData.postalCode) }
    var city by remember { mutableStateOf(adData.city) }

    // Skjerminnholdet
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Rediger Annonse", style = MaterialTheme.typography.labelLarge)

        // Tittel
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Tittel") },
            modifier = Modifier.fillMaxWidth()
        )

        // Pris
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Pris") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        // Kategori
        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Kategori") },
            modifier = Modifier.fillMaxWidth()
        )

        // Underkategori
        OutlinedTextField(
            value = underCategory,
            onValueChange = { underCategory = it },
            label = { Text("Underkategori") },
            modifier = Modifier.fillMaxWidth()
        )

        // Beskrivelse
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Beskrivelse") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4
        )

        // Adresse
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Adresse") },
            modifier = Modifier.fillMaxWidth()
        )

        // Postkode
        OutlinedTextField(
            value = postalCode,
            onValueChange = { postalCode = it },
            label = { Text("Postkode") },
            modifier = Modifier.fillMaxWidth()
        )

        // By
        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("By") },
            modifier = Modifier.fillMaxWidth()
        )

        // Lagre-knapp
        Button(
            onClick = {
                // Her kan du legge til logikk for å lagre endringene
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lagre Endringer")
        }

        // Tilbake-knapp
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Avbryt")
        }
    }

}