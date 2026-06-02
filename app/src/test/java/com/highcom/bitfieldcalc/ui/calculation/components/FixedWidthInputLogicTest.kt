package com.highcom.bitfieldcalc.ui.calculation.components

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import org.junit.Assert.assertEquals
import org.junit.Test

class FixedWidthInputLogicTest {
    private val isHex: (Char) -> Boolean = { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }

    @Test
    fun normalizeDigits_padsToLength() {
        assertEquals("00FF", FixedWidthInputLogic.normalizeDigits("FF", 4))
        assertEquals("0000", FixedWidthInputLogic.normalizeDigits("", 4))
    }

    @Test
    fun applyChange_replacesSingleDigitAndAdvancesCursor() {
        val current = TextFieldValue("0000", TextRange(1))
        val proposed = TextFieldValue("0A00", TextRange(2))

        val result = FixedWidthInputLogic.applyChange(current, proposed, 4, isHex)

        assertEquals("0A00", result.text)
        assertEquals(2, result.selection.start)
    }

    @Test
    fun applyChange_backspaceClearsPreviousDigit() {
        val current = TextFieldValue("00A0", TextRange(3))
        val proposed = TextFieldValue("00A", TextRange(2))

        val result = FixedWidthInputLogic.applyChange(current, proposed, 4, isHex)

        assertEquals("0000", result.text)
        assertEquals(2, result.selection.start)
    }

    @Test
    fun applyChange_pastePadsToFullWidth() {
        val current = TextFieldValue("0000", TextRange(0))
        val proposed = TextFieldValue("FF", TextRange(2))

        val result = FixedWidthInputLogic.applyChange(current, proposed, 4, isHex)

        assertEquals("00FF", result.text)
    }

    @Test
    fun stripPrefix_andWithPrefix() {
        assertEquals("00FF", FixedWidthInputLogic.stripPrefix("0x00FF", "0x"))
        assertEquals("0x00FF", FixedWidthInputLogic.withPrefix("00FF", "0x"))
    }
}
