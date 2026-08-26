package com.dhritiman.aura.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dhritiman.aura.apps.AppManager
import com.dhritiman.aura.data.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppSelectionViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val appManager =
        AppManager(application)

    private val appPreferences =
        AppPreferences(application)

    private val _selectedApps =
        MutableStateFlow<Set<String>>(
            emptySet()
        )

    val selectedApps: StateFlow<Set<String>> =
        _selectedApps.asStateFlow()

    val installedApps =
        appManager.getInstalledApps()

    init {

        loadSelectedApps()
    }

    private fun loadSelectedApps() {

        viewModelScope.launch {

            appPreferences.selectedApps.collect { apps ->

                _selectedApps.value = apps
            }
        }
    }

    fun setAppSelected(
        packageName: String,
        selected: Boolean
    ) {

        val current =
            _selectedApps.value.toMutableSet()

        if (selected) {

            current.add(packageName)

        } else {

            current.remove(packageName)
        }

        val updated =
            current.toSet()

        _selectedApps.value = updated

        viewModelScope.launch {

            appPreferences.saveSelectedApps(
                updated
            )
        }
    }

    fun saveSelectedApps() {

        viewModelScope.launch {

            appPreferences.saveSelectedApps(
                _selectedApps.value
            )
        }
    }
}