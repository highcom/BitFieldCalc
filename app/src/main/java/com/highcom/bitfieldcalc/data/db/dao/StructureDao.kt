package com.highcom.bitfieldcalc.data.db.dao

import androidx.room.*
import com.highcom.bitfieldcalc.data.db.entity.StructureEntity
import com.highcom.bitfieldcalc.data.db.entity.StructureWithFields
import kotlinx.coroutines.flow.Flow

@Dao
interface StructureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(structure: StructureEntity): Long

    @Update
    suspend fun update(structure: StructureEntity): Int

    @Query("DELETE FROM structures WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Transaction
    @Query("SELECT * FROM structures WHERE id = :id")
    suspend fun getStructureWithFieldsById(id: Long): StructureWithFields?

    @Transaction
    @Query("SELECT * FROM structures WHERE is_pinned = 1 ORDER BY sort_order")
    fun getPinnedStructuresWithFields(): Flow<List<@JvmSuppressWildcards StructureWithFields>>

    @Transaction
    @Query("SELECT * FROM structures ORDER BY sort_order")
    fun getAllStructuresWithFields(): Flow<List<@JvmSuppressWildcards StructureWithFields>>

    @Transaction
    @Query("SELECT * FROM structures WHERE name LIKE '%' || :query || '%' OR tag LIKE '%' || :query || '%' ORDER BY sort_order")
    fun searchStructuresWithFields(query: String): Flow<List<@JvmSuppressWildcards StructureWithFields>>
}

