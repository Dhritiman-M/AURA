package com.dhritiman.aura.agent

import android.util.Log
import com.dhritiman.aura.accessibility.ScreenState

class GoalEvaluator {

    companion object {

        private const val TAG =
            "AURA_GOAL"
    }

    fun evaluate(
        goal: String,
        screen: ScreenState?
    ): Boolean {

        if(screen == null) {

            return false
        }

        val normalizedGoal =
            goal.lowercase()

        /*
         * Goal:
         *
         * open whatsapp
         *
         * Completion:
         *
         * WhatsApp package visible
         */

        if(
            normalizedGoal.startsWith(
                "open "
            )
        )
        {

            val appName =
                normalizedGoal
                    .removePrefix(
                        "open "
                    )
                    .trim()

            val currentPackage =
                screen.packageName
                    ?.lowercase()
                    ?: ""

            return when(appName) {

                "whatsapp" ->
                    currentPackage.contains(
                        "whatsapp"
                    )

                "chrome" ->
                    currentPackage.contains(
                        "chrome"
                    )

                "camera" ->
                    currentPackage.contains(
                        "camera"
                    )

                "settings" ->
                    currentPackage.contains(
                        "settings"
                    )

                else ->
                    false
            }
        }

        /*
         * Generic search completion.
         *
         * Later this becomes AI-based.
         */

        if(
            normalizedGoal.contains(
                "search"
            )
        ) {

            val hasResults =
                screen.elements.any { element ->

                    val text =
                        element.text
                            ?.lowercase()
                            ?: ""


                    text.contains(
                        "result"
                    )
                    ||
                    text.contains(
                        "john"
                    )
                }

            Log.d(
                TAG,
                "Search completion: $hasResults"
            )

            return hasResults
        }

        return false
    }
}