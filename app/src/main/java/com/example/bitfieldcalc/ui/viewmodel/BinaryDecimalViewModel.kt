package com.example.bitfieldcalc.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.AndroidViewModel
import com.example.bitfieldcalc.data.PreferenceRepository

class BinaryDecimalViewModel(application: Application) : AndroidViewModel(application) {

    private val preferenceRepository = PreferenceRepository(application)

    // 初期状態をRepositoryから取得
    // 2進数 -> 10進数モードかどうか
    private val _isBinaryMode = mutableStateOf(preferenceRepository.getCurrentMode())
    val isBinaryMode: State<Boolean> = _isBinaryMode

    // 入力された値と変換結果を保持
    private val _input = mutableStateOf("")
    val input: State<String> = _input

    private val _result = mutableStateOf("")
    val result: State<String> = _result

    // 入力を更新する関数
    fun updateInput(value: String) {
        _input.value += value
    }

    // 入力をクリアする関数
    fun clearInput() {
        _input.value = ""
        _result.value = ""
    }

    // 変換処理
    fun convert() {
        try {
            _result.value = if (_isBinaryMode.value) {
                // 2進数 -> 10進数
                val decimal = Integer.parseInt(_input.value, 2)
                decimal.toString()
            } else {
                // 10進数 -> 2進数
                val binary = Integer.toBinaryString(_input.value.toInt())
                binary
            }
        } catch (e: NumberFormatException) {
            _result.value = "Invalid Input"
        }
    }

    // モードを切り替える関数
    fun toggleMode() {
        val newMode = !_isBinaryMode.value
        _isBinaryMode.value = newMode
        preferenceRepository.setCurrentMode(newMode)
        _input.value = ""
        _result.value = ""
    }
}