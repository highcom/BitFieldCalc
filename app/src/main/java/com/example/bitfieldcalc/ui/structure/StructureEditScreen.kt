package com.example.bitfieldcalc.ui.structure

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StructureEditScreen(structureId: Long?, onDone: () -> Unit) {
    // Simple form: name and tag
    val nameState = remember { mutableStateOf("") }
    val tagState = remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = if (structureId == null) "Create Structure" else "Edit Structure")
        OutlinedTextField(value = nameState.value, onValueChange = { nameState.value = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        OutlinedTextField(value = tagState.value, onValueChange = { tagState.value = it }, label = { Text("Tag") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))

        Button(onClick = { /* TODO: validate & save via ViewModel */ onDone() }, modifier = Modifier.padding(top = 12.dp)) {
            Text("Save")
        }
    }
}

