package com.example.agricultureoffline

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class DiseaseDatabaseTest {

    @Test
    fun testClassIndicesAllMappedToDiseaseInfo() {
        // Locate assets directory
        val assetsDir = File("src/main/assets")
        val classIndicesFile = File(assetsDir, "class_indices.json")
        val diseaseInfoFile = File(assetsDir, "disease_info.json")

        assertTrue("class_indices.json must exist in assets", classIndicesFile.exists())
        assertTrue("disease_info.json must exist in assets", diseaseInfoFile.exists())

        val classIndicesJson = JSONObject(classIndicesFile.readText())
        val diseaseInfoArray = JSONArray(diseaseInfoFile.readText())

        val diseaseMap = mutableMapOf<String, JSONObject>()
        for (i in 0 until diseaseInfoArray.length()) {
            val item = diseaseInfoArray.getJSONObject(i)
            val className = item.optString("class_name", item.optString("name"))
            diseaseMap[className] = item
        }

        // Verify every single model output class has a corresponding database entry
        val keys = classIndicesJson.keys()
        var classCount = 0
        while (keys.hasNext()) {
            val key = keys.next()
            val className = classIndicesJson.getString(key)
            classCount++

            val record = diseaseMap[className]
            assertNotNull("Class '$className' from class_indices.json must have a database entry", record)

            val plantName = record!!.optString("plant_name")
            val diseaseName = record.optString("disease_name")
            val symptoms = record.optString("symptoms")
            val treatment = record.optString("treatment")
            val prevention = record.optString("prevention")

            assertTrue("plant_name must not be empty for $className", plantName.isNotBlank())
            assertTrue("disease_name must not be empty for $className", diseaseName.isNotBlank())
            assertTrue("symptoms must not be empty for $className", symptoms.isNotBlank())
            assertTrue("treatment must not be empty for $className", treatment.isNotBlank())
            assertTrue("prevention must not be empty for $className", prevention.isNotBlank())
        }

        assertEquals("Total model classes should be 16", 16, classCount)
    }

    @Test
    fun testConfidenceThresholdBoundary() {
        val confidenceThreshold = 0.50f
        val highConfidence = 0.85f
        val lowConfidence = 0.42f

        assertTrue(highConfidence >= confidenceThreshold)
        assertFalse(lowConfidence >= confidenceThreshold)
    }
}
