package com.highcom.bitfieldcalc.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.highcom.bitfieldcalc.R

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val isBigEndian by viewModel.isBigEndian.collectAsState()
    val isMsbFirst by viewModel.isMsbFirst.collectAsState()
    val bitLength by viewModel.bitLength.collectAsState()

    SettingsContent(
        isBigEndian = isBigEndian,
        isMsbFirst = isMsbFirst,
        bitLength = bitLength,
        onBack = onBack,
        onSaveSettings = { be, msb, len -> viewModel.saveEnvironmentSettings(be, msb, len) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    isBigEndian: Boolean,
    isMsbFirst: Boolean,
    bitLength: Int,
    onBack: () -> Unit,
    onSaveSettings: (Boolean, Boolean, Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.global_settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.endian_big), modifier = Modifier.weight(1f))
                Switch(
                    checked = isBigEndian,
                    onCheckedChange = { onSaveSettings(it, isMsbFirst, bitLength) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.bit_order_msb), modifier = Modifier.weight(1f))
                Switch(
                    checked = isMsbFirst,
                    onCheckedChange = { onSaveSettings(isBigEndian, it, bitLength) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.bit_width), modifier = Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.Start) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = bitLength == 8,
                            onClick = { onSaveSettings(isBigEndian, isMsbFirst, 8) }
                        )
                        Text("8 bit")
                        Spacer(modifier = Modifier.width(8.dp))
                        RadioButton(
                            selected = bitLength == 16,
                            onClick = { onSaveSettings(isBigEndian, isMsbFirst, 16) }
                        )
                        Text("16 bit")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = bitLength == 32,
                            onClick = { onSaveSettings(isBigEndian, isMsbFirst, 32) }
                        )
                        Text("32 bit")
                        Spacer(modifier = Modifier.width(8.dp))
                        RadioButton(
                            selected = bitLength == 64,
                            onClick = { onSaveSettings(isBigEndian, isMsbFirst, 64) }
                        )
                        Text("64 bit")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsContent(
            isBigEndian = true,
            isMsbFirst = true,
            bitLength = 32,
            onBack = {},
            onSaveSettings = { _, _, _ -> }
        )
    }
}

