package com.example.bitfieldcalc.ui.calculation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bitfieldcalc.ui.calculation.components.BitGrid
import com.example.bitfieldcalc.ui.calculation.components.NumberInputFields
import com.example.bitfieldcalc.ui.calculation.components.DecodedFieldsList
import com.example.bitfieldcalc.ui.selector.QuickSelectorBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainCalculationScreen(
    viewModel: BitFieldCalcViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToManager: () -> Unit
) {
    val hex by viewModel.hex.collectAsState()
    val dec by viewModel.dec.collectAsState()
    val bin by viewModel.bin.collectAsState()
    val rawValue by viewModel.rawValue.collectAsState()
    val pinnedStructures by viewModel.pinnedStructures.collectAsState()
    val selectedStructure by viewModel.selectedStructure.collectAsState()
    val decodedResults by viewModel.decodedResults.collectAsState()
    val isMsbFirst by viewModel.isMsbFirst.collectAsState()

    var showSelector by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BitField Calc") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = onNavigateToManager) {
                        Icon(Icons.Default.List, contentDescription = "Manager")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // 上部エリア（スクロール可能）: 画面の約2/3を割り当て
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Pinned structures tabs
                if (pinnedStructures.isNotEmpty()) {
                    ScrollableTabRow(
                        selectedTabIndex = pinnedStructures.indexOfFirst { it.structure.id == selectedStructure?.structure?.id }
                            .coerceAtLeast(0),
                        edgePadding = 8.dp,
                        divider = {}
                    ) {
                        pinnedStructures.forEach { item ->
                            Tab(
                                selected = selectedStructure?.structure?.id == item.structure.id,
                                onClick = { viewModel.selectStructure(item) },
                                text = { Text(item.structure.name) }
                            )
                        }
                    }
                }

                // Current structure selector
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showSelector = true }
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "現在の構造体: ▼ ${selectedStructure?.structure?.name ?: "選択されていません"}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                BitGrid(
                    value = rawValue,
                    isMsbFirst = isMsbFirst,
                    onToggle = { idx -> viewModel.toggleBit(idx) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                NumberInputFields(
                    hex = hex,
                    dec = dec,
                    bin = bin,
                    onHexChanged = { viewModel.updateRawValueFromHex(it) },
                    onDecChanged = { viewModel.updateRawValueFromDec(it) },
                    onBinChanged = { viewModel.updateRawValueFromBin(it) }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // 下部エリア（デコード結果）: 画面の約1/3を割り当て
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                DecodedFieldsList(fields = decodedResults)
            }
        }
    }

    if (showSelector) {
        QuickSelectorBottomSheet(
            viewModel = viewModel,
            onDismiss = { showSelector = false },
            onManageClick = {
                showSelector = false
                onNavigateToManager()
            }
        )
    }
}
