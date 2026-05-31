package com.example.bitfieldcalc.ui.calculation.components

import java.math.BigInteger

/**
 * Utility functions for validating/parsing input strings for hex/dec/bin fields.
 */
object NumberInputUtil {
    fun maxDecDigits(bitLength: Int = 64): Int =
        BigInteger.ONE.shiftLeft(bitLength).subtract(BigInteger.ONE).toString(10).length

    fun isValidHex(s: String, bitLength: Int = 64): Boolean {
        val ss = s.trim().removePrefix("0x").removePrefix("0X")
        val maxHexChars = bitLength / 4
        return ss.length <= maxHexChars && ss.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
    }

    fun isValidBin(s: String, bitLength: Int = 64): Boolean {
        val ss = s.trim().removePrefix("0b").removePrefix("0B")
        return ss.length <= bitLength && ss.all { it == '0' || it == '1' }
    }

    fun isValidDec(s: String, bitLength: Int = 64): Boolean {
        val ss = s.trim()
        val maxDigits = maxDecDigits(bitLength)
        return ss.length <= maxDigits && ss.all { it in '0'..'9' }
    }
}

