package com.example.bitfieldcalc.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bitfieldcalc.data.db.entity.FieldEntity

@Dao
interface FieldDao {
    @Insert
    suspend fun insertAll(fields: List<FieldEntity>): List<Long>

    @Query("DELETE FROM fields WHERE structure_id = :structureId")
    suspend fun deleteByStructureId(structureId: Long): Int

    @Query("SELECT * FROM fields WHERE structure_id = :structureId ORDER BY msb DESC")
    suspend fun getFieldsByStructureId(structureId: Long): List<FieldEntity>
}

