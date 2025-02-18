package com.example.bitfieldcalc.data

import android.content.Context
import android.content.SharedPreferences

class PreferenceRepository(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("binary_decimal_preferences", Context.MODE_PRIVATE)

    private val MODE_KEY = "mode_key"

    // 現在のモード（true: 2進数 -> 10進数、false: 10進数 -> 2進数）
    fun getCurrentMode(): Boolean {
        return sharedPreferences.getBoolean(MODE_KEY, true) // デフォルトは2進数 -> 10進数
    }

    fun setCurrentMode(isBinaryMode: Boolean) {
        sharedPreferences.edit().putBoolean(MODE_KEY, isBinaryMode).apply()
    }
}