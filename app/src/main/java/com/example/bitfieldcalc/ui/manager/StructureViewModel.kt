package com.example.bitfieldcalc.ui.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bitfieldcalc.data.db.entity.FieldEntity
import com.example.bitfieldcalc.data.db.entity.StructureEntity
import com.example.bitfieldcalc.data.db.entity.StructureWithFields
import com.example.bitfieldcalc.data.repository.StructureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StructureViewModel @Inject constructor(
    private val repository: StructureRepository
) : ViewModel() {
    private var pendingDeleteJob: Job? = null
    private var pendingDeletedId: Long? = null

    private val _uiState = MutableStateFlow<StructureManagerUiState>(StructureManagerUiState())
    val uiState: StateFlow<StructureManagerUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllStructuresWithFields().collect { list ->
                _uiState.emit(_uiState.value.copy(structures = list))
            }
        }
    }

    fun getAllStructures() = repository.getAllStructuresWithFields()

    suspend fun getStructureById(id: Long): StructureWithFields? {
        return repository.getStructureWithFieldsById(id)
    }

    fun validateAndSaveStructure(
        id: Long,
        structureName: String,
        tag: String?,
        fields: List<FieldEntity>
    ): Pair<Boolean, String?> {
        if (structureName.isBlank()) return false to "構造体名を入力してください"

        val ranges = mutableListOf<Pair<IntRange, String>>()
        for (f in fields) {
            if (f.fieldName.isBlank()) return false to "フィールド名を入力してください"
            if (f.msb < 0 || f.msb > 63 || f.lsb < 0 || f.lsb > 63) return false to "ビット範囲は0〜63の間である必要があります (${f.fieldName})"
            if (f.msb < f.lsb) return false to "MSBはLSB以上である必要があります (${f.fieldName})"
            ranges.add((f.lsb..f.msb) to f.fieldName)
        }

        for (i in ranges.indices) {
            for (j in i + 1 until ranges.size) {
                if (ranges[i].first.intersect(ranges[j].first).isNotEmpty()) {
                    return false to "ビット範囲が重複しています: ${ranges[i].second} と ${ranges[j].second}"
                }
            }
        }

        viewModelScope.launch {
            val entity = StructureEntity(id = id, name = structureName, tag = tag)
            repository.insertStructureWithFields(entity, fields)
        }
        return true to null
    }

    fun pendingDeleteStructure(structure: StructureWithFields) {
        pendingDeleteJob?.cancel()
        pendingDeletedId = structure.structure.id
        
        // Temporarily hide from UI
        _uiState.value = _uiState.value.copy(
            structures = _uiState.value.structures.filter { it.structure.id != structure.structure.id },
            lastDeletedStructure = structure
        )

        pendingDeleteJob = viewModelScope.launch {
            delay(3000)
            pendingDeletedId?.let { repository.deleteStructure(it) }
            pendingDeletedId = null
            _uiState.emit(_uiState.value.copy(lastDeletedStructure = null))
        }
    }

    fun restoreDeletedStructure() {
        val structure = _uiState.value.lastDeletedStructure ?: return
        pendingDeleteJob?.cancel()
        pendingDeletedId = null
        
        // Restore to UI list
        viewModelScope.launch {
            // Re-fetch or just let the repository flow update it. 
            // Since we canceled the job, the DB was never touched.
            // But we manually filtered the UI state, so we need to refresh it or wait for flow.
            _uiState.emit(_uiState.value.copy(lastDeletedStructure = null))
        }
    }

    fun updateSortOrder(structures: List<StructureEntity>) {
        viewModelScope.launch {
            for ((index, s) in structures.withIndex()) {
                repository.updateStructure(s.copy(sortOrder = index))
            }
        }
    }
}

data class StructureManagerUiState(
    val structures: List<StructureWithFields> = emptyList(),
    val lastDeletedStructure: StructureWithFields? = null
)
