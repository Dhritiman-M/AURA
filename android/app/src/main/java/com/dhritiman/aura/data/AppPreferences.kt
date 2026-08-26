package com.dhritiman.aura.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "aura_preferences"
)

class AppPreferences(
    private val context: Context
) {

    companion object {

        private val SELECTED_APPS =
            stringSetPreferencesKey(
                "selected_apps"
            )
    }

    val selectedApps: Flow<Set<String>> =
        context.dataStore.data.map { preferences ->

            preferences[
                SELECTED_APPS
            ] ?: emptySet()
        }

    suspend fun saveSelectedApps(
        apps: Set<String>
    ) {

        context.dataStore.edit { preferences ->

            preferences[
                SELECTED_APPS
            ] = apps
        }
    }
}