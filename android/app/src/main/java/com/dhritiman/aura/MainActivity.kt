package com.dhritiman.aura

import android.os.Bundle
import android.widget.Toast
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dhritiman.aura.agent.CommandProcessor
import com.dhritiman.aura.agent.TaskExecutor
import com.dhritiman.aura.apps.AppManager
import com.dhritiman.aura.data.AppPreferences
import com.dhritiman.aura.ui.AppSelectionScreen
import com.dhritiman.aura.ui.AppSelectionViewModel
import com.dhritiman.aura.ui.AppSelectionViewModelFactory
import com.dhritiman.aura.ui.HomeScreen

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.dhritiman.aura.agent.TaskPlanExecutor
import com.dhritiman.aura.agent.TestPlanFactory
import com.dhritiman.aura.agent.TaskPlanner

class MainActivity : ComponentActivity() {

    private val commandProcessor =
        CommandProcessor()
    private val taskPlanner =
        TaskPlanner()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        val appManager =
            AppManager(this)

        val taskExecutor =
            TaskExecutor(
                this,
                appManager
            )

        val taskPlanExecutor =
            TaskPlanExecutor(
                taskExecutor
            )

        val installedApps =
            appManager.getInstalledApps()

        val appPreferences =
            AppPreferences(this)

        

        setContent {

            MaterialTheme {

                Surface {

                    var showAppSelection by
                        remember {
                            mutableStateOf(false)
                        }

                    var selectedApps by
                        remember {
                            mutableStateOf<Set<String>>(
                                emptySet()
                            )
                        }
                    LaunchedEffect(Unit) {
                        appPreferences.selectedApps.collect { savedApps ->

                            selectedApps = savedApps
                        }
                    }

                    if (showAppSelection) {

                        AppSelectionScreen(

                            apps = installedApps,

                            selectedApps =
                                selectedApps,

                            onAppSelected = {
                                    packageName,
                                    selected ->

                                selectedApps =
                                    if (selected) {
                                        selectedApps +
                                                packageName
                                    } else {
                                        selectedApps -
                                                packageName
                                    }
                            },

                            onSave = {

                                kotlinx.coroutines.CoroutineScope(
                                    kotlinx.coroutines.Dispatchers.IO
                                ).launch {

                                    appPreferences.saveSelectedApps(
                                        selectedApps
                                    )
                                }

                                showAppSelection = false

                                Toast.makeText(
                                    this@MainActivity,
                                    "${selectedApps.size} apps selected",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )

                    } else {

                        HomeScreen(

                            onCommand = { command ->

                                Log.d(
                                    "AURA_PLAN",
                                    "User command: $command"
                                )

                                val plan =
                                    taskPlanner.createPlan(
                                        command
                                    )

                                /*
                                * Empty plan means the planner
                                * doesn't understand the command yet.
                                */
                                if (plan.steps.isEmpty()) {

                                    Toast.makeText(
                                        this@MainActivity,
                                        "I don't know how to do that yet.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    return@HomeScreen
                                }

                                Log.d(
                                    "AURA_PLAN",
                                    "Generated plan: $plan"
                                )

                                /*
                                * Execute the generated plan.
                                */
                                val success =
                                    taskPlanExecutor.execute(
                                        plan,
                                        selectedApps
                                    )

                                if (!success) {

                                    Toast.makeText(
                                        this@MainActivity,
                                        "Task failed.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },

                            onManageApps = {
                                showAppSelection = true
                            }
                        )
                    }
                }
            }
        }
    }
}