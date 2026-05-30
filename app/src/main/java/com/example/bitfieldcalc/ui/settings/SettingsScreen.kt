package com.example.bitfieldcalc.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    val isBigEndian = remember { mutableStateOf(false) }
    val isMsbFirst = remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(8.dp)) {
        Text("Global Settings")
        Text("Big Endian")
        Switch(checked = isBigEndian.value, onCheckedChange = { isBigEndian.value = it })

        Text("MSB First")
        Switch(checked = isMsbFirst.value, onCheckedChange = { isMsbFirst.value = it })

        Button(onClick = { /* TODO: export */ }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Text("Export JSON")
        }

        Button(onClick = { /* TODO: import */ }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Import JSON")
        }
    }
}

