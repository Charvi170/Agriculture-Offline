package com.example.agricultureoffline.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agricultureoffline.ai.PlantDiseaseClassifier
import com.example.agricultureoffline.data.local.Disease
import com.example.agricultureoffline.data.local.Scan
import com.example.agricultureoffline.data.repository.AnalysisRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnalysisViewModel(
    private val repository: AnalysisRepository,
    private val classifier: PlantDiseaseClassifier
) : ViewModel() {

    var scanResult by mutableStateOf<Pair<Scan, Disease>?>(null)
        private set

    var isAnalyzing by mutableStateOf(false)
        private set

    fun analyze(
        farmerId: Int,
        imagePath: String,
        onComplete: (Boolean) -> Unit
    ) {
        if (isAnalyzing) return
        isAnalyzing = true

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    // Load leaf image safely from local storage with memory-safe downsampling
                    val boundsOptions = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeFile(imagePath, boundsOptions)

                    var inSampleSize = 1
                    val reqSize = 1024
                    while (boundsOptions.outWidth / inSampleSize > reqSize || boundsOptions.outHeight / inSampleSize > reqSize) {
                        inSampleSize *= 2
                    }

                    val decodeOptions = BitmapFactory.Options().apply {
                        this.inSampleSize = inSampleSize
                        inPreferredConfig = Bitmap.Config.ARGB_8888
                    }
                    val bitmap = BitmapFactory.decodeFile(imagePath, decodeOptions)
                        ?: return@withContext null

                    // Run the REAL TFLite model inference safely off main thread
                    val prediction = classifier.predict(bitmap)
                        ?: return@withContext null

                    // Save prediction and retrieve disease info from offline SQLite
                    val scanId = repository.savePrediction(
                        farmerId = farmerId,
                        imagePath = imagePath,
                        prediction = prediction
                    ) ?: return@withContext null

                    // Load the persisted Scan + Disease record
                    repository.getScanResult(scanId)
                } catch (t: Throwable) {
                    android.util.Log.e("AnalysisViewModel", "Leaf analysis error", t)
                    null
                }
            }

            isAnalyzing = false

            if (result != null) {
                scanResult = result
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }

    /**
     * Load an existing historical scan from the local database
     */
    fun loadScan(
        scanId: Int,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    repository.getScanResult(scanId)
                } catch (t: Throwable) {
                    android.util.Log.e("AnalysisViewModel", "Error loading scan", t)
                    null
                }
            }

            if (result != null) {
                scanResult = result
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }

    override fun onCleared() {
        classifier.close()
        super.onCleared()
    }
}
