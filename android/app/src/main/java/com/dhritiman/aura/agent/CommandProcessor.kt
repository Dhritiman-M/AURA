package com.dhritiman.aura.agent

class CommandProcessor {

    fun process(command: String): Task {

        val normalizedCommand =
            command
                .trim()
                .lowercase()

        return when {

            normalizedCommand == "open settings" -> {

                Task(
                    command = command,
                    type = TaskType.OPEN_SETTINGS
                )
            }

            normalizedCommand.startsWith("open ") -> {

                val appName =
                    normalizedCommand
                        .removePrefix("open ")
                        .trim()

                Task(
                    command = command,
                    type = TaskType.OPEN_APP,
                    appName = appName
                )
            }

            else -> {

                Task(
                    command = command,
                    type = TaskType.UNKNOWN
                )
            }
        }
    }
}