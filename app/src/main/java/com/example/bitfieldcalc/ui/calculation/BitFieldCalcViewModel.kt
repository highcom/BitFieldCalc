package com.example.bitfieldcalc.ui.calculation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigInteger
import com.example.bitfieldcalc.data.repository.StructureRepository
import com.example.bitfieldcalc.domain.model.BitCalculator

@HiltViewModel
class BitFieldCalcViewModel @Inject constructor(
    private val repository: StructureRepository
) : ViewModel() {
    private val _rawValue = MutableStateFlow(BigInteger.ZERO)
    val rawValue: StateFlow<BigInteger> = _rawValue.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _hex = MutableStateFlow("0x0")
    val hex: StateFlow<String> = _hex.asStateFlow()

    private val _dec = MutableStateFlow("0")
    val dec: StateFlow<String> = _dec.asStateFlow()

    private val _bin = MutableStateFlow("0b0")
    val bin: StateFlow<String> = _bin.asStateFlow()

    fun updateRawValueFromHex(hexStr: String) {
        val v = BitCalculator.parseStringToBigInteger(hexStr, 16)
        setRawValue(v)
    }

    fun updateRawValueFromDec(decStr: String) {
        val v = BitCalculator.parseStringToBigInteger(decStr, 10)
        setRawValue(v)
    }

    fun updateRawValueFromBin(binStr: String) {
        val v = BitCalculator.parseStringToBigInteger(binStr, 2)
        setRawValue(v)
    }

    private fun setRawValue(v: BigInteger) {
        scope.launch {
            _rawValue.emit(v)
            _hex.emit("0x" + BitCalculator.toRadixString(v, 16, padTo64 = true))
            _dec.emit(BitCalculator.toRadixString(v, 10))
            _bin.emit("0b" + BitCalculator.toRadixString(v, 2, padTo64 = true))
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }

    fun toggleBit(bitIndex: Int) {
        require(bitIndex in 0..63)
        val mask = BigInteger.ONE.shiftLeft(bitIndex)
        val newVal = _rawValue.value.xor(mask)
        setRawValue(newVal)
    }

    fun loadSelectedStructure(structureId: Long) {
        // TODO: implement applying a structure
    }

    fun searchStructures(query: String) = repository.searchStructures(query)
}



