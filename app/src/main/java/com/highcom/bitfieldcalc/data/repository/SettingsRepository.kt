package com.highcom.bitfieldcalc.data.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _isBigEndian = MutableStateFlow(prefs.getBoolean("is_big_endian", false))
    val isBigEndian: StateFlow<Boolean> = _isBigEndian

    private val _isMsbFirst = MutableStateFlow(prefs.getBoolean("is_msb_first", true))
    val isMsbFirst: StateFlow<Boolean> = _isMsbFirst

    private val _bitLength = MutableStateFlow(prefs.getInt("bit_length", 64))
    val bitLength: StateFlow<Int> = _bitLength

    fun setBigEndian(value: Boolean) {
        prefs.edit().putBoolean("is_big_endian", value).apply()
        _isBigEndian.value = value
    }

    fun setMsbFirst(value: Boolean) {
        prefs.edit().putBoolean("is_msb_first", value).apply()
        _isMsbFirst.value = value
    }

    fun setBitLength(value: Int) {
        require(value == 8 || value == 16 || value == 32 || value == 64) { "bit length must be 8, 16, 32 or 64" }
        prefs.edit().putInt("bit_length", value).apply()
        _bitLength.value = value
    }
}
