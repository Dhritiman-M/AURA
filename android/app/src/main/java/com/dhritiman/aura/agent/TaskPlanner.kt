package com.dhritiman.aura.agent

import com.dhritiman.aura.accessibility.UIAction

class TaskPlanner {

    fun createPlan(
        command: String
    ): TaskPlan {

        val originalCommand =
            command.trim()

        val normalizedCommand =
            originalCommand.lowercase()

        if (normalizedCommand.isBlank()) {

            return TaskPlan(
                steps = emptyList()
            )
        }

        /*
         * =====================================
         * OPEN APP + PRESS BACK
         * =====================================
         */
        if (
            normalizedCommand.contains(
                " and press back"
            )
        ) {

            val appName =
                originalCommand
                    .substringBefore(
                        " and ",
                        ""
                    )
                    .trim()
                    .removePrefixIgnoreCase(
                        "open "
                    )
                    .removePrefixIgnoreCase(
                        "launch "
                    )
                    .removePrefixIgnoreCase(
                        "start "
                    )
                    .trim()

            if (appName.isNotBlank()) {

                return TaskPlan(
                    steps = listOf(

                        PlanStep.OpenApp(
                            appName = appName
                        ),

                        PlanStep.UI(
                            action =
                                UIAction.PressBack
                        )
                    )
                )
            }
        }

        /*
         * =====================================
         * PRESS BACK
         * =====================================
         */
        if (
            normalizedCommand == "back" ||
            normalizedCommand == "go back" ||
            normalizedCommand == "press back"
        ) {

            return TaskPlan(
                steps = listOf(

                    PlanStep.UI(
                        action =
                            UIAction.PressBack
                    )
                )
            )
        }

        /*
         * =====================================
         * SCROLL DOWN
         * =====================================
         */
        if (
            normalizedCommand == "scroll" ||
            normalizedCommand == "scroll down" ||
            normalizedCommand == "scroll forward"
        ) {

            return TaskPlan(
                steps = listOf(

                    PlanStep.UI(
                        action =
                            UIAction.ScrollForward
                    )
                )
            )
        }

        /*
         * =====================================
         * SCROLL UP
         * =====================================
         */
        if (
            normalizedCommand == "scroll up" ||
            normalizedCommand == "scroll backward"
        ) {

            return TaskPlan(
                steps = listOf(

                    PlanStep.UI(
                        action =
                            UIAction.ScrollBackward
                    )
                )
            )
        }

        /*
         * =====================================
         * CLICK TEXT
         * =====================================
         *
         * Example:
         *
         * click Search
         */
        if (
            normalizedCommand.startsWith(
                "click "
            )
        ) {

            val target =
                originalCommand
                    .substringAfter(
                        " ",
                        ""
                    )
                    .trim()

            if (target.isNotBlank()) {

                return TaskPlan(
                    steps = listOf(

                        PlanStep.UI(
                            action =
                                UIAction.ClickText(
                                    target
                                )
                        )
                    )
                )
            }
        }

        /*
         * =====================================
         * TYPE TEXT
         * =====================================
         *
         * Example:
         *
         * type hello world
         */
        if (
            normalizedCommand.startsWith(
                "type "
            )
        ) {

            val text =
                originalCommand
                    .substringAfter(
                        " ",
                        ""
                    )
                    .trim()

            if (text.isNotBlank()) {

                return TaskPlan(
                    steps = listOf(

                        PlanStep.UI(
                            action =
                                UIAction.TypeText(
                                    text
                                )
                        )
                    )
                )
            }
        }

        /*
         * =====================================
         * CLICK DESCRIPTION
         * =====================================
         *
         * Example:
         *
         * click description menu
         */
        if (
            normalizedCommand.startsWith(
                "click description "
            )
        ) {

            val description =
                originalCommand
                    .substringAfter(
                        "click description ",
                        ""
                    )
                    .trim()

            if (
                description.isNotBlank()
            ) {

                return TaskPlan(
                    steps = listOf(

                        PlanStep.UI(
                            action =
                                UIAction.ClickDescription(
                                    description
                                )
                        )
                    )
                )
            }
        }

        /*
         * =====================================
         * FIND TEXT
         * =====================================
         *
         * Example:
         *
         * find Search
         */
        if (
            normalizedCommand.startsWith(
                "find "
            )
        ) {

            val text =
                originalCommand
                    .substringAfter(
                        " ",
                        ""
                    )
                    .trim()

            if (text.isNotBlank()) {

                return TaskPlan(
                    steps = listOf(

                        PlanStep.UI(
                            action =
                                UIAction.FindText(
                                    text
                                )
                        )
                    )
                )
            }
        }

        /*
         * =====================================
         * OPEN APP
         * =====================================
         */
        if (
            normalizedCommand.startsWith(
                "open "
            )
        ) {

            val appName =
                originalCommand
                    .substringAfter(
                        " ",
                        ""
                    )
                    .trim()

            if (appName.isNotBlank()) {

                return TaskPlan(
                    steps = listOf(

                        PlanStep.OpenApp(
                            appName = appName
                        )
                    )
                )
            }
        }

        /*
         * =====================================
         * LAUNCH APP
         * =====================================
         */
        if (
            normalizedCommand.startsWith(
                "launch "
            )
        ) {

            val appName =
                originalCommand
                    .substringAfter(
                        " ",
                        ""
                    )
                    .trim()

            if (appName.isNotBlank()) {

                return TaskPlan(
                    steps = listOf(

                        PlanStep.OpenApp(
                            appName = appName
                        )
                    )
                )
            }
        }

        /*
         * =====================================
         * START APP
         * =====================================
         */
        if (
            normalizedCommand.startsWith(
                "start "
            )
        ) {

            val appName =
                originalCommand
                    .substringAfter(
                        " ",
                        ""
                    )
                    .trim()

            if (appName.isNotBlank()) {

                return TaskPlan(
                    steps = listOf(

                        PlanStep.OpenApp(
                            appName = appName
                        )
                    )
                )
            }
        }

        /*
         * =====================================
         * UNKNOWN COMMAND
         * =====================================
         */
        return TaskPlan(
            steps = emptyList()
        )
    }

    private fun String.removePrefixIgnoreCase(
        prefix: String
    ): String {

        return if (
            startsWith(
                prefix,
                ignoreCase = true
            )
        ) {

            substring(
                prefix.length
            )

        } else {

            this
        }
    }
}