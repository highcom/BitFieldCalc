package com.example.bitfieldcalc.ui.calculation.components

import com.example.bitfieldcalc.domain.model.BitCalculator
import java.math.BigInteger

/**
 * Pure model of a bit grid. No Android dependencies so it can be unit tested easily.
 */
class BitGridModel(initial: BigInteger = BigInteger.ZERO, private val bitLength: Int = 64) {
    private val mask = BigInteger.ONE.shiftLeft(bitLength).subtract(BigInteger.ONE)
    private var _value: BigInteger = initial.and(mask)

    val value: BigInteger get() = _value

    fun setValue(v: BigInteger) {
        _value = v.and(mask)
    }

    fun toggleBit(index: Int) {
        require(index in 0 until bitLength)
        val bitMask = BigInteger.ONE.shiftLeft(index)
        _value = _value.xor(bitMask)
    }

    fun isBitSet(index: Int): Boolean {
        require(index in 0 until bitLength)
        return _value.and(BigInteger.ONE.shiftLeft(index)) != BigInteger.ZERO
    }

    fun toHex(pad: Boolean = true): String = "0x" + BitCalculator.toRadixString(_value, 16, if (pad) bitLength else 0)
    fun toDec(): String = BitCalculator.toRadixString(_value, 10)
    fun toBin(pad: Boolean = true): String = "0b" + BitCalculator.toRadixString(_value, 2, if (pad) bitLength else 0)
}

