package com.dhritiman.aura.accessibility

data class AccessibilityNodeData(

    val text: String?,

    val contentDescription: String?,

    val className: String?,

    val resourceId: String?,

    val isClickable: Boolean,

    val isFocusable: Boolean,

    val isEditable: Boolean,

    val isScrollable: Boolean,

    val isEnabled: Boolean,

    val childCount: Int
)