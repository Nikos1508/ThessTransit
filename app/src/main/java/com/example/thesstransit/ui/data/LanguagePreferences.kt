package com.example.thesstransit.ui.data

import android.content.Context
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.gitlab.mitsiosm.oseth.data.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.languageDataStore by preferencesDataStore("language_preferences")

class LanguagePreferences(
    private val context: Context
) {
    companion object {
        private val LANGUAGE =
            stringPreferencesKey("language")
    }

    val language: Flow<Language> =
        context.languageDataStore.data.map { pref ->

            when (pref[LANGUAGE]) {
                "EN" -> Language.ENGLISH
                else -> Language.GREEK
            }
        }

    suspend fun save(language: Language) {

        context.languageDataStore.edit {
            it[LANGUAGE] =
                if (language == Language.ENGLISH)
                    "EN"
                else
                    "GR"
        }
    }
}