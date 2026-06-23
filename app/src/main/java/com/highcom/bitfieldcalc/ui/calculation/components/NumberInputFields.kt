package com.highcom.bitfieldcalc.ui.calculation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.highcom.bitfieldcalc.R
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
                        text = stringResource(R.string.bit_limit_exceeded, bitLength),
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

@Preview(showBackground = true)
@Composable
fun NumberInputFieldsPreview() {
    MaterialTheme {
        NumberInputFields(
            hex = "0x1234",
            dec = "4660",
            bin = "0b1001000110100",
            bitLength = 32,
            onHexChanged = {},
            onDecChanged = {},
            onBinChanged = {}
        )
    }
}

