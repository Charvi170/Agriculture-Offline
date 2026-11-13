package com.example.agricultureoffline.domain

enum class ScanStatusKey {
    LowConfidence,
    Uncertain,
    Healthy,
    Confirmed
}

object ScanInsights {
    const val STATUS_LOW_CONFIDENCE = "Low Confidence"
    const val STATUS_UNCERTAIN = "Uncertain"
    const val STATUS_HEALTHY = "Healthy"
    const val STATUS_CONFIRMED = "Confirmed"

    const val FALLBACK_PLANT = "Unspecified"
    const val FALLBACK_SYMPTOMS = "Disease information is currently unavailable for this prediction."
    const val FALLBACK_TREATMENT = "No verified treatment information available."
    const val FALLBACK_PREVENTION = "Consult a local agricultural extension officer for diagnosis."
    const val FALLBACK_PREVENTION_SHORT = "Consult a local agricultural extension officer."

    fun deriveStatus(
        confidence: Float,
        predictedClass: String,
        diseaseName: String,
        lowConfidenceThreshold: Float
    ): String {
        return when {
            confidence < lowConfidenceThreshold -> STATUS_LOW_CONFIDENCE
            predictedClass.equals("PlantVillage", ignoreCase = true) -> STATUS_UNCERTAIN
            isHealthyPrediction(predictedClass = predictedClass, diseaseName = diseaseName) -> STATUS_HEALTHY
            else -> STATUS_CONFIRMED
        }
    }

    fun keyFor(
        rawStatus: String,
        predictedClass: String,
        diseaseName: String,
        confidence: Float,
        lowConfidenceThreshold: Float
    ): ScanStatusKey {
        val normalized = rawStatus.trim()
        return when {
            normalized.equals(STATUS_LOW_CONFIDENCE, ignoreCase = true) ||
                confidence < lowConfidenceThreshold -> ScanStatusKey.LowConfidence

            normalized.equals(STATUS_UNCERTAIN, ignoreCase = true) ||
                predictedClass.equals("PlantVillage", ignoreCase = true) -> ScanStatusKey.Uncertain

            normalized.equals(STATUS_HEALTHY, ignoreCase = true) ||
                isHealthyPrediction(predictedClass = predictedClass, diseaseName = diseaseName) -> ScanStatusKey.Healthy

            else -> ScanStatusKey.Confirmed
        }
    }

    fun isHealthyPrediction(predictedClass: String, diseaseName: String): Boolean {
        return predictedClass.contains("healthy", ignoreCase = true) ||
            diseaseName.contains("healthy", ignoreCase = true)
    }

    fun isInformationUnavailable(
        predictedClass: String,
        symptoms: String,
        treatment: String
    ): Boolean {
        return predictedClass.equals("PlantVillage", ignoreCase = true) ||
            symptoms.equals(FALLBACK_SYMPTOMS, ignoreCase = true) ||
            treatment.equals(FALLBACK_TREATMENT, ignoreCase = true)
    }
}
