package com.example.bitfieldcalc.ui.calculation.components

/**
 * Utility functions for validating/parsing input strings for hex/dec/bin fields.
 */
object NumberInputUtil {
    private val HEX_REGEX = Regex("^[0-9A-Fa-f]{0,16}$")
    private val BIN_REGEX = Regex("^[01]{0,64}$")
    private val DEC_REGEX = Regex("^[0-9]{0,20}$")

    fun isValidHex(s: String): Boolean {
        val ss = s.trim().removePrefix("0x").removePrefix("0X")
        return HEX_REGEX.matches(ss)
    }

    fun isValidBin(s: String): Boolean {
        val ss = s.trim().removePrefix("0b").removePrefix("0B")
        return BIN_REGEX.matches(ss)
    }

    fun isValidDec(s: String): Boolean {
        val ss = s.trim()
        if (!DEC_REGEX.matches(ss)) return false
        // further numeric range checks could be applied by caller
        return true
    }
}

