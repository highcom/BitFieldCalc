package com.highcom.bitfieldcalc.ui.structure

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun BitIndexInputField(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    maxBitIndex: Int,
    modifier: Modifier = Modifier,
) {
    var text by remember { mutableStateOf(value.toString()) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(value) {
        if (!isFocused) {
            text = value.toString()
        }
    }

    LaunchedEffect(isFocused) {
        if (isFocused) {
            if (value == 0) {
                text = ""
            }
        } else if (text.isBlank()) {
            text = "0"
            onValueChange(0)
        }
    }

    OutlinedTextField(
        value = text,
        onValueChange = { newText ->
            val filtered = newText.filter { it.isDigit() }.take(2)
            text = filtered
            filtered.toIntOrNull()?.let(onValueChange)
        },
        label = { Text(label) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        interactionSource = interactionSource,
        singleLine = true,
        isError = value > maxBitIndex,
        supportingText = if (value > maxBitIndex) {
            { Text("0〜$maxBitIndex") }
        } else {
            null
        }
    )
}
