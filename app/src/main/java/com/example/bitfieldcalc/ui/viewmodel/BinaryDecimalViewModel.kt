package com.example.bitfieldcalc.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.AndroidViewModel
import com.example.bitfieldcalc.data.PreferenceRepository
import net.objecthunter.exp4j.ExpressionBuilder

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

    // 計算処理（加減乗除を行う）
    fun calculate(input: String, isBinaryMode: Boolean) {
        // 2進数の計算を行う場合
        if (isBinaryMode) {
            try {
                val equation = input.replace("=", "") // "=" を除去
                val binaryRegex = Regex("[01]+") // 2進数のパターン

                // 2進数を10進数に変換する処理
                val inputDecimal = binaryRegex.replace(equation) { matchResult ->
                    val binaryValue = matchResult.value
                    val decimalValue = Integer.parseInt(binaryValue, 2)  // 2進数を10進数に変換
                    decimalValue.toString()  // 変換した10進数を文字列として返す
                }
                _result.value = eval(inputDecimal).toString()
            } catch (e: Exception) {
                _result.value = "Error"
            }
        } else {
            // 10進数の計算を行う場合
            try {
                _result.value = eval(input).toString()
            } catch (e: Exception) {
                _result.value = "Error"
            }
        }
    }

    // 10進数の計算式を実行する関数（例: 1+2*3）
    private fun eval(expressionInput: String): Double {
        val expression = ExpressionBuilder(expressionInput).build()
        return expression.evaluate()
    }
}