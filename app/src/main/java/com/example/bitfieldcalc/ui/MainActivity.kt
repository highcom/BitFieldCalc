package com.example.bitfieldcalc.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.bitfieldcalc.ui.calculation.BitFieldCalcViewModel
import com.example.bitfieldcalc.ui.calculation.MainCalculationScreen
import com.example.bitfieldcalc.ui.manager.StructureViewModel
import com.example.bitfieldcalc.ui.settings.SettingsScreen
import com.example.bitfieldcalc.ui.settings.SettingsViewModel
import com.example.bitfieldcalc.ui.structure.StructureEditScreen
import com.example.bitfieldcalc.ui.structure.StructureManagerScreen
import dagger.hilt.android.AndroidEntryPoint

sealed interface Screen {
    object Main : Screen
    object Manager : Screen
    data class Edit(val structureId: Long?) : Screen
    object Settings : Screen
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm: BitFieldCalcViewModel by viewModels()
    private val structureVm: StructureViewModel by viewModels()
    private val settingsVm: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot(vm = vm, structureVm = structureVm, settingsVm = settingsVm)
        }
    }
}

@Composable
fun AppRoot(vm: BitFieldCalcViewModel, structureVm: StructureViewModel, settingsVm: SettingsViewModel) {
    val screenState = remember { mutableStateOf<Screen>(Screen.Main) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (val s = screenState.value) {
            is Screen.Main -> MainCalculationScreen(
                viewModel = vm,
                onNavigateToSettings = { screenState.value = Screen.Settings },
                onNavigateToManager = { screenState.value = Screen.Manager }
            )
            is Screen.Manager -> StructureManagerScreen(
                viewModel = structureVm,
                onBack = { screenState.value = Screen.Main },
                onAdd = { screenState.value = Screen.Edit(null) },
                onEdit = { id -> screenState.value = Screen.Edit(id) }
            )
            is Screen.Edit -> StructureEditScreen(
                viewModel = structureVm,
                structureId = s.structureId,
                onDone = { screenState.value = Screen.Manager },
                onCancel = { screenState.value = Screen.Manager }
            )
            is Screen.Settings -> SettingsScreen(
                viewModel = settingsVm,
                onBack = { screenState.value = Screen.Main }
            )
        }
    }
}
