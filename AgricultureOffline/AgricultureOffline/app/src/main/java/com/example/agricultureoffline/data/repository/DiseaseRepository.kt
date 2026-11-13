package com.example.agricultureoffline.data.repository

import com.example.agricultureoffline.data.local.Disease
import com.example.agricultureoffline.data.local.DiseaseDao

class DiseaseRepository(
    private val diseaseDao: DiseaseDao
) {

    suspend fun getDiseaseByClassName(
        className: String
    ): Disease? {
        return diseaseDao.getDiseaseByClassName(className)
            ?: diseaseDao.getDiseaseByName(className)
    }

    suspend fun getDiseaseByName(
        name: String
    ): Disease? {
        return diseaseDao.getDiseaseByName(name)
            ?: diseaseDao.getDiseaseByClassName(name)
    }

    suspend fun getDiseaseById(
        id: Int
    ): Disease? {
        return diseaseDao.getDiseaseById(id)
    }

    suspend fun getAllDiseases(): List<Disease> {
        return diseaseDao.getAllDiseases()
    }
}