package com.highcom.bitfieldcalc.ui.structure

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.highcom.bitfieldcalc.data.db.entity.FieldEntity
import com.highcom.bitfieldcalc.ui.manager.StructureViewModel

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
    var expanded by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val allTags = remember(uiState.structures) {
        uiState.structures.mapNotNull { it.structure.tag }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }

    var fields by remember { mutableStateOf(listOf<FieldEntity>()) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedBitWidth by remember { mutableIntStateOf(32) }
    val globalBitLength by viewModel.bitLength.collectAsState()
    val maxBitIndex = selectedBitWidth - 1

    LaunchedEffect(structureId) {
        if (structureId != null) {
            val s = viewModel.getStructureById(structureId)
            if (s != null) {
                name = s.structure.name
                tag = s.structure.tag ?: ""
                fields = s.fields
                selectedBitWidth = s.structure.bitWidth
            }
        } else {
            selectedBitWidth = globalBitLength
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
                            bitWidth = selectedBitWidth,
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

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = tag,
                    onValueChange = {
                        tag = it
                        expanded = true
                    },
                    label = { Text("タグ名") },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryEditable, true),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                )

                val filteredTags = allTags.filter { it.contains(tag, ignoreCase = true) }
                if (filteredTags.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        filteredTags.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    tag = selectionOption
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text("ビット幅", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(8, 16, 32, 64).forEach { width ->
                    FilterChip(
                        selected = selectedBitWidth == width,
                        onClick = { selectedBitWidth = width },
                        label = { Text("${width}bit") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ビット範囲: 0〜${maxBitIndex}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "【フィールド定義リスト】", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    val lastField = fields.lastOrNull()
                    val nextMsb = if (lastField != null) lastField.lsb - 1 else maxBitIndex
                    val safeMsb = nextMsb.coerceAtLeast(0)
                    fields = fields + FieldEntity(
                        structureId = structureId ?: 0L,
                        fieldName = "",
                        msb = safeMsb,
                        lsb = safeMsb
                    )
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Field")
                }
            }

            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                itemsIndexed(fields) { index, field ->
                    FieldEditItem(
                        field = field,
                        previousField = if (index > 0) fields[index - 1] else null,
                        maxBitIndex = maxBitIndex,
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
    previousField: FieldEntity?,
    maxBitIndex: Int,
    onUpdate: (FieldEntity) -> Unit,
    onDelete: () -> Unit
) {
    val bitSize = if (field.msb >= field.lsb) field.msb - field.lsb + 1 else 0

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
                BitIndexInputField(
                    value = field.msb,
                    onValueChange = { onUpdate(field.copy(msb = it)) },
                    label = "MSB",
                    maxBitIndex = maxBitIndex,
                    modifier = Modifier.width(70.dp)
                )
                Text("〜")
                BitIndexInputField(
                    value = field.lsb,
                    onValueChange = { onUpdate(field.copy(lsb = it)) },
                    label = "LSB",
                    maxBitIndex = maxBitIndex,
                    modifier = Modifier.width(70.dp)
                )

                BitSizeInputField(
                    value = bitSize,
                    onValueChange = { newSize ->
                        if (newSize > 0) {
                            val nextMsb = previousField?.let { it.lsb - 1 } ?: maxBitIndex
                            val newMsb = nextMsb.coerceIn(0, maxBitIndex)
                            val newLsb = (newMsb - (newSize - 1)).coerceIn(0, maxBitIndex)
                            onUpdate(field.copy(msb = newMsb, lsb = newLsb))
                        }
                    },
                    maxSize = maxBitIndex + 1,
                    modifier = Modifier.width(80.dp)
                )
            }
        }
    }
}
