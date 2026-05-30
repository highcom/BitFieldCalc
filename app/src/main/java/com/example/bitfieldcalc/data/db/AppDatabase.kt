package com.example.bitfieldcalc.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bitfieldcalc.data.db.dao.FieldDao
import com.example.bitfieldcalc.data.db.dao.StructureDao
import com.example.bitfieldcalc.data.db.entity.FieldEntity
import com.example.bitfieldcalc.data.db.entity.StructureEntity

@Database(entities = [StructureEntity::class, FieldEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun structureDao(): StructureDao
    abstract fun fieldDao(): FieldDao
}

