package com.example.bitfieldcalc.ui.calculation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NumberInputFields(
    hex: String,
    dec: String,
    bin: String,
    onHexChanged: (String) -> Unit,
    onDecChanged: (String) -> Unit,
    onBinChanged: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = hex,
            onValueChange = { if (NumberInputUtil.isValidHex(it)) onHexChanged(it) },
            label = { Text("Hex") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = dec,
            onValueChange = { if (NumberInputUtil.isValidDec(it)) onDecChanged(it) },
            label = { Text("Decimal") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = bin,
            onValueChange = { if (NumberInputUtil.isValidBin(it)) onBinChanged(it) },
            label = { Text("Binary") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
