package com.highcom.bitfieldcalc.ui.calculation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.math.BigInteger

@Composable
fun BitGrid(
    value: BigInteger,
    isMsbFirst: Boolean,
    bitLength: Int,
    onToggle: (Int) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val gridPadding = 16.dp * 2
    val cellWidth = (screenWidth - gridPadding) / 8
    val rowCount = (bitLength + 7) / 8
    val gridHeight = cellWidth * rowCount

    Column(modifier = Modifier.padding(16.dp)) {
        val maxBit = bitLength - 1
        Text(
            text = if (isMsbFirst) "Bits ($maxBit..0)" else "Bits (0..$maxBit)",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(8),
            modifier = Modifier.height(gridHeight),
            userScrollEnabled = false
        ) {
            items(bitLength) { index ->
                val bitIndex = if (isMsbFirst) (bitLength - 1) - index else index
                val isSet = value.testBit(bitIndex)
                
                BitCell(
                    index = bitIndex,
                    isSet = isSet,
                    onClick = { onToggle(bitIndex) }
                )
            }
        }
    }
}

@Composable
fun BitCell(
    index: Int,
    isSet: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(2.dp)
            .aspectRatio(1f)
            .background(if (isSet) MaterialTheme.colorScheme.primary else Color.LightGray)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = index.toString(),
                fontSize = 10.sp,
                color = if (isSet) MaterialTheme.colorScheme.onPrimary else Color.Black
            )
            Text(
                text = if (isSet) "1" else "0",
                fontSize = 16.sp,
                color = if (isSet) MaterialTheme.colorScheme.onPrimary else Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BitGridPreview() {
    MaterialTheme {
        BitGrid(
            value = BigInteger.valueOf(0xAA),
            isMsbFirst = true,
            bitLength = 32,
            onToggle = {}
        )
    }
}

