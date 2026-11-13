package com.example.agricultureoffline.data.repository

import android.util.Log
import com.example.agricultureoffline.data.local.Disease
import com.example.agricultureoffline.data.local.Scan
import com.example.agricultureoffline.data.model.PredictionResult
import com.example.agricultureoffline.domain.ScanInsights
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnalysisRepository(
    private val diseaseRepository: DiseaseRepository,
    private val scanRepository: ScanRepository
) {

    companion object {
        private const val TAG = "AnalysisRepository"
        const val CONFIDENCE_THRESHOLD = 0.50f
    }

    suspend fun savePrediction(
        farmerId: Int,
        imagePath: String,
        prediction: PredictionResult
    ): Int? {
        Log.d(TAG, "AI Prediction: class='${prediction.diseaseName}', confidence=${prediction.confidence}")

        // 1. Parameterized lookup from offline SQLite database using predicted class label
        var disease = diseaseRepository.getDiseaseByClassName(prediction.diseaseName)

        if (disease == null) {
            Log.w(TAG, "No matching disease record in SQLite for: ${prediction.diseaseName}")
            val formattedName = prediction.diseaseName.replace('_', ' ').trim()
            disease = Disease(
                id = 0,
                className = prediction.diseaseName,
                plantName = ScanInsights.FALLBACK_PLANT,
                diseaseName = formattedName,
                symptoms = ScanInsights.FALLBACK_SYMPTOMS,
                treatment = ScanInsights.FALLBACK_TREATMENT,
                prevention = ScanInsights.FALLBACK_PREVENTION
            )
        }

        // 2. Confidence and status assessment
        val status = ScanInsights.deriveStatus(
            confidence = prediction.confidence,
            predictedClass = prediction.diseaseName,
            diseaseName = disease.diseaseName,
            lowConfidenceThreshold = CONFIDENCE_THRESHOLD
        )

        // 3. Format timestamp for local display and history
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val formattedTimestamp = dateFormat.format(Date())

        // 4. Create scan history record
        val scan = Scan(
            farmerId = farmerId,
            imagePath = imagePath,
            predictedClass = prediction.diseaseName,
            diseaseName = disease.diseaseName,
            diseaseId = disease.id,
            confidence = prediction.confidence,
            status = status,
            scannedAt = formattedTimestamp,
            dateTime = System.currentTimeMillis()
        )

        // 5. Persist to SQLite scan_history table
        val insertedId = scanRepository.insertScan(scan).toInt()
        return if (insertedId > 0) insertedId else null
    }

    suspend fun getScanResult(
        scanId: Int
    ): Pair<Scan, Disease>? {
        val scan = scanRepository.getScanById(scanId) ?: return null

        val disease = (if (scan.diseaseId > 0) diseaseRepository.getDiseaseById(scan.diseaseId) else null)
            ?: diseaseRepository.getDiseaseByClassName(scan.predictedClass)
            ?: Disease(
                id = 0,
                className = scan.predictedClass,
                plantName = ScanInsights.FALLBACK_PLANT,
                diseaseName = scan.diseaseName,
                symptoms = ScanInsights.FALLBACK_SYMPTOMS,
                treatment = ScanInsights.FALLBACK_TREATMENT,
                prevention = ScanInsights.FALLBACK_PREVENTION_SHORT
            )

        return Pair(scan, disease)
    }
}
