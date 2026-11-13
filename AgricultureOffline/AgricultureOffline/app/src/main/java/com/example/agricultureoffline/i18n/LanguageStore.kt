package com.example.agricultureoffline.i18n

import android.content.Context
import android.content.res.Configuration
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

object LanguageStore {
    private const val PREFS = "agrivision_prefs"
    private const val KEY = "app_language"
    const val DEFAULT = "en"
    val supported = listOf("en", "hi", "pa")

    fun get(context: Context): String {
        val value = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, DEFAULT) ?: DEFAULT
        return if (value in supported) value else DEFAULT
    }

    fun set(context: Context, language: String) {
        val normalized = if (language in supported) language else DEFAULT
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, normalized)
            .apply()
    }
}

val LocalAppLanguage = staticCompositionLocalOf { LanguageStore.DEFAULT }
val LocalSetAppLanguage = staticCompositionLocalOf<(String) -> Unit> { {} }

fun Context.localized(language: String): Context {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    return createConfigurationContext(config)
}

@Composable
fun LocalizedApp(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val languageState = remember { mutableStateOf(LanguageStore.get(appContext)) }
    val localizedContext = remember(languageState.value, context) {
        context.localized(languageState.value)
    }

    val registryOwner = context as? ActivityResultRegistryOwner
    val providers = mutableListOf<ProvidedValue<*>>(
        LocalContext provides localizedContext,
        LocalAppLanguage provides languageState.value,
        LocalSetAppLanguage provides { code ->
            LanguageStore.set(appContext, code)
            languageState.value = code
        }
    )
    if (registryOwner != null) {
        providers.add(LocalActivityResultRegistryOwner provides registryOwner)
    }

    CompositionLocalProvider(*providers.toTypedArray()) {
        content()
    }
}
