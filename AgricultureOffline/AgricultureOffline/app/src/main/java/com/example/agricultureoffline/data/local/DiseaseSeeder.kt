package com.example.agricultureoffline.data.local

import android.content.Context
import android.util.Log
import org.json.JSONArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DiseaseSeeder {

    private const val TAG = "DiseaseSeeder"

    suspend fun seed(
        context: Context,
        database: AppDatabase
    ) {
        withContext(Dispatchers.IO) {
            try {
                val dao = database.diseaseDao()

                val json = context.assets
                    .open("disease_info.json")
                    .bufferedReader()
                    .use { it.readText() }

                val array = JSONArray(json)

                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)

                    val className = item.optString("class_name", item.optString("name"))
                    val plantName = item.optString("plant_name", "General")
                    val diseaseName = item.optString("disease_name", className.replace('_', ' ').trim())
                    val symptoms = item.optString("symptoms", "No symptoms reported.")
                    val treatment = item.optString("treatment", "No treatment recorded.")
                    val prevention = item.optString("prevention", "No preventive measures recorded.")
                    val sourceUrl = item.optString("source_url", "")

                    // Update existing records or insert missing records
                    val existing = dao.getDiseaseByClassName(className)
                    if (existing == null) {
                        dao.insertDisease(
                            Disease(
                                className = className,
                                plantName = plantName,
                                diseaseName = diseaseName,
                                symptoms = symptoms,
                                treatment = treatment,
                                prevention = prevention,
                                sourceUrl = sourceUrl
                            )
                        )
                    } else {
                        dao.insertDisease(
                            existing.copy(
                                plantName = plantName,
                                diseaseName = diseaseName,
                                symptoms = symptoms,
                                treatment = treatment,
                                prevention = prevention,
                                sourceUrl = sourceUrl
                            )
                        )
                    }
                }
                Log.d(TAG, "Disease database initialized successfully with ${dao.getDiseaseCount()} entries.")
            } catch (e: Exception) {
                Log.e(TAG, "Error seeding disease database: ${e.message}", e)
            }
        }
    }
}