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
            normalizedGoal.contains(
                "open whatsapp"
            )
        ) {

            val packageName =
                screen.packageName
                    ?.lowercase()
                    ?: ""

            val completed =
                packageName.contains(
                    "whatsapp"
                )

            Log.d(
                TAG,
                "WhatsApp open status: $completed"
            )

            return completed
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