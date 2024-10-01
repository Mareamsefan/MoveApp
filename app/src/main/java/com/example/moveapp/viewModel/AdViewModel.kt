package com.example.moveapp.viewModel
import AdData
import android.content.Context
import android.widget.Toast
import com.example.moveapp.repository.AdRepo


class AdViewModel {

    companion object {

        suspend fun createAd(context: Context, adTitle: String, adPrice: Double, adCategory: String, adDescription: String, userId: String, address: String, postalCode: String): Boolean {
            return try {
                val ad = AdData(adTitle=adTitle, adPrice=adPrice, adCategory= adCategory, adDescription=adDescription, userId = userId, address= address, postalCode = postalCode)

                val success = AdRepo.addAdToDatabase(ad)

                if (!success) {
                    Toast.makeText(context, "Adding the ad to the database failed!", Toast.LENGTH_SHORT).show()
                    return false
                }

                true
            } catch (e: Exception) {
                Toast.makeText(context, "Ad creation failed, please try again!", Toast.LENGTH_SHORT).show()
                false

            }
        }

    }
}