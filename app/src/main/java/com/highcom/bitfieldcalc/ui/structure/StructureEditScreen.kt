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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.highcom.bitfieldcalc.R
import com.highcom.bitfieldcalc.data.db.entity.FieldEntity
import com.highcom.bitfieldcalc.ui.manager.StructureViewModel
import com.highcom.bitfieldcalc.ui.manager.ValidationResult

@Composable
fun StructureEditScreen(
    viewModel: StructureViewModel,
    structureId: Long?,
    onDone: () -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val allTags = remember(uiState.structures) {
        uiState.structures.mapNotNull { it.structure.tag }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }

    var fields by remember { mutableStateOf(listOf<FieldEntity>()) }
    var validationError by remember { mutableStateOf<ValidationResult?>(null) }
    var selectedBitWidth by remember { mutableIntStateOf(32) }
    val globalBitLength by viewModel.bitLength.collectAsState()

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

    StructureEditContent(
        structureId = structureId,
        name = name,
        tag = tag,
        allTags = allTags,
        fields = fields,
        validationError = validationError,
        selectedBitWidth = selectedBitWidth,
        onNameChange = { name = it },
        onTagChange = { tag = it },
        onFieldsChange = { fields = it },
        onBitWidthChange = { selectedBitWidth = it },
        onSave = {
            val result = viewModel.validateAndSaveStructure(
                id = structureId ?: 0L,
                structureName = name,
                tag = tag.ifBlank { null },
                bitWidth = selectedBitWidth,
                fields = fields
            )
            if (result.success) {
                onDone()
            } else {
                validationError = result
            }
        },
        onCancel = onCancel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StructureEditContent(
    structureId: Long?,
    name: String,
    tag: String,
    allTags: List<String>,
    fields: List<FieldEntity>,
    validationError: ValidationResult?,
    selectedBitWidth: Int,
    onNameChange: (String) -> Unit,
    onTagChange: (String) -> Unit,
    onFieldsChange: (List<FieldEntity>) -> Unit,
    onBitWidthChange: (Int) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val maxBitIndex = selectedBitWidth - 1

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(if (structureId == null) R.string.create_structure else R.string.edit_structure)) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cancel))
                    }
                },
                actions = {
                    TextButton(onClick = onSave) {
                        Text(stringResource(R.string.save))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (validationError != null && !validationError.success && validationError.errorResId != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = stringResource(validationError.errorResId, *validationError.errorArgs.toTypedArray()),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.structure_name)) },
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
                        onTagChange(it)
                        expanded = true
                    },
                    label = { Text(stringResource(R.string.tag_name)) },
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
                                    onTagChange(selectionOption)
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(stringResource(R.string.bit_width), style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(8, 16, 32, 64).forEach { width ->
                    FilterChip(
                        selected = selectedBitWidth == width,
                        onClick = { onBitWidthChange(width) },
                        label = { Text("${width}bit") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.bit_range, maxBitIndex),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.field_definition_list), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    val lastField = fields.lastOrNull()
                    val nextMsb = if (lastField != null) lastField.lsb - 1 else maxBitIndex
                    val safeMsb = nextMsb.coerceAtLeast(0)
                    onFieldsChange(fields + FieldEntity(
                        structureId = structureId ?: 0L,
                        fieldName = "",
                        msb = safeMsb,
                        lsb = safeMsb
                    ))
                }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_field))
                }
            }

            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                itemsIndexed(fields) { index, field ->
                    FieldEditItem(
                        field = field,
                        previousField = if (index > 0) fields[index - 1] else null,
                        maxBitIndex = maxBitIndex,
                        onUpdate = { updated ->
                            onFieldsChange(fields.toMutableList().apply { set(index, updated) })
                        },
                        onDelete = {
                            onFieldsChange(fields.toMutableList().apply { removeAt(index) })
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StructureEditScreenPreview() {
    MaterialTheme {
        StructureEditContent(
            structureId = null,
            name = "New Structure",
            tag = "Test",
            allTags = listOf("MCU", "Peripheral"),
            fields = listOf(
                FieldEntity(fieldName = "Field A", msb = 7, lsb = 0, structureId = 0),
                FieldEntity(fieldName = "Field B", msb = 15, lsb = 8, structureId = 0)
            ),
            validationError = null,
            selectedBitWidth = 32,
            onNameChange = {},
            onTagChange = {},
            onFieldsChange = {},
            onBitWidthChange = {},
            onSave = {},
            onCancel = {}
        )
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
                    label = { Text(stringResource(R.string.name)) },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_field))
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
                Text(stringResource(R.string.range_separator))
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
