package com.example.agricultureoffline.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agricultureoffline.sms.OtpApiResult
import com.example.agricultureoffline.sms.OtpRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OtpViewModel : ViewModel() {

    private val dataSource = OtpRemoteDataSource()

    var isDispatchingSms by mutableStateOf(false)
        private set

    var lastResult by mutableStateOf<OtpApiResult?>(null)
        private set

    fun sendLoginSuccessSms(countryCode: String, nationalNumber: String) {
        if (isDispatchingSms) return
        isDispatchingSms = true
        viewModelScope.launch {
            val now = Date()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val dateStr = dateFormat.format(now)
            val timeStr = timeFormat.format(now)

            val result = withContext(Dispatchers.IO) {
                dataSource.sendLoginSuccessNotification(countryCode, nationalNumber, dateStr, timeStr)
            }
            lastResult = result
            isDispatchingSms = false
        }
    }
}
