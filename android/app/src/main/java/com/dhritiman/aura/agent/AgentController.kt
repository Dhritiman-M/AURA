package com.dhritiman.aura.agent

import android.util.Log
import com.dhritiman.aura.accessibility.UIAction


class AgentController (
    private val taskExecutor: TaskExecutor,
    private val selectedApps: Set<String>
){

    companion object {

        private const val TAG =
            "AURA_AGENT"
    }

    private val executor =
    AgentExecutor(
        taskExecutor,
        selectedApps
    )
    
    private val history =
    ActionHistory()

    private val goalEvaluator =
    GoalEvaluator()

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
            /*
            * Some goals do not require
            * screen observation initially.
            *
            * Example:
            * open whatsapp
            */

            if(
                state.stepCount == 0 &&
                state.goal.lowercase()
                    .startsWith("open ")
            ) {

                val decision =
                    decide(state)

                when(decision) {

                    is ActionDecision.OpenApp -> {


                        val success =
                            executor.execute(
                                decision
                            )


                        if(!success) {

                            Log.e(
                                TAG,
                                "Could not open app"
                            )

                            break
                        }


                        Thread.sleep(1500)

                    }


                    else -> {}
                }


                state =
                    state.copy(
                        stepCount =
                            state.stepCount + 1
                    )


                continue
            }
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

            val completed =
                goalEvaluator.evaluate(
                    state.goal,
                    screen
                )

            if(completed) {

                Log.d(
                    TAG,
                    "Goal completed successfully"
                )

                break
            }

            Log.d(
                TAG,
                "Observation step ${state.stepCount}"
            )

            val decision =
                decide(
                    state
                )

            when(decision) {

                is ActionDecision.OpenApp -> {

                    val success =
                    executor.execute(
                        decision
                    )

                    if(!success){

                    Log.e(
                        TAG,
                        "Could not open app"
                    )

                    break
                    }
                }

                is ActionDecision.PerformUI -> {

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

        val goal =
            state.goal.lowercase()
        if(
                goal.startsWith("open ")
            ) {
    
                val appName =
                    goal.removePrefix(
                        "open "
                    )
                    .trim()
    
    
                return ActionDecision.OpenApp(
    
                    appName =
                        appName,
    
                    reason =
                        "Application launch required"
                )
            }
        val screen =
            state.currentScreen
                ?: return ActionDecision.Failed(
                    "No screen available"
                )

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

                return ActionDecision.PerformUI(

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