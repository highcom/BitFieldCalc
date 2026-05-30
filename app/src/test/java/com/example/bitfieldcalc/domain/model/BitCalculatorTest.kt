package com.example.bitfieldcalc.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigInteger

class BitCalculatorTest {
    @Test
    fun parseHexAndFormat() {
        val v = BitCalculator.parseStringToBigInteger("0xFF", 16)
        assertEquals(BigInteger("255"), v)
        val hex = BitCalculator.toRadixString(v, 16, padTo64 = false)
        assertEquals("FF", hex)
    }

    @Test
    fun parseBinAndToggle() {
        val v = BitCalculator.parseStringToBigInteger("0b1010", 2)
        assertEquals(BigInteger("10"), v)
    }

    @Test
    fun extractUnsignedField() {
        // raw 0xAA00 -> bits [15:8] = 0xAA = 170
        val raw = BitCalculator.parseStringToBigInteger("0xAA00", 16)
        val extracted = BitCalculator.extractFieldValue(raw, 15, 8, isSigned = false)
        assertEquals(BigInteger("170"), extracted)
    }

    @Test
    fun extractSignedField() {
        // field 8-bit 0xFF interpreted signed -> -1
        val raw = BitCalculator.parseStringToBigInteger("0x00FF", 16)
        val extracted = BitCalculator.extractFieldValue(raw, 7, 0, isSigned = true)
        assertEquals(BigInteger("-1"), extracted)
    }
}

