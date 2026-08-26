package com.dhritiman.aura.agent

import com.dhritiman.aura.accessibility.UIAction

sealed class PlanStep {

    data class OpenApp(
        val appName: String
    ) : PlanStep()

    data class UI(
        val action: UIAction
    ) : PlanStep()
}

data class TaskPlan(
    val steps: List<PlanStep>
)