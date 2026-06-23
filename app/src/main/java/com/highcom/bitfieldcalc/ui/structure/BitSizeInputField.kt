package com.highcom.bitfieldcalc.ui.structure

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BitSizeInputField(
    value: Int,
    onValueChange: (Int) -> Unit,
    maxSize: Int,
    modifier: Modifier = Modifier,
) {
    var text by remember { mutableStateOf(if (value > 0) value.toString() else "") }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(value) {
        if (!isFocused) {
            text = if (value > 0) value.toString() else ""
        }
    }

    LaunchedEffect(isFocused) {
        if (!isFocused && text.isBlank()) {
            text = if (value > 0) value.toString() else ""
        }
    }

    OutlinedTextField(
        value = text,
        onValueChange = { newText ->
            val filtered = newText.filter { it.isDigit() }.take(2)
            text = filtered
            filtered.toIntOrNull()?.let {
                if (it in 1..maxSize) {
                    onUpdateWithDebounce(it, onValueChange)
                }
            }
        },
        label = { Text("Size") },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        interactionSource = interactionSource,
        singleLine = true,
        isError = value > maxSize || (value == 0 && text.isNotEmpty()),
        supportingText = if (value > maxSize) {
            { Text("1〜$maxSize") }
        } else {
            null
        }
    )
}

// Simple debounce-like behavior if needed, or just call onValueChange
private fun onUpdateWithDebounce(value: Int, onValueChange: (Int) -> Unit) {
    onValueChange(value)
}

@Preview(showBackground = true)
@Composable
fun BitSizeInputFieldPreview() {
    BitSizeInputField(
        value = 8,
        onValueChange = {},
        maxSize = 32
    )
}

