package com.example.bitfieldcalc.ui.calculation.components

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

object FixedWidthInputLogic {
    fun applyChange(
        current: TextFieldValue,
        proposed: TextFieldValue,
        length: Int,
        isCharValid: (Char) -> Boolean
    ): TextFieldValue {
        val text = normalizeDigits(current.text, length)
        val sel = current.selection.start.coerceIn(0, length)

        if (proposed.text == text) {
            val newSel = proposed.selection.start.coerceIn(0, length)
            return proposed.copy(selection = TextRange(newSel))
        }

        val proposedText = proposed.text

        when {
            proposedText.length == text.length -> {
                val diffIndices = text.indices.filter { text[it] != proposedText[it] }
                if (diffIndices.size == 1) {
                    val idx = diffIndices.first()
                    if (isCharValid(proposedText[idx])) {
                        val newSel = (idx + 1).coerceAtMost(length)
                        return TextFieldValue(proposedText, TextRange(newSel))
                    }
                }
            }

            proposedText.length == text.length + 1 -> {
                val insertPos = (proposed.selection.start - 1).coerceIn(0, length - 1)
                val newChar = proposedText.getOrNull(insertPos) ?: return current.copy(text = text)
                if (isCharValid(newChar)) {
                    val updated = text.toCharArray().also { it[insertPos] = newChar }
                    val newSel = (insertPos + 1).coerceAtMost(length)
                    return TextFieldValue(String(updated), TextRange(newSel))
                }
            }

            proposedText.length < text.length -> {
                if (proposedText.length == text.length - 1) {
                    val clearIndex = (sel - 1).coerceAtLeast(0)
                    val updated = text.toCharArray().also { it[clearIndex] = '0' }
                    return TextFieldValue(String(updated), TextRange(clearIndex))
                }
            }
        }

        val cleaned = proposedText.filter(isCharValid)
        if (cleaned.isNotEmpty()) {
            val padded = cleaned.takeLast(length).padStart(length, '0')
            val newSel = proposed.selection.start.coerceIn(0, length)
            return TextFieldValue(padded, TextRange(newSel))
        }

        return current.copy(text = text, selection = TextRange(sel))
    }

    fun normalizeDigits(text: String, length: Int): String {
        val trimmed = text.take(length)
        return trimmed.padStart(length, '0')
    }

    fun stripPrefix(value: String, prefix: String): String =
        value.removePrefix(prefix).removePrefix(prefix.uppercase())

    fun withPrefix(digits: String, prefix: String): String = prefix + digits
}
