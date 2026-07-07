package com.highcom.bitfieldcalc.ui.calculation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.highcom.bitfieldcalc.R
import java.math.BigInteger

@Composable
fun CalculatorSection(
    valueA: BigInteger,
    valueB: BigInteger,
    onValueAChanged: (String) -> Unit,
    onValueBChanged: (String) -> Unit,
    onSetA: () -> Unit,
    onSetB: () -> Unit,
    onAnd: () -> Unit,
    onOr: () -> Unit,
    onXor: () -> Unit,
    onShiftLeft: () -> Unit,
    onShiftRight: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Value A row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BlockCursorDigitField(
                    label = stringResource(R.string.calc_value_a),
                    prefix = "0x",
                    digits = valueA.toString(16).uppercase(),
                    onDigitsChange = onValueAChanged,
                    isCharValid = { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onSetA) {
                    Text(stringResource(R.string.set_a))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Value B row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BlockCursorDigitField(
                    label = stringResource(R.string.calc_value_b),
                    prefix = "0x",
                    digits = valueB.toString(16).uppercase(),
                    onDigitsChange = onValueBChanged,
                    isCharValid = { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onSetB) {
                    Text(stringResource(R.string.set_b))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Operations Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OperationButton(text = stringResource(R.string.and), onClick = onAnd)
                OperationButton(text = stringResource(R.string.or), onClick = onOr)
                OperationButton(text = stringResource(R.string.xor), onClick = onXor)
                OperationButton(text = stringResource(R.string.shift_left), onClick = onShiftLeft)
                OperationButton(text = stringResource(R.string.shift_right), onClick = onShiftRight)
            }
        }
    }
}

@Composable
fun OperationButton(
    text: String,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.widthIn(min = 60.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}
