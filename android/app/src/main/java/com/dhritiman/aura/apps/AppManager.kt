package com.dhritiman.aura.apps

import android.content.Context
import android.content.Intent
import android.util.Log

class AppManager(
    private val context: Context
) {

    companion object {
        private const val TAG = "AURA_APPS"
    }

    private val packageManager =
        context.packageManager

    fun getInstalledApps(): List<InstalledApp> {

        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val apps =
            packageManager
                .queryIntentActivities(intent, 0)
                .map { resolveInfo ->

                    val activityInfo =
                        resolveInfo.activityInfo

                    InstalledApp(
                        name = resolveInfo
                            .loadLabel(packageManager)
                            .toString(),

                        packageName =
                            activityInfo.packageName,

                        activityName =
                            activityInfo.name
                    )
                }
                .distinctBy {
                    it.packageName
                }
                .sortedBy {
                    it.name.lowercase()
                }

        Log.d(
            TAG,
            "Found ${apps.size} launcher apps"
        )

        return apps
    }

    fun findApp(
        appName: String
    ): InstalledApp? {

        val normalizedName =
            normalize(appName)

        Log.d(
            TAG,
            "Searching for app: '$normalizedName'"
        )

        val apps =
            getInstalledApps()

        /*
         * 1. Exact application-name match
         */
        val exactMatch =
            apps.firstOrNull { app ->

                normalize(app.name) ==
                        normalizedName
            }

        if (exactMatch != null) {

            Log.d(
                TAG,
                "Exact match: ${exactMatch.name} " +
                        "${exactMatch.packageName}"
            )

            return exactMatch
        }

        /*
         * 2. Application name contains requested name
         */
        val partialMatch =
            apps.firstOrNull { app ->

                normalize(app.name)
                    .contains(normalizedName)
            }

        if (partialMatch != null) {

            Log.d(
                TAG,
                "Partial match: ${partialMatch.name} " +
                        "${partialMatch.packageName}"
            )

            return partialMatch
        }

        /*
         * 3. Requested name contains application name
         */
        val reversePartialMatch =
            apps.firstOrNull { app ->

                normalizedName
                    .contains(normalize(app.name))
            }

        if (reversePartialMatch != null) {

            Log.d(
                TAG,
                "Reverse partial match: " +
                        "${reversePartialMatch.name} " +
                        "${reversePartialMatch.packageName}"
            )

            return reversePartialMatch
        }

        /*
         * 4. Direct package-name match
         *
         * This is useful if the user eventually says:
         *
         * Open com.whatsapp
         */
        val packageMatch =
            apps.firstOrNull { app ->

                normalize(app.packageName) ==
                        normalizedName
            }

        if (packageMatch != null) {

            Log.d(
                TAG,
                "Package match: " +
                        "${packageMatch.name} " +
                        "${packageMatch.packageName}"
            )

            return packageMatch
        }

        /*
         * Nothing found
         */
        Log.e(
            TAG,
            "No application matched '$normalizedName'"
        )

        /*
         * Print applications containing
         * 'what' or 'whatsapp' to logcat.
         *
         * This is temporary debugging information.
         */
        apps
            .filter { app ->

                val name =
                    normalize(app.name)

                name.contains("what") ||
                        name.contains("whatsapp") ||
                        app.packageName
                            .lowercase()
                            .contains("whatsapp")
            }
            .forEach { app ->

                Log.d(
                    TAG,
                    "WhatsApp candidate -> " +
                            "${app.name} | " +
                            "${app.packageName} | " +
                            "${app.activityName}"
                )
            }

        return null
    }

    private fun normalize(
        value: String
    ): String {

        return value
            .trim()
            .lowercase()
            .replace(
                Regex("\\s+"),
                " "
            )
    }
}