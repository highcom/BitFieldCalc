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
import androidx.compose.ui.unit.dp
import com.highcom.bitfieldcalc.data.db.entity.StructureWithFields
import com.highcom.bitfieldcalc.ui.manager.StructureViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StructureManagerScreen(
    viewModel: StructureViewModel,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.lastDeletedStructure) {
        if (uiState.lastDeletedStructure != null) {
            val result = snackbarHostState.showSnackbar(
                message = "${uiState.lastDeletedStructure?.structure?.name} を削除しました",
                actionLabel = "取り消す",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.restoreDeletedStructure()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("構造体マネージャー") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onAdd) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Tag Filter (simplified)
            var selectedTag by remember { mutableStateOf("すべて") }
            val tags = listOf("すべて") + uiState.structures.mapNotNull { it.structure.tag }.distinct()
            
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
                val filtered = if (selectedTag == "すべて") {
                    uiState.structures
                } else {
                    uiState.structures.filter { it.structure.tag == selectedTag }
                }

                items(filtered, key = { it.structure.id }) { item ->
                    StructureListItem(
                        item = item,
                        onEdit = { onEdit(item.structure.id) },
                        onDelete = { viewModel.pendingDeleteStructure(item) }
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
            Icon(Icons.Default.Menu, contentDescription = "Reorder", modifier = Modifier.padding(end = 12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item.structure.isPinned) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Pinned",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp).padding(end = 4.dp)
                        )
                    }
                    Text(text = item.structure.name, style = MaterialTheme.typography.titleMedium)
                }
                Text(
                    text = "${item.structure.bitWidth}bit, ${item.fields.size} fields" + (item.structure.tag?.let { " [$it]" } ?: ""),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
