package com.highcom.bitfieldcalc.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.highcom.bitfieldcalc.R
import com.highcom.bitfieldcalc.ui.calculation.BitFieldCalcViewModel
import com.highcom.bitfieldcalc.ui.calculation.MainCalculationScreen
import com.highcom.bitfieldcalc.ui.manager.StructureViewModel
import com.highcom.bitfieldcalc.ui.settings.SettingsScreen
import com.highcom.bitfieldcalc.ui.settings.SettingsViewModel
import com.highcom.bitfieldcalc.ui.structure.StructureEditScreen
import com.highcom.bitfieldcalc.ui.structure.StructureManagerScreen
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
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
        MobileAds.initialize(this) {}
        setContent {
            AppRoot(vm = vm, structureVm = structureVm, settingsVm = settingsVm)
        }
    }
}

@Composable
fun AdBanner() {
    val adUnitId = stringResource(id = R.string.admob_banner_unit_id)
    val isAdLoaded = remember { mutableStateOf(false) }

    // 広告がロードされた場合のみ領域を確保し、表示する
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isAdLoaded.value) 50.dp else 0.dp)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = adUnitId
                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            isAdLoaded.value = true
                        }
                        override fun onAdFailedToLoad(error: LoadAdError) {
                            isAdLoaded.value = false
                        }
                    }
                    loadAd(AdRequest.Builder().build())
                }
            },
            onRelease = { adView ->
                adView.destroy()
            }
        )
    }
}

@Composable
fun AppRoot(vm: BitFieldCalcViewModel, structureVm: StructureViewModel, settingsVm: SettingsViewModel) {
    val screenState = remember { mutableStateOf<Screen>(Screen.Main) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
            Box(modifier = Modifier.weight(1f)) {
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
            AdBanner()
        }
    }
}
