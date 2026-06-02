package com.highcom.bitfieldcalc.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.highcom.bitfieldcalc.data.db.dao.FieldDao
import com.highcom.bitfieldcalc.data.db.dao.StructureDao
import com.highcom.bitfieldcalc.data.db.entity.FieldEntity
import com.highcom.bitfieldcalc.data.db.entity.StructureEntity

@Database(entities = [StructureEntity::class, FieldEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun structureDao(): StructureDao
    abstract fun fieldDao(): FieldDao
}

