package com.dhritiman.aura.agent


enum class GoalStatus {

    RUNNING,

    COMPLETED,

    FAILED,

    WAITING
}

data class GoalState(

    val goal: String,

    val status: GoalStatus =
        GoalStatus.RUNNING,


    val currentStep: Int = 0,


    val totalSteps: Int = 0
)