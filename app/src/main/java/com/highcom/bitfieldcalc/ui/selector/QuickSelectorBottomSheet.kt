package com.highcom.bitfieldcalc.ui.selector

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.highcom.bitfieldcalc.ui.calculation.BitFieldCalcViewModel

@Composable
fun QuickSelectorBottomSheet(
    viewModel: BitFieldCalcViewModel,
    onDismiss: () -> Unit,
    onManageClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val structures by viewModel.searchStructures(searchQuery).collectAsState(initial = emptyList())

    QuickSelectorContent(
        searchQuery = searchQuery,
        structures = structures,
        onSearchQueryChange = { searchQuery = it },
        onDismiss = onDismiss,
        onManageClick = onManageClick,
        onSelectStructure = {
            viewModel.selectStructure(it)
            onDismiss()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickSelectorContent(
    searchQuery: String,
    structures: List<StructureWithFields>,
    onSearchQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onManageClick: () -> Unit,
    onSelectStructure: (StructureWithFields) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxHeight(0.8f).padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.select_structure), style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = onManageClick) {
                    Text(stringResource(R.string.structure_manager))
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                val pinned = structures.filter { it.structure.isPinned }
                val others = structures.filter { !it.structure.isPinned }

                if (pinned.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.favorites),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(pinned) { item ->
                        StructureItem(item) {
                            onSelectStructure(item)
                        }
                    }
                }

                if (others.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.all_structures),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(others) { item ->
                        StructureItem(item) {
                            onSelectStructure(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StructureItem(item: StructureWithFields, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = if (item.structure.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(end = 12.dp)
        )
        Column {
            Text(text = item.structure.name, style = MaterialTheme.typography.bodyLarge)
            if (item.structure.tag != null) {
                Text(
                    text = stringResource(R.string.tag_label, item.structure.tag),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun QuickSelectorBottomSheetPreview() {
    val mockStructures = listOf(
        StructureWithFields(
            structure = StructureEntity(id = 1, name = "GPIO Config", tag = "MCU", isPinned = true),
            fields = emptyList()
        ),
        StructureWithFields(
            structure = StructureEntity(id = 2, name = "Timer Control", tag = "Peripheral"),
            fields = emptyList()
        )
    )

    MaterialTheme {
        // ModalBottomSheet won't show up correctly in Preview if not in a Scaffold/Box
        QuickSelectorContent(
            searchQuery = "",
            structures = mockStructures,
            onSearchQueryChange = {},
            onDismiss = {},
            onManageClick = {},
            onSelectStructure = {}
        )
    }
}

