package com.dhritiman.aura.agent

import android.util.Log
import com.dhritiman.aura.accessibility.UIAction
import com.dhritiman.aura.accessibility.UIActionResult

class TaskPlanExecutor(
    private val taskExecutor: TaskExecutor
) {

    companion object {

        private const val TAG =
            "AURA_PLAN"

        private const val MAX_RETRIES =
            3
    }

    fun execute(
        plan: TaskPlan,
        selectedApps: Set<String>
    ): Boolean {

        Log.d(
            TAG,
            "Starting task plan: " +
                    "${plan.steps.size} steps"
        )

        for (
            (index, step)
            in plan.steps.withIndex()
        ) {

            Log.d(
                TAG,
                "--------------------------------"
            )

            Log.d(
                TAG,
                "Executing step ${index + 1}: $step"
            )

            val success =
                executeStepWithRetry(
                    step,
                    selectedApps
                )

            if (!success) {

                Log.e(
                    TAG,
                    "Step ${index + 1} failed"
                )

                return false
            }

            /*
             * Give the Android UI a small amount
             * of time to settle before inspecting
             * the next state.
             */
            waitForUiToSettle()
        }

        Log.d(
            TAG,
            "Task plan completed successfully"
        )

        return true
    }

    private fun executeStepWithRetry(
        step: PlanStep,
        selectedApps: Set<String>
    ): Boolean {

        for (
            attempt in 1..MAX_RETRIES
        ) {

            Log.d(
                TAG,
                "Attempt $attempt/$MAX_RETRIES"
            )

            val result =
                executeStep(
                    step,
                    selectedApps
                )

            if (result) {

                Log.d(
                    TAG,
                    "Step succeeded on attempt $attempt"
                )

                return true
            }

            if (
                attempt < MAX_RETRIES
            ) {

                Log.d(
                    TAG,
                    "Step failed. Retrying..."
                )

                waitForUiToSettle()
            }
        }

        return false
    }

    private fun executeStep(
        step: PlanStep,
        selectedApps: Set<String>
    ): Boolean {

        return when (step) {

            is PlanStep.OpenApp -> {

                taskExecutor.execute(

                    Task(
                        command =
                            "Open ${step.appName}",

                        type =
                            TaskType.OPEN_APP,

                        appName =
                            step.appName
                    ),

                    selectedApps
                )
            }

            is PlanStep.UI -> {

                val result =
                    taskExecutor
                        .executeUIAction(
                            step.action
                        )

                result ==
                        UIActionResult.Success
            }
        }
    }

    private fun waitForUiToSettle() {

        Thread.sleep(300)
    }
}