package com.example.bitfieldcalc.ui.calculation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.math.BigInteger

@Composable
fun NumberInputFields(
    hex: String,
    dec: String,
    bin: String,
    bitLength: Int,
    onHexChanged: (String) -> Unit,
    onDecChanged: (String) -> Unit,
    onBinChanged: (String) -> Unit
) {
    val maxValue = BigInteger.ONE.shiftLeft(bitLength).subtract(BigInteger.ONE)
    val isDecOverflow = try { BigInteger(dec) > maxValue } catch (e: Exception) { false }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = hex,
            onValueChange = { if (NumberInputUtil.isValidHex(it, bitLength)) onHexChanged(it) },
            label = { Text("HEX") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = dec,
            onValueChange = { if (NumberInputUtil.isValidDec(it, bitLength)) onDecChanged(it) },
            label = { Text("DEC") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            isError = isDecOverflow,
            supportingText = {
                if (isDecOverflow) {
                    Text(
                        text = "${bitLength}bitの上限値を超えています",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
        OutlinedTextField(
            value = bin,
            onValueChange = { if (NumberInputUtil.isValidBin(it, bitLength)) onBinChanged(it) },
            label = { Text("BIN") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
    }
}
