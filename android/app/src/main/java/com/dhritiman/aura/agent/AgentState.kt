package com.dhritiman.aura.agent

import com.dhritiman.aura.accessibility.ScreenState


data class AgentState(

    val goal: String,

    val currentScreen:
        ScreenState? = null,

    val completedActions:
        List<String> = emptyList(),

    val stepCount:
        Int = 0
)