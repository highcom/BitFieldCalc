package com.example.bitfieldcalc.domain.model

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
     * If padTo64 is true and radix is 2/16, it will left-pad to 64 bits / 16 hex chars.
     */
    fun toRadixString(rawValue: BigInteger, radix: Int, padTo64: Boolean = false): String {
        val unsigned = if (rawValue.signum() < 0) {
            // For negative BigInteger, convert to two's complement 64-bit representation
            rawValue.and(ONE.shiftLeft(64).subtract(ONE))
        } else rawValue

        val str = when (radix) {
            16 -> unsigned.toString(16).uppercase()
            10 -> unsigned.toString(10)
            2 -> unsigned.toString(2)
            else -> unsigned.toString(radix)
        }

        return if (padTo64) {
            when (radix) {
                16 -> str.padStart(16, '0')
                2 -> str.padStart(64, '0')
                else -> str
            }
        } else str
    }

    /**
     * Extract field value from rawValue in range [msb:lsb].
     * If isSigned is true, performs two's complement sign extension.
     */
    fun extractFieldValue(rawValue: BigInteger, msb: Int, lsb: Int, isSigned: Boolean): BigInteger {
        require(msb in 0..63 && lsb in 0..63 && msb >= lsb) { "msb/lsb out of range" }
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

