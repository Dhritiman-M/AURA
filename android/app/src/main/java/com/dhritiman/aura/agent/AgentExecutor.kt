package com.dhritiman.aura.agent

import android.util.Log
import com.dhritiman.aura.accessibility.AuraAccessibilityService
import com.dhritiman.aura.accessibility.UIActionResult


class AgentExecutor(

    private val taskExecutor: TaskExecutor,
    private val selectedApps: Set<String>

) {

    companion object {

        private const val TAG =
            "AURA_AGENT_EXECUTOR"
    }

    private fun waitForUIUpdate() {

        val service =
            AuraAccessibilityService.instance
                ?: return

        val changed =
            service.waitForWindowChange(
                5000
            )

        if(changed) {

            Log.d(
                TAG,
                "UI change detected"
            )

        } else {

            Log.d(
                TAG,
                "UI change timeout"
            )
        }
    }

    fun execute(
        decision: ActionDecision
    ): Boolean {


        return when(decision) {

            is ActionDecision.OpenApp -> {

                Log.d(
                    TAG,
                    "Opening app: ${decision.appName}"
                )

                val result=taskExecutor.execute(

                    Task(

                        command =
                            "Open ${decision.appName}",

                        type =
                            TaskType.OPEN_APP,

                        appName =
                            decision.appName
                    ),

                    selectedApps
                )
                if(result){
                    waitForUIUpdate()
                }
                result
            }

            is ActionDecision.PerformUI -> {


                Log.d(
                    TAG,
                    "Executing UI action: ${decision.action}"
                )


                val service =
                    AuraAccessibilityService.instance



                if(service == null) {


                    Log.e(
                        TAG,
                        "Accessibility unavailable"
                    )


                    false

                }

                else {


                    val result =
                        service.executeAction(
                            decision.action
                        )


                    result ==
                            UIActionResult.Success

                }
            }



            is ActionDecision.Completed -> {


                Log.d(
                    TAG,
                    decision.message
                )

                true
            }



            is ActionDecision.Failed -> {


                Log.e(
                    TAG,
                    decision.reason
                )

                false
            }
        }
    }
}