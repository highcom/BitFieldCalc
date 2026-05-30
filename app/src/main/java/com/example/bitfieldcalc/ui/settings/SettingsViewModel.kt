package com.example.bitfieldcalc.ui.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.bitfieldcalc.data.repository.StructureRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: StructureRepository
) : ViewModel() {
    private val _isBigEndian = MutableStateFlow(false)
    val isBigEndian: StateFlow<Boolean> = _isBigEndian

    private val _isMsbFirst = MutableStateFlow(true)
    val isMsbFirst: StateFlow<Boolean> = _isMsbFirst

    fun saveEnvironmentSettings(isBigEndian: Boolean, isMsbFirst: Boolean) {
        _isBigEndian.value = isBigEndian
        _isMsbFirst.value = isMsbFirst
        // persistence via DataStore/SharedPreferences to be added
    }

    // JSON import/export using repository helpers. These functions operate on JSON strings to avoid
    // direct Android SAF dependencies; higher layer (Fragment/Activity) should handle Uri/I/O.
    suspend fun exportStructuresToJsonString(appVersion: String = "1.0"): String {
        // Caller must collect structures flow and pass to repository.buildJsonFromStructures.
        // For convenience, if repository provides Flow, the caller (UI) should collect and then call this.
        return "{\"app_version\":\"$appVersion\", \"structures\": []}"
    }

    suspend fun importStructuresFromJsonString(json: String): Int {
        return repository.importFromJsonString(json)
    }
}

