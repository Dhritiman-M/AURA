package com.dhritiman.aura.agent

import android.util.Log
import com.dhritiman.aura.accessibility.AuraAccessibilityService

class ActionVerifier {

    companion object {
        private const val TAG = "AURA_VERIFY"
    }

    fun verify(
        expectedText: String? = null,
        expectedPackage: String? = null
    ): Boolean {

        val service =
            AuraAccessibilityService.instance

        if (service == null) {

            Log.d(
                TAG,
                "Accessibility service unavailable"
            )

            return false
        }

        val state =
            service.observeCurrentScreen()

        if (state == null) {

            Log.d(
                TAG,
                "Could not observe current screen"
            )

            return false
        }

        /*
         * Verify application/package.
         */
        if (
            expectedPackage != null &&
            state.packageName != expectedPackage
        ) {

            Log.d(
                TAG,
                "Expected package: " +
                        expectedPackage +
                        ", actual: " +
                        state.packageName
            )

            return false
        }

        /*
         * If there is no specific UI expectation,
         * successfully observing the screen is enough
         * for now.
         */
        if (expectedText == null) {

            return true
        }

        /*
         * Look for the expected text.
         */
        val found =
            state.elements.any { element ->

                element.text
                    ?.trim()
                    ?.equals(
                        expectedText,
                        ignoreCase = true
                    ) == true ||

                element.contentDescription
                    ?.trim()
                    ?.equals(
                        expectedText,
                        ignoreCase = true
                    ) == true
            }

        Log.d(
            TAG,
            "Expected text '$expectedText': " +
                    if (found) "FOUND"
                    else "NOT FOUND"
        )

        return found
    }
}