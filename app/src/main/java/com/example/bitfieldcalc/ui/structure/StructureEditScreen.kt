package com.example.bitfieldcalc.ui.structure

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bitfieldcalc.data.db.entity.FieldEntity
import com.example.bitfieldcalc.ui.manager.StructureViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StructureEditScreen(
    viewModel: StructureViewModel,
    structureId: Long?,
    onDone: () -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var fields by remember { mutableStateOf(listOf<FieldEntity>()) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(structureId) {
        if (structureId != null) {
            val s = viewModel.getStructureById(structureId)
            if (s != null) {
                name = s.structure.name
                tag = s.structure.tag ?: ""
                fields = s.fields
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (structureId == null) "構造体の作成" else "構造体の編集") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancel")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        val (success, msg) = viewModel.validateAndSaveStructure(
                            id = structureId ?: 0L,
                            structureName = name,
                            tag = tag.ifBlank { null },
                            fields = fields
                        )
                        if (success) {
                            onDone()
                        } else {
                            error = msg
                        }
                    }) {
                        Text("保存")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (error != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("構造体名") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = tag,
                onValueChange = { tag = it },
                label = { Text("タグ名") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "【フィールド定義リスト】", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    fields = fields + FieldEntity(structureId = structureId ?: 0L, fieldName = "", msb = 0, lsb = 0)
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Field")
                }
            }

            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                itemsIndexed(fields) { index, field ->
                    FieldEditItem(
                        field = field,
                        onUpdate = { updated ->
                            fields = fields.toMutableList().apply { set(index, updated) }
                        },
                        onDelete = {
                            fields = fields.toMutableList().apply { removeAt(index) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FieldEditItem(
    field: FieldEntity,
    onUpdate: (FieldEntity) -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = field.fieldName,
                    onValueChange = { onUpdate(field.copy(fieldName = it)) },
                    label = { Text("名前") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Field")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = field.msb.toString(),
                    onValueChange = { onUpdate(field.copy(msb = it.toIntOrNull() ?: 0)) },
                    label = { Text("MSB") },
                    modifier = Modifier.width(80.dp)
                )
                Text("〜")
                OutlinedTextField(
                    value = field.lsb.toString(),
                    onValueChange = { onUpdate(field.copy(lsb = it.toIntOrNull() ?: 0)) },
                    label = { Text("LSB") },
                    modifier = Modifier.width(80.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = field.isSigned,
                        onCheckedChange = { onUpdate(field.copy(isSigned = it)) }
                    )
                    Text("Signed")
                }
            }
        }
    }
}
