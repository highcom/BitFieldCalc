package com.example.bitfieldcalc.ui.calculation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bitfieldcalc.data.db.entity.StructureWithFields
import com.example.bitfieldcalc.data.repository.StructureRepository
import com.example.bitfieldcalc.data.repository.SettingsRepository
import com.example.bitfieldcalc.domain.model.BitCalculator
import com.example.bitfieldcalc.ui.calculation.components.NumberInputUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class BitFieldCalcViewModel @Inject constructor(
    private val repository: StructureRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {
    private val _rawValue = MutableStateFlow(BigInteger.ZERO)
    val rawValue: StateFlow<BigInteger> = _rawValue.asStateFlow()

    val isMsbFirst: StateFlow<Boolean> = settingsRepository.isMsbFirst
    val bitLength: StateFlow<Int> = settingsRepository.bitLength

    private val _hex = MutableStateFlow("0x" + "0".repeat(bitLength.value / 4))
    val hex: StateFlow<String> = _hex.asStateFlow()

    private val _dec = MutableStateFlow("0".padStart(NumberInputUtil.maxDecDigits(bitLength.value), '0'))
    val dec: StateFlow<String> = _dec.asStateFlow()

    private val _bin = MutableStateFlow("0b" + "0".repeat(bitLength.value))
    val bin: StateFlow<String> = _bin.asStateFlow()

    private val _selectedStructure = MutableStateFlow<StructureWithFields?>(null)
    val selectedStructure: StateFlow<StructureWithFields?> = _selectedStructure.asStateFlow()

    val pinnedStructures: StateFlow<List<StructureWithFields>> = repository.getPinnedStructuresWithFields()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            bitLength.collect {
                setRawValue(_rawValue.value)
            }
        }
    }

    val decodedResults = combine(_rawValue, _selectedStructure, bitLength) { value, structure, currentBitLength ->
        val effectiveBitWidth = currentBitLength
        val maxBitIndex = effectiveBitWidth - 1
        structure?.fields?.map { field ->
            try {
                val fieldVal = BitCalculator.extractFieldValue(
                    value,
                    field.msb,
                    field.lsb,
                    field.isSigned,
                    maxBitIndex
                )
                val hexVal = "0x" + fieldVal.toString(16).uppercase()
                val decVal = fieldVal.toString(10)
                FieldResult(field.fieldName, hexVal, decVal, field.msb, field.lsb)
            } catch (e: IllegalArgumentException) {
                FieldResult(field.fieldName, "Error", "Invalid range", field.msb, field.lsb)
            }
        } ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
        val currentBitLength = bitLength.value
        val mask = BigInteger.ONE.shiftLeft(currentBitLength).subtract(BigInteger.ONE)
        val maskedValue = v.and(mask)

        viewModelScope.launch {
            _rawValue.emit(maskedValue)
            _hex.emit("0x" + BitCalculator.toRadixString(maskedValue, 16, padTo = currentBitLength))
            _dec.emit(
                BitCalculator.toRadixString(maskedValue, 10).padStart(
                    NumberInputUtil.maxDecDigits(currentBitLength),
                    '0'
                )
            )
            _bin.emit("0b" + BitCalculator.toRadixString(maskedValue, 2, padTo = currentBitLength))
        }
    }

    fun toggleBit(bitIndex: Int) {
        val currentBitLength = bitLength.value
        require(bitIndex in 0 until currentBitLength)
        val mask = BigInteger.ONE.shiftLeft(bitIndex)
        val newVal = _rawValue.value.xor(mask)
        setRawValue(newVal)
    }

    fun loadSelectedStructure(structureId: Long) {
        viewModelScope.launch {
            val structure = repository.getStructureWithFieldsById(structureId)
            structure?.let {
                _selectedStructure.emit(it)
            }
        }
    }

    fun selectStructure(structure: StructureWithFields) {
        viewModelScope.launch {
            _selectedStructure.emit(structure)
        }
    }

    fun searchStructures(query: String) = repository.searchStructures(query)
}

data class FieldResult(
    val name: String,
    val hexValue: String,
    val decValue: String,
    val msb: Int,
    val lsb: Int
)
