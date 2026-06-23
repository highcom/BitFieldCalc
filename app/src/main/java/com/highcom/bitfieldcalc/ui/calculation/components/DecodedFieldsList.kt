package com.highcom.bitfieldcalc.ui.calculation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.highcom.bitfieldcalc.ui.calculation.FieldResult

@Composable
fun DecodedFieldsList(fields: List<FieldResult>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = "【フィールドデコード結果】",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(fields) { field ->
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "■ ${field.name}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "[${field.msb}:${field.lsb}]",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    text = "  : ${field.hexValue}   (${field.decValue})",
                    style = MaterialTheme.typography.bodyMedium
                )
                HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DecodedFieldsListPreview() {
    MaterialTheme {
        DecodedFieldsList(
            fields = listOf(
                FieldResult("Field 1", "0x34", "52", 7, 0),
                FieldResult("Field 2", "0x12", "18", 15, 8)
            )
        )
    }
}

