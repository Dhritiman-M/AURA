package com.dhritiman.aura.agent

import com.dhritiman.aura.accessibility.UIAction


sealed class ActionDecision {


    data class Perform(
        val action: UIAction,
        val reason: String
    ): ActionDecision()


    data class Completed(
        val message: String
    ): ActionDecision()


    data class Failed(
        val reason: String
    ): ActionDecision()
}