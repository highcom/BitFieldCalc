package com.example.bitfieldcalc.ui.calculation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bitfieldcalc.ui.calculation.components.BitGrid
import com.example.bitfieldcalc.ui.calculation.components.NumberInputFields

@Composable
fun MainCalculationScreen(viewModel: BitFieldCalcViewModel) {
    val hex = viewModel.hex.collectAsState()
    val dec = viewModel.dec.collectAsState()
    val bin = viewModel.bin.collectAsState()
    val rawValue = viewModel.rawValue.collectAsState()

    Surface(color = MaterialTheme.colorScheme.background) {
        Column {
            NumberInputFields(
                hex = hex.value,
                dec = dec.value,
                bin = bin.value,
                onHexChanged = { viewModel.updateRawValueFromHex(it) },
                onDecChanged = { viewModel.updateRawValueFromDec(it) },
                onBinChanged = { viewModel.updateRawValueFromBin(it) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            BitGrid(value = rawValue.value, onToggle = { idx -> viewModel.toggleBit(idx) })
        }
    }
}

