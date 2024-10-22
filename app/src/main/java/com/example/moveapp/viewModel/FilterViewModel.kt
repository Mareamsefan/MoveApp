package com.example.moveapp.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FilterViewModel(
    private val savedStateHandle: SavedStateHandle // Inject SavedStateHandle
) : ViewModel() {

    // Use keys to store data in SavedStateHandle, which survives configuration changes and navigation.
    private val _location = MutableStateFlow(savedStateHandle.get<String?>("location") ?: null)
    val location: StateFlow<String?> = _location

    private val _category = MutableStateFlow(savedStateHandle.get<String?>("category") ?: null)
    val category: StateFlow<String?> = _category

    private val _minPrice = MutableStateFlow(savedStateHandle.get<Double?>("minPrice") ?: null)
    val minPrice: StateFlow<Double?> = _minPrice

    private val _maxPrice = MutableStateFlow(savedStateHandle.get<Double?>("maxPrice") ?: null)
    val maxPrice: StateFlow<Double?> = _maxPrice

    fun applyFilter(newLocation: String?, newCategory: String?, newMinPrice: Double?, newMaxPrice: Double?) {
        _location.value = newLocation
        _category.value = newCategory
        _minPrice.value = newMinPrice
        _maxPrice.value = newMaxPrice

        // Save the current state in SavedStateHandle so that the values survive navigation
        savedStateHandle["location"] = newLocation
        savedStateHandle["category"] = newCategory
        savedStateHandle["minPrice"] = newMinPrice
        savedStateHandle["maxPrice"] = newMaxPrice
    }

    fun resetFilters() {
        _location.value = null
        _category.value = null
        _minPrice.value = null
        _maxPrice.value = null

        // Also reset the SavedStateHandle values
        savedStateHandle["location"] = null
        savedStateHandle["category"] = null
        savedStateHandle["minPrice"] = null
        savedStateHandle["maxPrice"] = null
    }
}
