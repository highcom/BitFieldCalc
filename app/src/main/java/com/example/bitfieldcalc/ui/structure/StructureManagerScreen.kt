package com.example.bitfieldcalc.ui.structure

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Simple skeleton for the Structure Manager screen.
 * onAdd: invoked when user requests to add new structure
 * onEdit: invoked with structureId when edit requested
 */
@Composable
fun StructureManagerScreen(
    structures: List<com.example.bitfieldcalc.data.db.entity.StructureWithFields> = emptyList(),
    onAdd: () -> Unit = {},
    onEdit: (Long) -> Unit = {}
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Card(modifier = Modifier.fillMaxWidth().padding(4.dp).clickable { onAdd() }) {
            Text("+ New Structure", modifier = Modifier.padding(12.dp))
        }

        for (s in structures) {
            Card(modifier = Modifier.fillMaxWidth().padding(4.dp).clickable { onEdit(s.structure.id) }) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = s.structure.name)
                    Text(text = s.structure.tag ?: "No tag")
                    Text(text = "Fields: ${s.fields.size}")
                }
            }
        }
    }
}

