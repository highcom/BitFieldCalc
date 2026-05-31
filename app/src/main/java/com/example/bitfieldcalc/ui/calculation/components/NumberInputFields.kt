package com.example.bitfieldcalc.ui.calculation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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

    val hexDigits = FixedWidthInputLogic.stripPrefix(hex, "0x")
    val decDigits = FixedWidthInputLogic.normalizeDigits(dec, NumberInputUtil.maxDecDigits(bitLength))
    val binDigits = FixedWidthInputLogic.stripPrefix(bin, "0b")

    Column(modifier = Modifier.padding(16.dp)) {
        BlockCursorDigitField(
            label = "HEX",
            prefix = "0x",
            digits = hexDigits,
            onDigitsChange = { onHexChanged(FixedWidthInputLogic.withPrefix(it, "0x")) },
            isCharValid = { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' },
            modifier = Modifier.fillMaxWidth()
        )
        BlockCursorDigitField(
            label = "DEC",
            prefix = "",
            digits = decDigits,
            onDigitsChange = onDecChanged,
            isCharValid = { it in '0'..'9' },
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
        BlockCursorDigitField(
            label = "BIN",
            prefix = "0b",
            digits = binDigits,
            onDigitsChange = { onBinChanged(FixedWidthInputLogic.withPrefix(it, "0b")) },
            isCharValid = { it == '0' || it == '1' },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
    }
}
