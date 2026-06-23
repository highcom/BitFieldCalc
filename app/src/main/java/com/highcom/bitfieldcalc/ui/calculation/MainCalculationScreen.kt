package com.highcom.bitfieldcalc.ui.calculation

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.highcom.bitfieldcalc.data.db.entity.FieldEntity
import com.highcom.bitfieldcalc.data.db.entity.StructureEntity
import com.highcom.bitfieldcalc.data.db.entity.StructureWithFields
import com.highcom.bitfieldcalc.ui.calculation.components.BitGrid
import com.highcom.bitfieldcalc.ui.calculation.components.NumberInputFields
import com.highcom.bitfieldcalc.ui.calculation.components.DecodedFieldsList
import com.highcom.bitfieldcalc.ui.selector.QuickSelectorBottomSheet
import java.math.BigInteger

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
    val bitLength by viewModel.bitLength.collectAsState()

    var showSelector by remember { mutableStateOf(false) }

    MainCalculationContent(
        hex = hex,
        dec = dec,
        bin = bin,
        rawValue = rawValue,
        pinnedStructures = pinnedStructures,
        selectedStructure = selectedStructure,
        decodedResults = decodedResults,
        isMsbFirst = isMsbFirst,
        bitLength = bitLength,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToManager = onNavigateToManager,
        onSelectStructure = { viewModel.selectStructure(it) },
        onToggleBit = { viewModel.toggleBit(it) },
        onHexChanged = { viewModel.updateRawValueFromHex(it) },
        onDecChanged = { viewModel.updateRawValueFromDec(it) },
        onBinChanged = { viewModel.updateRawValueFromBin(it) },
        onShowSelector = { showSelector = true }
    )

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainCalculationContent(
    hex: String,
    dec: String,
    bin: String,
    rawValue: BigInteger,
    pinnedStructures: List<StructureWithFields>,
    selectedStructure: StructureWithFields?,
    decodedResults: List<FieldResult>,
    isMsbFirst: Boolean,
    bitLength: Int,
    onNavigateToSettings: () -> Unit,
    onNavigateToManager: () -> Unit,
    onSelectStructure: (StructureWithFields) -> Unit,
    onToggleBit: (Int) -> Unit,
    onHexChanged: (String) -> Unit,
    onDecChanged: (String) -> Unit,
    onBinChanged: (String) -> Unit,
    onShowSelector: () -> Unit
) {
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
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
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
                                onClick = { onSelectStructure(item) },
                                text = { Text(item.structure.name) }
                            )
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onShowSelector() }
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
                    bitLength = bitLength,
                    onToggle = onToggleBit
                )

                Spacer(modifier = Modifier.height(12.dp))

                NumberInputFields(
                    hex = hex,
                    dec = dec,
                    bin = bin,
                    bitLength = bitLength,
                    onHexChanged = onHexChanged,
                    onDecChanged = onDecChanged,
                    onBinChanged = onBinChanged
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                DecodedFieldsList(fields = decodedResults)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainCalculationScreenPreview() {
    val mockStructure = StructureWithFields(
        structure = StructureEntity(id = 1, name = "Sample Structure"),
        fields = listOf(
            FieldEntity(fieldName = "Field 1", msb = 7, lsb = 0, structureId = 1),
            FieldEntity(fieldName = "Field 2", msb = 15, lsb = 8, structureId = 1)
        )
    )

    MaterialTheme {
        MainCalculationContent(
            hex = "0x1234",
            dec = "4660",
            bin = "0b1001000110100",
            rawValue = BigInteger.valueOf(4660),
            pinnedStructures = listOf(mockStructure),
            selectedStructure = mockStructure,
            decodedResults = listOf(
                FieldResult("Field 1", "0x34", "52", 7, 0),
                FieldResult("Field 2", "0x12", "18", 15, 8)
            ),
            isMsbFirst = true,
            bitLength = 32,
            onNavigateToSettings = {},
            onNavigateToManager = {},
            onSelectStructure = {},
            onToggleBit = {},
            onHexChanged = {},
            onDecChanged = {},
            onBinChanged = {},
            onShowSelector = {}
        )
    }
}

