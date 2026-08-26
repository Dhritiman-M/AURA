package com.dhritiman.aura.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dhritiman.aura.apps.InstalledApp

@Composable
fun AppSelectionScreen(
    apps: List<InstalledApp>,
    selectedApps: Set<String>,
    onAppSelected: (String, Boolean) -> Unit,
    onSave: () -> Unit
) {

    /*
     * Stores whatever the user types
     * into the search box.
     */
    var searchQuery by remember {
        mutableStateOf("")
    }

    /*
     * Filter the app list according to
     * the search query.
     *
     * We search both:
     * - application name
     * - package name
     *
     * Example:
     *
     * "whatsapp" -> WhatsApp
     * "spotify" -> Spotify
     * "com.whatsapp" -> WhatsApp
     */
    val filteredApps =
        apps.filter { app ->

            val query =
                searchQuery.trim()

            if (query.isEmpty()) {

                true

            } else {

                app.name.contains(
                    query,
                    ignoreCase = true
                ) ||
                app.packageName.contains(
                    query,
                    ignoreCase = true
                )
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "AURA App Access"
        )

        Text(
            text = "Select the apps AURA can work with.",
            modifier = Modifier.padding(
                top = 8.dp,
                bottom = 12.dp
            )
        )

        /*
         * Search bar
         */
        OutlinedTextField(

            value = searchQuery,

            onValueChange = {
                searchQuery = it
            },

            modifier = Modifier.fillMaxWidth(),

            label = {
                Text("Search apps")
            },

            placeholder = {
                Text("e.g. WhatsApp, Chrome, Spotify")
            },

            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {

            items(
                items = filteredApps,
                key = { it.packageName }
            ) { app ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 8.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically,

                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    Text(
                        text = app.name,

                        modifier =
                            Modifier.weight(1f)
                    )

                    Checkbox(

                        checked =
                            selectedApps.contains(
                                app.packageName
                            ),

                        onCheckedChange = {
                            checked ->

                            onAppSelected(
                                app.packageName,
                                checked
                            )
                        }
                    )
                }
            }
        }

        Button(

            onClick = onSave,

            modifier =
                Modifier.fillMaxWidth()

        ) {

            Text(
                "Save Selection"
            )
        }
    }
}