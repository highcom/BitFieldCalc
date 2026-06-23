package com.highcom.bitfieldcalc.ui.structure

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.highcom.bitfieldcalc.R
import com.highcom.bitfieldcalc.data.db.entity.FieldEntity
import com.highcom.bitfieldcalc.data.db.entity.StructureEntity
import com.highcom.bitfieldcalc.data.db.entity.StructureWithFields
import com.highcom.bitfieldcalc.ui.manager.StructureManagerUiState
import com.highcom.bitfieldcalc.ui.manager.StructureViewModel

@Composable
fun StructureManagerScreen(
    viewModel: StructureViewModel,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    StructureManagerContent(
        uiState = uiState,
        onBack = onBack,
        onAdd = onAdd,
        onEdit = onEdit,
        onDelete = { viewModel.pendingDeleteStructure(it) },
        onRestore = { viewModel.restoreDeletedStructure() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StructureManagerContent(
    uiState: StructureManagerUiState,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (StructureWithFields) -> Unit,
    onRestore: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val allLabel = stringResource(R.string.all)
    val deletedMessage = stringResource(R.string.deleted_message, uiState.lastDeletedStructure?.structure?.name ?: "")
    val undoLabel = stringResource(R.string.undo)

    LaunchedEffect(uiState.lastDeletedStructure) {
        if (uiState.lastDeletedStructure != null) {
            val result = snackbarHostState.showSnackbar(
                message = deletedMessage,
                actionLabel = undoLabel,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                onRestore()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.structure_manager)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = onAdd) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            var selectedTag by remember { mutableStateOf(allLabel) }
            val tags = listOf(allLabel) + uiState.structures.mapNotNull { it.structure.tag }.distinct()
            
            ScrollableTabRow(
                selectedTabIndex = tags.indexOf(selectedTag).coerceAtLeast(0),
                edgePadding = 8.dp,
                divider = {}
            ) {
                tags.forEach { tag ->
                    Tab(
                        selected = selectedTag == tag,
                        onClick = { selectedTag = tag },
                        text = { Text(tag) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                val filtered = if (selectedTag == allLabel) {
                    uiState.structures
                } else {
                    uiState.structures.filter { it.structure.tag == selectedTag }
                }

                items(filtered, key = { it.structure.id }) { item ->
                    StructureListItem(
                        item = item,
                        onEdit = { onEdit(item.structure.id) },
                        onDelete = { onDelete(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun StructureListItem(
    item: StructureWithFields,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onEdit() }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.reorder), modifier = Modifier.padding(end = 12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item.structure.isPinned) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = stringResource(R.string.pinned),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp).padding(end = 4.dp)
                        )
                    }
                    Text(text = item.structure.name, style = MaterialTheme.typography.titleMedium)
                }
                Text(
                    text = "${item.structure.bitWidth}bit, ${item.fields.size} fields" + (item.structure.tag?.let { " [${it}]" } ?: ""),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StructureManagerScreenPreview() {
    val mockStructures = listOf(
        StructureWithFields(
            structure = StructureEntity(id = 1, name = "GPIO Config", tag = "MCU"),
            fields = listOf(FieldEntity(structureId = 1, fieldName = "EN", msb = 0, lsb = 0))
        ),
        StructureWithFields(
            structure = StructureEntity(id = 2, name = "Timer Control", tag = "Peripheral", isPinned = true),
            fields = listOf(FieldEntity(structureId = 2, fieldName = "MODE", msb = 3, lsb = 0))
        )
    )

    MaterialTheme {
        StructureManagerContent(
            uiState = StructureManagerUiState(structures = mockStructures),
            onBack = {},
            onAdd = {},
            onEdit = {},
            onDelete = {},
            onRestore = {}
        )
    }
}

