package com.dhritiman.aura.accessibility

import android.view.accessibility.AccessibilityNodeInfo

enum class MatchType {
    EXACT_TEXT,
    EXACT_DESCRIPTION,
    VIEW_ID,
    NORMALIZED_TEXT,
    CONTAINS_TEXT
}

data class UIElementMatch(
    val node: AccessibilityNodeInfo,
    val matchType: MatchType,
    val score: Int
)