package com.dhritiman.aura.agent

import com.dhritiman.aura.accessibility.UIAction

sealed class PlanStep {

    data class OpenApp(
        val appName: String,
        val expectedPackage: String? = null
    ) : PlanStep()

    data class UI(
        val action: UIAction,
        val expectedText: String? = null
    ) : PlanStep()
}

data class TaskPlan(
    val steps: List<PlanStep>
)