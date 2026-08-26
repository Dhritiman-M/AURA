package com.dhritiman.aura.agent

import com.dhritiman.aura.accessibility.UIAction

object TestPlanFactory {

    fun createWhatsAppTestPlan(): TaskPlan {

        return TaskPlan(
            steps = listOf(

                /*
                 * Step 1:
                 * Open WhatsApp.
                 */
                PlanStep.OpenApp(
                    appName = "WhatsApp"
                ),

                /*
                 * Step 2:
                 * Press the Android back button.
                 *
                 * This is only a basic test that
                 * Accessibility UI actions work
                 * after launching an app.
                 */
                PlanStep.UI(
                    UIAction.PressBack
                )
            )
        )
    }
}