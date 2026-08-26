package com.dhritiman.aura.agent

data class Task(
    val command: String,
    val type: TaskType,
    val appName: String? = null
)

enum class TaskType {
    OPEN_SETTINGS,
    OPEN_APP,
    UNKNOWN
}