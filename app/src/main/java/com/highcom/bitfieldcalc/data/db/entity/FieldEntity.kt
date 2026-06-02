package com.highcom.bitfieldcalc.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fields",
    foreignKeys = [
        ForeignKey(
            entity = StructureEntity::class,
            parentColumns = ["id"],
            childColumns = ["structure_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["structure_id"])]
)
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


