package com.dhritiman.aura.agent

import android.util.Log
import com.dhritiman.aura.accessibility.UIActionResult

class TaskPlanExecutor(
    private val taskExecutor: TaskExecutor
) {

    companion object {

        private const val TAG =
            "AURA_PLAN"

        private const val MAX_RETRIES =
            3

        private const val UI_SETTLE_DELAY =
            500L

        private const val VERIFICATION_ATTEMPTS =
            5

        private const val VERIFICATION_DELAY =
            300L
    }

    private val actionVerifier =
        ActionVerifier()

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

            val executed =
                executeStep(
                    step,
                    selectedApps
                )

            if (!executed) {

                Log.d(
                    TAG,
                    "Action execution failed"
                )

                waitForUiToSettle()

                continue
            }

            /*
             * Give the application time to
             * update its UI.
             */
            waitForUiToSettle()

            val verified =
                verifyStep(step)

            if (verified) {

                Log.d(
                    TAG,
                    "Step verified successfully"
                )

                return true
            }

            Log.d(
                TAG,
                "Step verification failed"
            )

            waitForUiToSettle()
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

    private fun verifyStep(
        step: PlanStep
    ): Boolean {

        return when (step) {

            is PlanStep.OpenApp -> {

                waitForExpectedState(
                    expectedPackage =
                        step.expectedPackage
                )
            }

            is PlanStep.UI -> {

                /*
                 * If this step doesn't yet have
                 * an expected result, observing
                 * the screen is enough for now.
                 */
                waitForExpectedState(
                    expectedText =
                        step.expectedText
                )
            }
        }
    }

    private fun waitForExpectedState(
        expectedText: String? = null,
        expectedPackage: String? = null
    ): Boolean {

        repeat(
            VERIFICATION_ATTEMPTS
        ) {

            if (
                actionVerifier.verify(
                    expectedText =
                        expectedText,

                    expectedPackage =
                        expectedPackage
                )
            ) {

                return true
            }

            Thread.sleep(
                VERIFICATION_DELAY
            )
        }

        return false
    }

    private fun waitForUiToSettle() {

        Thread.sleep(
            UI_SETTLE_DELAY
        )
    }
}