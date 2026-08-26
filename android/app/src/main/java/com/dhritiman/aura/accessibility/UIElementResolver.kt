package com.dhritiman.aura.accessibility

import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo

class UIElementResolver {

    companion object {

        private const val TAG =
            "AURA_RESOLVER"
    }

    /**
     * Finds the best matching UI element
     * using several strategies.
     */
    fun find(
        root: AccessibilityNodeInfo?,
        target: String
    ): AccessibilityNodeInfo? {

        if (
            root == null ||
            target.isBlank()
        ) {
            return null
        }

        val normalizedTarget =
            target.trim()

        /*
         * ------------------------------------
         * 1. Exact text match
         * ------------------------------------
         */
        val exactText =
            findExactText(
                root,
                normalizedTarget
            )

        if (exactText != null) {

            Log.d(
                TAG,
                "Exact text match: $target"
            )

            return exactText
        }

        /*
         * ------------------------------------
         * 2. Exact content description
         * ------------------------------------
         */
        val description =
            findExactDescription(
                root,
                normalizedTarget
            )

        if (description != null) {

            Log.d(
                TAG,
                "Content description match: $target"
            )

            return description
        }

        /*
         * ------------------------------------
         * 3. Case-insensitive text
         * ------------------------------------
         */
        val caseInsensitive =
            findCaseInsensitiveText(
                root,
                normalizedTarget
            )

        if (caseInsensitive != null) {

            Log.d(
                TAG,
                "Case-insensitive match: $target"
            )

            return caseInsensitive
        }

        /*
         * ------------------------------------
         * 4. Partial text
         * ------------------------------------
         */
        val partial =
            findPartialText(
                root,
                normalizedTarget
            )

        if (partial != null) {

            Log.d(
                TAG,
                "Partial text match: $target"
            )

            return partial
        }

        Log.d(
            TAG,
            "No UI element found for: $target"
        )

        return null
    }

    private fun findExactText(
        node: AccessibilityNodeInfo,
        target: String
    ): AccessibilityNodeInfo? {

        val text =
            node.text?.toString()

        if (
            text != null &&
            text == target
        ) {

            return node
        }

        for (
            index in 0 until node.childCount
        ) {

            val child =
                node.getChild(index)
                    ?: continue

            val result =
                findExactText(
                    child,
                    target
                )

            if (result != null) {

                return result
            }

            child.recycle()
        }

        return null
    }

    private fun findExactDescription(
        node: AccessibilityNodeInfo,
        target: String
    ): AccessibilityNodeInfo? {

        val description =
            node.contentDescription
                ?.toString()

        if (
            description != null &&
            description.equals(
                target,
                ignoreCase = true
            )
        ) {

            return node
        }

        for (
            index in 0 until node.childCount
        ) {

            val child =
                node.getChild(index)
                    ?: continue

            val result =
                findExactDescription(
                    child,
                    target
                )

            if (result != null) {

                return result
            }

            child.recycle()
        }

        return null
    }

    private fun findCaseInsensitiveText(
        node: AccessibilityNodeInfo,
        target: String
    ): AccessibilityNodeInfo? {

        val text =
            node.text?.toString()

        if (
            text != null &&
            text.equals(
                target,
                ignoreCase = true
            )
        ) {

            return node
        }

        for (
            index in 0 until node.childCount
        ) {

            val child =
                node.getChild(index)
                    ?: continue

            val result =
                findCaseInsensitiveText(
                    child,
                    target
                )

            if (result != null) {

                return result
            }

            child.recycle()
        }

        return null
    }

    private fun findPartialText(
        node: AccessibilityNodeInfo,
        target: String
    ): AccessibilityNodeInfo? {

        val text =
            node.text?.toString()

        if (
            text != null &&
            text.contains(
                target,
                ignoreCase = true
            )
        ) {

            return node
        }

        for (
            index in 0 until node.childCount
        ) {

            val child =
                node.getChild(index)
                    ?: continue

            val result =
                findPartialText(
                    child,
                    target
                )

            if (result != null) {

                return result
            }

            child.recycle()
        }

        return null
    }

    private fun findClickableParent(
    node: AccessibilityNodeInfo
): AccessibilityNodeInfo? {

    var current =
        node.parent

    while (current != null) {

        if (
            current.isClickable &&
            current.isEnabled
        ) {

            return current
        }

        val parent =
            current.parent

        current.recycle()

        current = parent
    }

    return null
    }
}