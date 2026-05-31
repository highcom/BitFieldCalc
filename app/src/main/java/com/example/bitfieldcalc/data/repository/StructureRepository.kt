package com.example.bitfieldcalc.data.repository

import com.example.bitfieldcalc.data.db.AppDatabase
import com.example.bitfieldcalc.data.db.dao.FieldDao
import com.example.bitfieldcalc.data.db.dao.StructureDao
import com.example.bitfieldcalc.data.db.entity.FieldEntity
import com.example.bitfieldcalc.data.db.entity.StructureEntity
import com.example.bitfieldcalc.data.db.entity.StructureWithFields
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StructureRepository(
    private val db: AppDatabase,
    private val structureDao: StructureDao,
    private val fieldDao: FieldDao
) {
    fun getAllStructuresWithFields(): Flow<List<StructureWithFields>> {
        return structureDao.getAllStructuresWithFields()
    }

    fun getPinnedStructuresWithFields(): Flow<List<StructureWithFields>> {
        return structureDao.getPinnedStructuresWithFields()
    }

    suspend fun getStructureWithFieldsById(id: Long): StructureWithFields? {
        return structureDao.getStructureWithFieldsById(id)
    }

    fun searchStructures(query: String): Flow<List<StructureWithFields>> {
        return structureDao.searchStructuresWithFields(query)
    }

    suspend fun insertStructureWithFields(structure: StructureEntity, fields: List<FieldEntity>) {
        db.withTransaction {
            val newId = structureDao.insert(structure)
            // If it's an update, we replace all fields
            if (structure.id != 0L) {
                fieldDao.deleteByStructureId(structure.id)
            }
            if (fields.isNotEmpty()) {
                val fieldsWithParent = fields.map { it.copy(id = 0L, structureId = newId) }
                fieldDao.insertAll(fieldsWithParent)
            }
        }
    }

    suspend fun updateStructure(structure: StructureEntity) {
        structureDao.update(structure)
    }

    suspend fun deleteStructure(structureId: Long) {
        // cascade will delete fields
        structureDao.deleteById(structureId)
    }

    /**
     * Export all structures to a JSON string. Caller should collect the Flow from getAllStructuresWithFields
     * and then call this method with a serialized list. Here we provide a lightweight helper that accepts
     * a pre-collected list; to keep responsibilities small we add a separate method below.
     */
    fun buildJsonFromStructures(structures: List<StructureWithFields>, appVersion: String = "1.0"): String {
        val arr = JSONArray()
        for (s in structures) {
            val obj = JSONObject()
            obj.put("name", s.structure.name)
            if (s.structure.tag != null) obj.put("tag", s.structure.tag)
            obj.put("is_pinned", s.structure.isPinned)
            obj.put("bit_width", s.structure.bitWidth)
            val fieldsArr = JSONArray()
            for (f in s.fields) {
                val fo = JSONObject()
                fo.put("field_name", f.fieldName)
                fo.put("msb", f.msb)
                fo.put("lsb", f.lsb)
                fo.put("is_signed", f.isSigned)
                fieldsArr.put(fo)
            }
            obj.put("fields", fieldsArr)
            arr.put(obj)
        }
        val root = JSONObject()
        root.put("app_version", appVersion)
        root.put("structures", arr)
        return root.toString(2)
    }

    /**
     * Import structures from a JSON string and persist into DB.
     * Returns the number of structures imported.
     */
    suspend fun importFromJsonString(json: String): Int = withContext(Dispatchers.Default) {
        val obj = JSONObject(json)
        val structures = obj.optJSONArray("structures") ?: JSONArray()
        var imported = 0
        for (i in 0 until structures.length()) {
            val s = structures.getJSONObject(i)
            val name = s.optString("name", "")
            if (name.isEmpty()) continue
            val tag = if (s.has("tag")) s.optString("tag") else null
            val isPinned = s.optBoolean("is_pinned", false)
            val bitWidth = s.optInt("bit_width", 32)
            val fieldsArr = s.optJSONArray("fields") ?: JSONArray()
            val structureEntity = StructureEntity(name = name, tag = tag, isPinned = isPinned, bitWidth = bitWidth)
            val fields = mutableListOf<FieldEntity>()
            for (j in 0 until fieldsArr.length()) {
                val f = fieldsArr.getJSONObject(j)
                val fieldName = f.optString("field_name", "")
                val msb = f.optInt("msb", 0)
                val lsb = f.optInt("lsb", 0)
                val isSigned = f.optBoolean("is_signed", false)
                fields.add(FieldEntity(structureId = 0L, fieldName = fieldName, msb = msb, lsb = lsb, isSigned = isSigned))
            }
            insertStructureWithFields(structureEntity, fields)
            imported++
        }
        imported
    }
}


