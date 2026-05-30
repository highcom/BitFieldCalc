package com.example.bitfieldcalc.ui.manager

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import com.example.bitfieldcalc.data.db.entity.StructureEntity
import com.example.bitfieldcalc.data.db.entity.FieldEntity
import com.example.bitfieldcalc.data.repository.StructureRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class StructureViewModel @Inject constructor(
    private val repository: StructureRepository
) : ViewModel() {
    private var pendingDeleteJob: Job? = null
    private var pendingDeletedId: Long? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun validateAndSaveStructure(structureName: String, tag: String?, fields: List<FieldEntity>): Boolean {
        // simple validation: bit overlap
        val ranges = mutableListOf<IntRange>()
        for (f in fields) {
            if (f.msb < f.lsb) return false
            ranges.add(f.lsb..f.msb)
        }
        for (i in ranges.indices) {
            for (j in i + 1 until ranges.size) {
                if (ranges[i].intersect(ranges[j]).isNotEmpty()) return false
            }
        }

        // persist
        scope.launch {
            val entity = StructureEntity(name = structureName, tag = tag)
            repository.insertStructureWithFields(entity, fields)
        }
        return true
    }

    fun pendingDeleteStructure(structureId: Long) {
        pendingDeleteJob?.cancel()
        pendingDeletedId = structureId
        pendingDeleteJob = scope.launch {
            delay(3000)
            pendingDeletedId?.let { repository.deleteStructure(it) }
            pendingDeletedId = null
        }
    }

    fun cancelDeleteStructure() {
        pendingDeleteJob?.cancel()
        pendingDeletedId = null
    }

    fun updateSortOrder(structures: List<StructureEntity>) {
        scope.launch {
            for ((index, s) in structures.withIndex()) {
                repository.insertStructureWithFields(s.copy(sortOrder = index), emptyList())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}


