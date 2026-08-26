package com.dhritiman.aura.agent

import android.util.Log
import com.dhritiman.aura.accessibility.UIAction


class AgentController {

    companion object {

        private const val TAG =
            "AURA_AGENT"
    }

    private val executor =
        AgentExecutor()
    
    private val history =
    ActionHistory()

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
            state.stepCount < 10
        ) {

            val screen =
                com.dhritiman.aura.accessibility
                    .AuraAccessibilityService
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
                decide(
                    state
                )

            when(decision) {

                is ActionDecision.Perform -> {

                    Log.d(
                        TAG,
                        "Executing: " +
                                decision.action
                    )

                    val success =
                        executor.execute(
                            decision
                        )
                    
                    history.add(

                        actionName =
                            decision.action.toString(),

                        success =
                            success
                    )

                    Log.d(
                        TAG,
                        "Action history size: " +
                                history.getHistory().size
                    )
                    
                    if(success) {

                        Log.d(
                            TAG,
                            "Action executed"
                        )

                    }
                    else {

                        Log.e(
                            TAG,
                            "Action failed"
                        )

                        break
                    }


                }

                is ActionDecision.Completed -> {

                    Log.d(
                        TAG,
                        decision.message
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

            /*
             * Allow UI to update
             */

            Thread.sleep(
                800
            )
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

        if(
            goal.contains(
                "search"
            )
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

            if(searchExists) {

                return ActionDecision.Perform(

                    action =
                        UIAction.ClickText(
                            "Search"
                        ),

                    reason =
                        "Search button detected"
                )
            }
        }

        return ActionDecision.Failed(
            "No action available"
        )
    }
}