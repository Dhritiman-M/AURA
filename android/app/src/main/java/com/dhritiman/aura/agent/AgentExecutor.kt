package com.dhritiman.aura.agent

import android.util.Log
import com.dhritiman.aura.accessibility.AuraAccessibilityService
import com.dhritiman.aura.accessibility.UIActionResult


class AgentExecutor {


    companion object {

        private const val TAG =
            "AURA_AGENT_EXECUTOR"
    }



    fun execute(
        decision: ActionDecision
    ): Boolean {


        when(decision) {


            is ActionDecision.Perform -> {


                Log.d(
                    TAG,
                    "Performing action: " +
                            decision.action
                )


                val service =
                    AuraAccessibilityService.instance


                if(service == null) {

                    Log.e(
                        TAG,
                        "Accessibility service unavailable"
                    )

                    return false
                }



                val result =
                    service.executeAction(
                        decision.action
                    )



                return result ==
                        UIActionResult.Success
            }



            is ActionDecision.Completed -> {


                Log.d(
                    TAG,
                    "Goal completed: " +
                            decision.message
                )

                return true
            }



            is ActionDecision.Failed -> {


                Log.e(
                    TAG,
                    "Decision failed: " +
                            decision.reason
                )

                return false
            }
        }
    }
}