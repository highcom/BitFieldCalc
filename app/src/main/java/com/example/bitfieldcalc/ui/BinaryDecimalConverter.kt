package com.example.bitfieldcalc.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bitfieldcalc.ui.viewmodel.BinaryDecimalViewModel

@Composable
fun BinaryDecimalConverterCompose(viewModel: BinaryDecimalViewModel = viewModel()) {
    BinaryDecimalConverter(
        input = viewModel.input.value,
        result = viewModel.result.value,
        isBinaryMode = viewModel.isBinaryMode.value,
        onToggleMode = { viewModel.toggleMode() },
        onKeyClick = { viewModel.updateInput(it) },
        onClear = { viewModel.clearInput() },
        onConvert = { viewModel.convert() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BinaryDecimalConverter(
    input: String = "",
    result: String = "",
    isBinaryMode: Boolean = true,
    onToggleMode: () -> Unit = {},
    onKeyClick: (String) -> Unit = {},
    onClear: () -> Unit = {},
    onConvert: () -> Unit = {}
) {
    // 2進数モードの時、4桁ずつ区切って表示
    val formattedInput = if (isBinaryMode) formatBinaryInput(input) else input

    // 2進数結果を4桁ずつ区切る
    val formattedResult = if (!isBinaryMode && result.isNotEmpty()) formatBinaryInput(result) else result

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top // 結果表示エリアを上に持っていく
    ) {
        // 結果表示
        Text(
            text = formattedResult.ifEmpty { "結果" },
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp) // 結果表示の下に余白
        )

        // モード切り替えボタン（色で状態を視覚化）
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { onToggleMode() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isBinaryMode) Color.Green else Color.Gray
                )
            ) {
                Text("2進数 -> 10進数")
            }
            Button(
                onClick = { onToggleMode() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isBinaryMode) Color.Green else Color.Gray
                )
            ) {
                Text("10進数 -> 2進数")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 入力フィールド (右詰め入力)
        OutlinedTextField(
            value = formattedInput,
            onValueChange = {},
            label = { Text("Enter value") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(textAlign = TextAlign.End, fontSize = 24.sp),  // 右詰め
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // テンキーボタン
        Keypad(
            isBinaryMode = isBinaryMode,
            onKeyClick = onKeyClick,
            onClear = onClear
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 変換ボタン
        Button(onClick = { onConvert() }) {
            Text("変換")
        }
    }
}

@Composable
fun Keypad(isBinaryMode: Boolean, onKeyClick: (String) -> Unit, onClear: () -> Unit) {
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
                                "=" -> {} // 変換処理はメインでやっているのでここでは何もしない
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

// 2進数の入力を4桁ごとに区切る関数
fun formatBinaryInput(input: String): String {
    // 逆順にして4桁ごとに区切り、再度逆順に戻す
    val reversedInput = input.reversed()
    val chunked = reversedInput.chunked(4).joinToString(" ")
    return chunked.reversed() // 最後に逆順に戻す
}