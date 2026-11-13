package com.example.agricultureoffline.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.agricultureoffline.data.local.Scan
import com.example.agricultureoffline.data.repository.ScanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScanHistoryViewModel(
    private val scanRepository: ScanRepository
) : ViewModel() {

    var scans by mutableStateOf<List<Scan>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun loadScans(farmerId: Int) {
        isLoading = true
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                scanRepository.getAllScans(farmerId)
            }
            scans = list
            isLoading = false
        }
    }

    fun deleteScan(scanId: Int, farmerId: Int, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                scanRepository.deleteScan(scanId)
            }
            loadScans(farmerId)
            onDeleted()
        }
    }
}

class ScanHistoryViewModelFactory(
    private val scanRepository: ScanRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScanHistoryViewModel::class.java)) {
            return ScanHistoryViewModel(scanRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
