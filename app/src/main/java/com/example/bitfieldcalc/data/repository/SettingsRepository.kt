package com.example.bitfieldcalc.data.repository

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

    fun setBigEndian(value: Boolean) {
        prefs.edit().putBoolean("is_big_endian", value).apply()
        _isBigEndian.value = value
    }

    fun setMsbFirst(value: Boolean) {
        prefs.edit().putBoolean("is_msb_first", value).apply()
        _isMsbFirst.value = value
    }
}
