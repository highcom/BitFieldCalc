package com.highcom.bitfieldcalc.data.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class StructureWithFields(
    @Embedded
    val structure: StructureEntity,

    @Relation(parentColumn = "id", entityColumn = "structure_id")
    val fields: List<FieldEntity>
)

