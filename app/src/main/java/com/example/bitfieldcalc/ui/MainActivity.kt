package com.example.bitfieldcalc.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bitfieldcalc.ui.calculation.BitFieldCalcViewModel
import com.example.bitfieldcalc.ui.calculation.MainCalculationScreen
import com.example.bitfieldcalc.ui.settings.SettingsScreen
import com.example.bitfieldcalc.ui.structure.StructureEditScreen
import com.example.bitfieldcalc.ui.structure.StructureManagerScreen

sealed interface Screen {
    object Main : Screen
    object Manager : Screen
    data class Edit(val structureId: Long?) : Screen
    object Settings : Screen
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm: BitFieldCalcViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppRoot(vm = vm)
        }
    }
}

@Composable
fun AppRoot(vm: BitFieldCalcViewModel) {
    val screenState = remember { mutableStateOf<Screen>(Screen.Main) }

    Surface(color = MaterialTheme.colorScheme.background) {
        // Top navigation row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { screenState.value = Screen.Main }) { Text("Calc") }
            Button(onClick = { screenState.value = Screen.Manager }) { Text("Structures") }
            Button(onClick = { screenState.value = Screen.Settings }) { Text("Settings") }
        }

        when (val s = screenState.value) {
            is Screen.Main -> MainCalculationScreen(viewModel = vm)
            is Screen.Manager -> StructureManagerScreen(onAdd = { screenState.value = Screen.Edit(null) }, onEdit = { id -> screenState.value = Screen.Edit(id) })
            is Screen.Edit -> StructureEditScreen(structureId = s.structureId, onDone = { screenState.value = Screen.Manager })
            is Screen.Settings -> SettingsScreen()
        }
    }
}

