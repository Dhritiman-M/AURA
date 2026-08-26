package com.dhritiman.aura.agent

import android.util.Log
import com.dhritiman.aura.accessibility.AuraAccessibilityService
import com.dhritiman.aura.accessibility.UIAction

class AgentController {

    companion object {

        private const val TAG =
            "AURA_AGENT"
    }

    fun start(
        goal: String
    ) {


        var state =
            AgentState(
                goal = goal
            )

        Log.d(
            TAG,
            "Starting goal: $goal"
        )

        while(
            state.stepCount < 20
        ) {

            val screen =
                AuraAccessibilityService
                    .instance
                    ?.observeCurrentScreen()

            state =
                state.copy(
                    currentScreen =
                        screen,

                    stepCount =
                        state.stepCount + 1
                )

            Log.d(
                TAG,
                "Observation step ${state.stepCount}"
            )

            val decision =
                decide(state)

            when(decision) {

                is ActionDecision.Perform -> {

                    Log.d(
                        TAG,
                        "Executing: " +
                        decision.action
                    )

                    // execution will be connected
                    // in next step
                }

                is ActionDecision.Completed -> {

                    Log.d(
                        TAG,
                        "Goal completed"
                    )

                    break
                }

                is ActionDecision.Failed -> {

                    Log.e(
                        TAG,
                        decision.reason
                    )
                    break
                }
            }
        }
    }

    private fun decide(
        state: AgentState
    ): ActionDecision {

        val screen =
            state.currentScreen
                ?: return ActionDecision.Failed(
                    "No screen available"
                )


        val goal =
            state.goal.lowercase()


        /*
        * Temporary rule:
        *
        * If the goal requires searching,
        * find a search element.
        */
        if (
            goal.contains("search")
        ) {


            val searchExists =
                screen.elements.any { element ->


                    val text =
                        element.text
                            ?.lowercase()
                            ?: ""


                    val description =
                        element.contentDescription
                            ?.lowercase()
                            ?: ""


                    text.contains(
                        "search"
                    )
                    ||
                    description.contains(
                        "search"
                    )
                }



            if (searchExists) {


                return ActionDecision.Perform(

                    action =
                        UIAction.ClickText(
                            "Search"
                        ),

                    reason =
                        "Search element found on screen"
                )
            }
        }



        return ActionDecision.Failed(
            "No suitable action found"
        )
    }
}