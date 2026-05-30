package com.example.bitfieldcalc.ui.calculation.components

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.math.BigInteger

@Composable
fun BitGrid(
    value: BigInteger,
    onToggle: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Bits (63..0)", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(8),
            modifier = Modifier.height(300.dp) // Adjust height as needed
        ) {
            items(64) { index ->
                val bitIndex = 63 - index
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
