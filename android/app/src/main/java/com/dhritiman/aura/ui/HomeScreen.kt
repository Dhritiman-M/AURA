package com.dhritiman.aura.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onCommand: (String) -> Unit,
    onManageApps: () -> Unit
) {

    var command by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "AURA"
        )

        Text(
            text = "Personal AI Agent",
            modifier = Modifier.padding(top = 8.dp)
        )

        OutlinedTextField(
            value = command,

            onValueChange = {
                command = it
            },

            label = {
                Text("Enter a command")
            },

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        )

        Button(
            onClick = {
                if (command.isNotBlank()) {
                    onCommand(command)
                }
            },

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {

            Text("Execute")
        }

        Button(
            onClick = onManageApps,

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {

            Text("Manage Apps")
        }
    }
}