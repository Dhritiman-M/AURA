package com.dhritiman.aura.agent

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.dhritiman.aura.apps.AppManager

import com.dhritiman.aura.accessibility.AuraAccessibilityService
import com.dhritiman.aura.accessibility.UIAction
import com.dhritiman.aura.accessibility.UIActionResult
import com.dhritiman.aura.apps.SystemAppResolver

class TaskExecutor(
    private val context: Context,
    private val appManager: AppManager
) {

    private val systemAppResolver =
    SystemAppResolver(
        context
    )
    companion object {
        private const val TAG = "AURA_TASK"
    }

    fun execute(
        task: Task,
        selectedApps: Set<String>
    ): Boolean {

        Log.d(
            TAG,
            "Executing task: $task"
        )

        return when (task.type) {

            TaskType.OPEN_SETTINGS -> {

                val intent =
                    Intent(Settings.ACTION_SETTINGS)

                intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                )

                context.startActivity(intent)

                true
            }

            TaskType.OPEN_APP -> {

                val appName =
                    task.appName

                Log.d(
                    TAG,
                    "Requested app: $appName"
                )

                if (appName.isNullOrBlank()) {

                    Log.e(
                        TAG,
                        "App name is empty"
                    )

                    return false
                }

                /*
                 * Find the application.
                 */

                val systemIntent =
                    systemAppResolver.resolve(
                        appName
                    )

                if(systemIntent != null) {

                    systemIntent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    )

                    context.startActivity(
                        systemIntent
                    )

                    Log.d(
                        TAG,
                        "Opened system app: $appName"
                    )

                    return true
                }

                val app =
                    appManager.findApp(appName)

                if (app == null) {

                    Log.e(
                        TAG,
                        "AppManager could not find: $appName"
                    )

                    Toast.makeText(
                        context,
                        "App not found: $appName",
                        Toast.LENGTH_LONG
                    ).show()

                    return false
                }

                Log.d(
                    TAG,
                    "FOUND APP"
                )

                Log.d(
                    TAG,
                    "Name: ${app.name}"
                )

                Log.d(
                    TAG,
                    "Package: ${app.packageName}"
                )

                Log.d(
                    TAG,
                    "Activity: ${app.activityName}"
                )

                /*
                 * SECURITY CHECK
                 *
                 * The user must explicitly select
                 * this application before AURA can
                 * interact with it.
                 */
                if (
                    !selectedApps.contains(
                        app.packageName
                    )
                ) {

                    Log.w(
                        TAG,
                        "ACCESS DENIED: " +
                                "${app.name} " +
                                "(${app.packageName}) " +
                                "is not selected"
                    )

                    Toast.makeText(
                        context,
                        "AURA does not have access to ${app.name}",
                        Toast.LENGTH_LONG
                    ).show()

                    return false
                }

                Log.d(
                    TAG,
                    "ACCESS GRANTED: " +
                            "${app.name}"
                )

                /*
                 * Build the Android component.
                 */
                val component =
                    ComponentName(
                        app.packageName,
                        app.activityName
                    )

                /*
                 * Create launch intent.
                 */
                val intent =
                    Intent(
                        Intent.ACTION_MAIN
                    ).apply {

                        addCategory(
                            Intent.CATEGORY_LAUNCHER
                        )

                        this.component =
                            component

                        addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                        )
                    }

                Log.d(
                    TAG,
                    "Launching: $component"
                )

                try {

                    context.startActivity(intent)

                    Log.d(
                        TAG,
                        "startActivity SUCCESS"
                    )

                    true

                } catch (
                    exception: Exception
                ) {

                    Log.e(
                        TAG,
                        "startActivity FAILED",
                        exception
                    )

                    Toast.makeText(
                        context,
                        "Could not open ${app.name}",
                        Toast.LENGTH_LONG
                    ).show()

                    false
                }
            }

            TaskType.UNKNOWN -> {

                Log.d(
                    TAG,
                    "Unknown task"
                )

                false
            }
        }
    }
    fun executeUIAction(action: UIAction): 
    UIActionResult {

        val service =
            AuraAccessibilityService.instance

        if (service == null) {

            return UIActionResult.Failed
        }
        return service.executeAction(
            action
        )
    }
}