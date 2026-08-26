package com.dhritiman.aura.accessibility

data class ScreenElement(
    val text: String?,
    val contentDescription: String?,
    val className: String?,
    val viewId: String?,
    val clickable: Boolean,
    val editable: Boolean,
    val scrollable: Boolean,
    val enabled: Boolean
)

data class ScreenState(
    val packageName: String?,
    val className: String?,
    val elements: List<ScreenElement>
)