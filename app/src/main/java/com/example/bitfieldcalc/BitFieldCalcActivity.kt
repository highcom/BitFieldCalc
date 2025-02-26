package com.example.bitfieldcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.bitfieldcalc.ui.BinaryDecimalConverter
import com.example.bitfieldcalc.ui.BinaryDecimalConverterCompose
import com.example.bitfieldcalc.ui.theme.BitFieldCalcTheme

class BitFieldCalcActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BitFieldCalcTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    BinaryDecimalConverterCompose()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BitFieldCalcPreview() {
    BitFieldCalcTheme {
        BinaryDecimalConverter()
    }
}