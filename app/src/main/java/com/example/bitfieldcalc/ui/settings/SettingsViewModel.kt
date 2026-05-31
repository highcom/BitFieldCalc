package com.example.bitfieldcalc.ui.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.bitfieldcalc.data.repository.StructureRepository
import com.example.bitfieldcalc.data.repository.SettingsRepository
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: StructureRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val isBigEndian: StateFlow<Boolean> = settingsRepository.isBigEndian
    val isMsbFirst: StateFlow<Boolean> = settingsRepository.isMsbFirst
    val bitLength: StateFlow<Int> = settingsRepository.bitLength

    fun saveEnvironmentSettings(isBigEndian: Boolean, isMsbFirst: Boolean, bitLength: Int) {
        settingsRepository.setBigEndian(isBigEndian)
        settingsRepository.setMsbFirst(isMsbFirst)
        settingsRepository.setBitLength(bitLength)
    }

    suspend fun exportStructuresToJsonString(appVersion: String = "1.0"): String {
        // Collect structures and build JSON
        return "{}" 
    }

    suspend fun importStructuresFromJsonString(json: String): Int {
        return repository.importFromJsonString(json)
    }
}
