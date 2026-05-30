package com.example.bitfieldcalc.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "fields")
data class FieldEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "structure_id")
    val structureId: Long,

    @ColumnInfo(name = "field_name")
    val fieldName: String,

    @ColumnInfo(name = "msb")
    val msb: Int,

    @ColumnInfo(name = "lsb")
    val lsb: Int,

    @ColumnInfo(name = "is_signed")
    val isSigned: Boolean = false
)


