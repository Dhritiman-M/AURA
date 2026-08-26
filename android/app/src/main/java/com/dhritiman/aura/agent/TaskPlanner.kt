package com.dhritiman.aura.agent

import com.dhritiman.aura.accessibility.UIAction

class TaskPlanner {

    fun createPlan(command: String): TaskPlan {

        val normalized =
            command
                .trim()
                .lowercase()

        val steps =
            mutableListOf<PlanStep>()

        /*
         * Example:
         *
         * "open whatsapp"
         *
         * becomes:
         *
         * OpenApp("whatsapp")
         */
        if (normalized.startsWith("open ")) {

            val remaining =
                normalized
                    .removePrefix("open ")
                    .trim()

            /*
             * Check whether the command
             * contains multiple instructions.
             */
            val andIndex =
                remaining.indexOf(" and ")

            if (andIndex == -1) {

                steps.add(
                    PlanStep.OpenApp(
                        remaining
                    )
                )

            } else {

                val appName =
                    remaining
                        .substring(
                            0,
                            andIndex
                        )
                        .trim()

                steps.add(
                    PlanStep.OpenApp(
                        appName
                    )
                )

                val nextCommand =
                    remaining
                        .substring(
                            andIndex + 5
                        )
                        .trim()

                parseUICommand(
                    nextCommand,
                    steps
                )
            }
        }

        return TaskPlan(
            steps = steps
        )
    }

    private fun parseUICommand(
        command: String,
        steps: MutableList<PlanStep>
    ) {

        /*
         * Search for something.
         *
         * Example:
         *
         * "search for john"
         */
        if (
            command.startsWith(
                "search for "
            )
        ) {

            val searchText =
                command
                    .removePrefix(
                        "search for "
                    )
                    .trim()

            steps.add(
                PlanStep.UI(
                    UIAction.WaitForText(
                        "Search"
                    )
                )
            )

            steps.add(
                PlanStep.UI(
                    UIAction.ClickText(
                        "Search"
                    )
                )
            )

            steps.add(
                PlanStep.UI(
                    UIAction.TypeText(
                        searchText
                    )
                )
            )
            return
        }

        /*
         * Click something.
         *
         * Example:
         *
         * "click settings"
         */
        if (
            command.startsWith(
                "click "
            )
        ) {

            val text =
                command
                    .removePrefix(
                        "click "
                    )
                    .trim()

            steps.add(
                PlanStep.UI(
                    UIAction.WaitForText(
                        text
                    )
                )
            )

            steps.add(
                PlanStep.UI(
                    UIAction.ClickText(
                        text
                    )
                )
            )

            return
        }

        /*
         * Type something.
         *
         * Example:
         *
         * "type hello"
         */
        if (
            command.startsWith(
                "type "
            )
        ) {

            val text =
                command
                    .removePrefix(
                        "type "
                    )
                    .trim()

            steps.add(
                PlanStep.UI(
                    UIAction.TypeText(
                        text
                    )
                )
            )

            return
        }

        /*
         * Scroll down.
         */
        if (
            command == "scroll down"
        ) {

            steps.add(
                PlanStep.UI(
                    UIAction.ScrollDown
                )
            )

            return
        }

        /*
         * Scroll forward.
         */
        if (
            command == "scroll forward"
        ) {

            steps.add(
                PlanStep.UI(
                    UIAction.ScrollForward
                )
            )

            return
        }

        /*
         * Go back.
         */
        if (
            command == "go back" ||
            command == "back"
        ) {

            steps.add(
                PlanStep.UI(
                    UIAction.PressBack
                )
            )
        }
    }
}