package com.example.agricultureoffline.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agricultureoffline.data.repository.FarmerRepository

class FarmerViewModelFactory(
    private val repository: FarmerRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(FarmerViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return FarmerViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}