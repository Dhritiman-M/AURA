package com.dhritiman.aura.agent

import android.util.Log
import com.dhritiman.aura.accessibility.UIActionResult

class TaskPlanExecutor(
    private val taskExecutor: TaskExecutor
) {

    companion object {

        private const val TAG =
            "AURA_PLAN"

        /*
         * Temporary delay between actions.
         *
         * We will replace this later with
         * intelligent UI-state waiting.
         */
        private const val ACTION_DELAY_MS = 500L
    }

    /**
     * Executes every step in a TaskPlan sequentially.
     *
     * selectedApps is passed all the way down to
     * TaskExecutor so that the user's app-selection
     * security restriction is always respected.
     */
    fun execute(
        plan: TaskPlan,
        selectedApps: Set<String>
    ): Boolean {

        Log.d(
            TAG,
            "================================"
        )

        Log.d(
            TAG,
            "Starting TaskPlan"
        )

        Log.d(
            TAG,
            "Total steps: ${plan.steps.size}"
        )

        Log.d(
            TAG,
            "Selected apps: ${selectedApps.size}"
        )

        Log.d(
            TAG,
            "================================"
        )

        /*
         * Execute each step in order.
         */
        for (
            (index, step)
            in plan.steps.withIndex()
        ) {

            val stepNumber =
                index + 1

            Log.d(
                TAG,
                "--------------------------------"
            )

            Log.d(
                TAG,
                "Executing step " +
                        "$stepNumber/" +
                        "${plan.steps.size}"
            )

            Log.d(
                TAG,
                "Step: $step"
            )

            /*
             * Execute the appropriate type
             * of plan step.
             */
            val success =
                when (step) {

                    /*
                     * =========================
                     * OPEN APPLICATION
                     * =========================
                     */
                    is PlanStep.OpenApp -> {

                        executeOpenAppStep(
                            step,
                            selectedApps
                        )
                    }

                    /*
                     * =========================
                     * UI ACTION
                     * =========================
                     */
                    is PlanStep.UI -> {

                        executeUIActionStep(
                            step
                        )
                    }
                }

            /*
             * Stop the entire plan immediately
             * if a step fails.
             */
            if (!success) {

                Log.e(
                    TAG,
                    "================================"
                )

                Log.e(
                    TAG,
                    "TASK PLAN FAILED"
                )

                Log.e(
                    TAG,
                    "Failed step: $stepNumber"
                )

                Log.e(
                    TAG,
                    "================================"
                )

                return false
            }

            Log.d(
                TAG,
                "Step $stepNumber completed successfully"
            )

            /*
             * Temporary delay to allow the Android
             * UI to update before the next action.
             */
            if (
                stepNumber <
                plan.steps.size
            ) {

                Thread.sleep(
                    ACTION_DELAY_MS
                )
            }
        }

        Log.d(
            TAG,
            "================================"
        )

        Log.d(
            TAG,
            "TASK PLAN COMPLETED SUCCESSFULLY"
        )

        Log.d(
            TAG,
            "================================"
        )

        return true
    }

    /**
     * Executes an application-opening step.
     */
    private fun executeOpenAppStep(
        step: PlanStep.OpenApp,
        selectedApps: Set<String>
    ): Boolean {

        Log.d(
            TAG,
            "Opening application: " +
                    step.appName
        )

        val task =
            Task(
                command =
                    "Open ${step.appName}",

                type =
                    TaskType.OPEN_APP,

                appName =
                    step.appName
            )

        /*
         * IMPORTANT:
         *
         * selectedApps is passed to TaskExecutor.
         *
         * This means an AI-generated plan cannot
         * bypass the user's app-selection system.
         */
        return taskExecutor.execute(
            task,
            selectedApps
        )
    }

    /**
     * Executes an Accessibility/UI action.
     */
    private fun executeUIActionStep(
        step: PlanStep.UI
    ): Boolean {

        Log.d(
            TAG,
            "Executing UI action: " +
                    step.action
        )

        val result =
            taskExecutor.executeUIAction(
                step.action
            )

        Log.d(
            TAG,
            "UI action result: $result"
        )

        return result ==
                UIActionResult.Success
    }
}