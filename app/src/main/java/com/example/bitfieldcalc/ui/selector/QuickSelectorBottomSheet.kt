package com.example.bitfieldcalc.ui.selector

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
import androidx.compose.ui.unit.dp
import com.example.bitfieldcalc.data.db.entity.StructureWithFields
import com.example.bitfieldcalc.ui.calculation.BitFieldCalcViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickSelectorBottomSheet(
    viewModel: BitFieldCalcViewModel,
    onDismiss: () -> Unit,
    onManageClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val structures by viewModel.searchStructures(searchQuery).collectAsState(initial = emptyList())

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxHeight(0.8f).padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "構造体の選択", style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = onManageClick) {
                    Text("構造体マネージャー")
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                placeholder = { Text("🔍 構造体名やタグで検索...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                val pinned = structures.filter { it.structure.isPinned }
                val others = structures.filter { !it.structure.isPinned }

                if (pinned.isNotEmpty()) {
                    item {
                        Text(
                            text = "お気に入り",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(pinned) { item ->
                        StructureItem(item) {
                            viewModel.selectStructure(item)
                            onDismiss()
                        }
                    }
                }

                if (others.isNotEmpty()) {
                    item {
                        Text(
                            text = "すべての構造体",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(others) { item ->
                        StructureItem(item) {
                            viewModel.selectStructure(item)
                            onDismiss()
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
                    text = "タグ: ${item.structure.tag}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
