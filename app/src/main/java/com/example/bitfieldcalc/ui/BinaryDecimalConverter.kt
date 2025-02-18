package com.example.bitfieldcalc.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bitfieldcalc.ui.viewmodel.BinaryDecimalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BinaryDecimalConverter(viewModel: BinaryDecimalViewModel = viewModel()) {
    // ViewModelから状態を取得
    val input = viewModel.input.value
    val result = viewModel.result.value
    val isBinaryMode = viewModel.isBinaryMode.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // モード切り替えボタン（色で状態を視覚化）
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { viewModel.toggleMode() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isBinaryMode) Color.Green else Color.Gray
                )
            ) {
                Text("2進数 -> 10進数")
            }
            Button(
                onClick = { viewModel.toggleMode() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isBinaryMode) Color.Green else Color.Gray
                )
            ) {
                Text("10進数 -> 2進数")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 入力フィールド
        OutlinedTextField(
            value = input,
            onValueChange = {},
            label = { Text("Enter value") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // テンキーボタン
        Keypad(
            isBinaryMode = isBinaryMode,
            onKeyClick = { viewModel.updateInput(it) },
            onClear = { viewModel.clearInput() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 変換ボタン
        Button(onClick = { viewModel.convert() }) {
            Text("変換")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 結果表示
        Text(
            text = if (result.isEmpty()) "結果" else result,
            fontSize = 24.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun Keypad(isBinaryMode: Boolean, onKeyClick: (String) -> Unit, onClear: () -> Unit) {
    // 2進数モードかどうかで表示するボタンを変更
    val buttonValues = if (isBinaryMode) {
        listOf(
            listOf("1", "0", "C")
        )
    } else {
        listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("C", "0", "=")
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        buttonValues.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { value ->
                    Button(
                        onClick = {
                            when (value) {
                                "C" -> onClear()
                                "=" -> {}  // 変換処理は既にメインでやっているのでここでは何もしない
                                else -> onKeyClick(value)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(value)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

