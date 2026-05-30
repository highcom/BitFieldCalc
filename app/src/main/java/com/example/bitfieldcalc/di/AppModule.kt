package com.example.bitfieldcalc.di

import android.content.Context
import androidx.room.Room
import com.example.bitfieldcalc.data.db.AppDatabase
import com.example.bitfieldcalc.data.db.dao.FieldDao
import com.example.bitfieldcalc.data.db.dao.StructureDao
import com.example.bitfieldcalc.data.repository.StructureRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "bitfield-db").build()
    }

    @Provides
    fun provideStructureDao(db: AppDatabase): StructureDao = db.structureDao()

    @Provides
    fun provideFieldDao(db: AppDatabase): FieldDao = db.fieldDao()

    @Provides
    @Singleton
    fun provideStructureRepository(db: AppDatabase, structureDao: StructureDao, fieldDao: FieldDao): StructureRepository {
        return StructureRepository(db, structureDao, fieldDao)
    }
}

