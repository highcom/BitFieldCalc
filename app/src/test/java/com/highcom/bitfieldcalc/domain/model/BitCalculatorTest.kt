package com.highcom.bitfieldcalc.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigInteger

class BitCalculatorTest {
    @Test
    fun parseHexAndFormat() {
        val v = BitCalculator.parseStringToBigInteger("0xFF", 16)
        assertEquals(BigInteger("255"), v)
        val hex = BitCalculator.toRadixString(v, 16, padTo = 0)
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

    @Test(expected = IllegalArgumentException::class)
    fun extractFieldOutOfRange() {
        val raw = BigInteger.ZERO
        BitCalculator.extractFieldValue(raw, 64, 0, isSigned = false, maxBitIndex = 63)
    }

    @Test
    fun extractFieldWithin32BitRange() {
        val raw = BitCalculator.parseStringToBigInteger("0xFFFFFFFF", 16)
        val extracted = BitCalculator.extractFieldValue(raw, 31, 0, isSigned = false, maxBitIndex = 31)
        assertEquals(BigInteger("4294967295"), extracted)
    }

    @Test(expected = IllegalArgumentException::class)
    fun extractFieldOutOf32BitRange() {
        val raw = BigInteger.ZERO
        BitCalculator.extractFieldValue(raw, 32, 0, isSigned = false, maxBitIndex = 31)
    }

    @Test(expected = IllegalArgumentException::class)
    fun extractFieldInvalidOrder() {
        val raw = BigInteger.ZERO
        BitCalculator.extractFieldValue(raw, 0, 1, isSigned = false)
    }

    @Test
    fun bitwiseOperations() {
        val a = BigInteger("10") // 1010
        val b = BigInteger("12") // 1100
        assertEquals(BigInteger("8"), BitCalculator.and(a, b, 4)) // 1000
        assertEquals(BigInteger("14"), BitCalculator.or(a, b, 4)) // 1110
        assertEquals(BigInteger("6"), BitCalculator.xor(a, b, 4)) // 0110
    }

    @Test
    fun shifts() {
        val v = BigInteger("1")
        assertEquals(BigInteger("2"), BitCalculator.shiftLeft(v, 4))
        assertEquals(BigInteger("4"), BitCalculator.shiftLeft(BigInteger("2"), 4))
        assertEquals(BigInteger("2"), BitCalculator.shiftRight(BigInteger("4"), 4))
        // test mask in shiftLeft
        assertEquals(BigInteger("0"), BitCalculator.shiftLeft(BigInteger("8"), 4))
    }

    @Test
    fun cCodeGeneration() {
        // Toggle bit 5 on
        assertEquals("REG |= (1 << 5);", BitCalculator.generateCCode(BigInteger.ZERO, BigInteger.valueOf(32), 5))
        // Toggle bit 3 off
        assertEquals("REG &= ~(1 << 3);", BitCalculator.generateCCode(BigInteger.valueOf(8), BigInteger.ZERO, 3))
        // Raw set
        assertEquals("REG = 0xAA;", BitCalculator.generateCCode(BigInteger.ZERO, BigInteger.valueOf(170)))
    }
}

