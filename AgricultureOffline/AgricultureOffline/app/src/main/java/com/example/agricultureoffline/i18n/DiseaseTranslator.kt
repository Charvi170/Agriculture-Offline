package com.example.agricultureoffline.i18n

import android.content.Context
import com.example.agricultureoffline.data.local.Disease
import org.json.JSONObject

data class LocalizedDisease(
    val className: String,
    val plantName: String,
    val diseaseName: String,
    val symptoms: String,
    val treatment: String,
    val prevention: String
)

object DiseaseTranslator {
    @Volatile
    private var translations: JSONObject? = null

    fun localize(context: Context, disease: Disease, language: String): LocalizedDisease {
        val node = nodeFor(context, disease.className, language)
        return LocalizedDisease(
            className = disease.className,
            plantName = node?.optString("plant_name").orFallback(disease.plantName),
            diseaseName = node?.optString("disease_name").orFallback(disease.diseaseName.ifBlank { disease.className }),
            symptoms = node?.optString("symptoms").orFallback(disease.symptoms),
            treatment = node?.optString("treatment").orFallback(disease.treatment),
            prevention = node?.optString("prevention").orFallback(disease.prevention)
        )
    }

    fun diseaseName(context: Context, className: String, fallback: String, language: String): String {
        return nodeFor(context, className, language)
            ?.optString("disease_name")
            .orFallback(fallback)
    }

    private fun nodeFor(context: Context, className: String, language: String): JSONObject? {
        if (language == LanguageStore.DEFAULT || className.isBlank()) return null
        val root = load(context)
        val langNode = root.optJSONObject(language) ?: return null
        return langNode.optJSONObject(className)
    }

    private fun load(context: Context): JSONObject {
        translations?.let { return it }
        synchronized(this) {
            translations?.let { return it }
            val json = context.assets.open("disease_translations.json")
                .bufferedReader()
                .use { it.readText() }
            val parsed = JSONObject(json)
            translations = parsed
            return parsed
        }
    }

    private fun String?.orFallback(fallback: String): String {
        return if (this.isNullOrBlank()) fallback else this
    }
}
