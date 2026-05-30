package com.example.bitfieldcalc.ui.calculation.components

import com.example.bitfieldcalc.domain.model.BitCalculator
import java.math.BigInteger

/**
 * Pure model of a 64-bit grid. No Android dependencies so it can be unit tested easily.
 */
class BitGridModel(initial: BigInteger = BigInteger.ZERO) {
    private var _value: BigInteger = initial.and(BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE))

    val value: BigInteger get() = _value

    fun setValue(v: BigInteger) {
        _value = v.and(BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE))
    }

    fun toggleBit(index: Int) {
        require(index in 0..63)
        val mask = BigInteger.ONE.shiftLeft(index)
        _value = _value.xor(mask)
    }

    fun isBitSet(index: Int): Boolean {
        require(index in 0..63)
        return _value.and(BigInteger.ONE.shiftLeft(index)) != BigInteger.ZERO
    }

    fun toHex(padTo64: Boolean = true): String = "0x" + BitCalculator.toRadixString(_value, 16, padTo64)
    fun toDec(): String = BitCalculator.toRadixString(_value, 10)
    fun toBin(padTo64: Boolean = true): String = "0b" + BitCalculator.toRadixString(_value, 2, padTo64)
}

