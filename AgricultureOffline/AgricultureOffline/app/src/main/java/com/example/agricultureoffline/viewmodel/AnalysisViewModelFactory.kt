package com.example.agricultureoffline.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agricultureoffline.ai.PlantDiseaseClassifier
import com.example.agricultureoffline.data.repository.AnalysisRepository

class AnalysisViewModelFactory(
    private val repository: AnalysisRepository,
    private val classifier: PlantDiseaseClassifier
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                AnalysisViewModel::class.java
            )
        ) {

            @Suppress("UNCHECKED_CAST")

            return AnalysisViewModel(
                repository = repository,
                classifier = classifier
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}