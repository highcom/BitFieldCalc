package com.highcom.bitfieldcalc.ui.calculation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockCursorDigitField(
    label: String,
    prefix: String,
    digits: String,
    onDigitsChange: (String) -> Unit,
    isCharValid: (Char) -> Boolean,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
) {
    val digitCount = digits.length
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()

    var textFieldValue by remember(digitCount) {
        mutableStateOf(TextFieldValue(FixedWidthInputLogic.normalizeDigits(digits, digitCount), TextRange(0)))
    }

    LaunchedEffect(digits) {
        val normalized = FixedWidthInputLogic.normalizeDigits(digits, digitCount)
        if (normalized != textFieldValue.text) {
            val cursor = textFieldValue.selection.start.coerceIn(0, digitCount)
            textFieldValue = TextFieldValue(normalized, TextRange(cursor))
        }
    }

    val cursorIndex = textFieldValue.selection.start.coerceIn(0, (digitCount - 1).coerceAtLeast(0))

    LaunchedEffect(isFocused, cursorIndex) {
        if (isFocused && digitCount > 8) {
            val approxCharWidth = 10
            val prefixWidth = prefix.length * approxCharWidth
            scrollState.scrollTo((prefixWidth + cursorIndex * approxCharWidth).coerceAtLeast(0))
        }
    }

    val colors = OutlinedTextFieldDefaults.colors()

    BasicTextField(
        value = textFieldValue,
        onValueChange = { proposed ->
            val updated = FixedWidthInputLogic.applyChange(
                current = textFieldValue,
                proposed = proposed,
                length = digitCount,
                isCharValid = isCharValid
            )
            textFieldValue = updated
            if (updated.text != digits) {
                onDigitsChange(updated.text)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .focusRequester(focusRequester),
        textStyle = TextStyle(
            color = Color.Transparent,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace
        ),
        cursorBrush = SolidColor(Color.Transparent),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
        interactionSource = interactionSource,
        singleLine = true,
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = textFieldValue.text,
                innerTextField = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            innerTextField()
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(scrollState)
                                .padding(horizontal = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (prefix.isNotEmpty()) {
                                Text(
                                    text = prefix,
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                            textFieldValue.text.forEachIndexed { index, char ->
                                val isCursorCell = isFocused && index == cursorIndex
                                val background = if (isCursorCell) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    Color.Transparent
                                }
                                val foreground = when {
                                    isCursorCell -> MaterialTheme.colorScheme.onPrimary
                                    char == '0' -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                                Box(
                                    modifier = Modifier
                                        .clickable(indication = null, interactionSource = null) {
                                            focusRequester.requestFocus()
                                            textFieldValue = textFieldValue.copy(selection = TextRange(index))
                                        }
                                        .background(background, MaterialTheme.shapes.extraSmall)
                                        .padding(horizontal = 1.dp, vertical = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = char.uppercaseChar().toString(),
                                        style = TextStyle(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 16.sp,
                                            color = foreground
                                        )
                                    )
                                }
                            }
                        }
                    }
                },
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                isError = isError,
                label = { Text(label) },
                supportingText = supportingText,
                colors = colors,
                contentPadding = OutlinedTextFieldDefaults.contentPadding(),
                container = {
                    OutlinedTextFieldDefaults.Container(
                        enabled = true,
                        isError = isError,
                        interactionSource = interactionSource,
                        colors = colors,
                        shape = OutlinedTextFieldDefaults.shape,
                    )
                }
            )
        }
    )
}
