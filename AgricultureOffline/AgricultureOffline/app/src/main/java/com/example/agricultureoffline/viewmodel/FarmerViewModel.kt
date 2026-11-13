package com.example.agricultureoffline.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agricultureoffline.data.local.Farmer
import com.example.agricultureoffline.data.repository.FarmerRepository
import kotlinx.coroutines.launch

class FarmerViewModel(
    private val repository: FarmerRepository
) : ViewModel() {

    fun saveFarmer(
        name: String,
        phone: String,
        onSaved: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val existing = repository.getFarmerByPhone(phone)
            val id = if (existing != null) {
                val updated = existing.copy(
                    name = name,
                    loginTimestamp = now
                )
                repository.updateFarmer(updated)
                existing.farmerId.toLong()
            } else {
                val farmer = Farmer(
                    name = name,
                    phoneNo = phone,
                    loginTimestamp = now
                )
                repository.insertFarmer(farmer)
            }
            onSaved(id)
        }
    }
}