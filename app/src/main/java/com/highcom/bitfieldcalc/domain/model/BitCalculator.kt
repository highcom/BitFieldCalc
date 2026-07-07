package com.highcom.bitfieldcalc.domain.model

import java.math.BigInteger

object BitCalculator {
    private val ZERO = BigInteger.ZERO
    private val ONE = BigInteger.ONE

    /**
     * Parse a string in given radix into a non-negative BigInteger.
     * Accepts optional prefixes: 0x for hex, 0b for binary.
     */
    fun parseStringToBigInteger(value: String, radix: Int): BigInteger {
        val s = value.trim()
        if (s.isEmpty()) return ZERO
        val normalized = when {
            s.startsWith("0x", ignoreCase = true) -> s.substring(2)
            s.startsWith("0b", ignoreCase = true) -> s.substring(2)
            else -> s
        }
        return try {
            BigInteger(normalized, radix)
        } catch (e: Exception) {
            ZERO
        }
    }

    /**
     * Format a BigInteger into a string of given radix.
     * If padTo is non-zero and radix is 2/16, it will left-pad to specified bits / corresponding hex chars.
     */
    fun toRadixString(rawValue: BigInteger, radix: Int, padTo: Int = 0): String {
        val unsigned = if (rawValue.signum() < 0) {
            // For negative BigInteger, convert to two's complement representation
            val bitLen = if (padTo > 0) padTo else 64
            rawValue.and(ONE.shiftLeft(bitLen).subtract(ONE))
        } else rawValue

        val str = when (radix) {
            16 -> unsigned.toString(16).uppercase()
            10 -> unsigned.toString(10)
            2 -> unsigned.toString(2)
            else -> unsigned.toString(radix)
        }

        return if (padTo > 0) {
            when (radix) {
                16 -> str.padStart(padTo / 4, '0')
                2 -> str.padStart(padTo, '0')
                else -> str
            }
        } else str
    }

    fun and(a: BigInteger, b: BigInteger, bitLength: Int): BigInteger = a.and(b).and(mask(bitLength))
    fun or(a: BigInteger, b: BigInteger, bitLength: Int): BigInteger = a.or(b).and(mask(bitLength))
    fun xor(a: BigInteger, b: BigInteger, bitLength: Int): BigInteger = a.xor(b).and(mask(bitLength))

    fun shiftLeft(v: BigInteger, bitLength: Int): BigInteger = v.shiftLeft(1).and(mask(bitLength))
    fun shiftRight(v: BigInteger, bitLength: Int): BigInteger = v.shiftRight(1)

    private fun mask(bitLength: Int) = ONE.shiftLeft(bitLength).subtract(ONE)

    fun generateCCode(oldValue: BigInteger, newValue: BigInteger, bitIndex: Int? = null): String {
        if (bitIndex != null) {
            val isSet = newValue.testBit(bitIndex)
            return if (isSet) "REG |= (1 << $bitIndex);" else "REG &= ~(1 << $bitIndex);"
        }
        return "REG = 0x${newValue.toString(16).uppercase()};"
    }

    /**
     * Extract field value from rawValue in range [msb:lsb].
     * If isSigned is true, performs two's complement sign extension.
     */
    fun extractFieldValue(
        rawValue: BigInteger,
        msb: Int,
        lsb: Int,
        isSigned: Boolean,
        maxBitIndex: Int = 63
    ): BigInteger {
        require(msb in 0..maxBitIndex && lsb in 0..maxBitIndex && msb >= lsb) { "msb/lsb out of range" }
        val width = msb - lsb + 1
        val mask = ONE.shiftLeft(width).subtract(ONE)
        val shifted = rawValue.shiftRight(lsb).and(mask)

        return if (isSigned && width > 0) {
            val signBit = ONE.shiftLeft(width - 1)
            if (shifted.and(signBit) != ZERO) {
                // negative: value - 2^width
                shifted.subtract(ONE.shiftLeft(width))
            } else shifted
        } else shifted
    }
}

