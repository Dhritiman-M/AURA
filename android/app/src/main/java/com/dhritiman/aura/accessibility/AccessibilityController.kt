package com.dhritiman.aura.accessibility

import android.graphics.Rect
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo

class AccessibilityController {

    companion object {

        private const val TAG =
            "AURA_UI"
    }

    /**
     * Returns the current root node.
     */
    fun getRoot():
            AccessibilityNodeInfo? {

        return AuraAccessibilityService
            .instance
            ?.getRootNode()
    }

    /**
     * Prints the current accessibility
     * tree to Logcat.
     */
    fun dumpCurrentScreen() {

        val root = getRoot()

        if (root == null) {

            Log.w(
                TAG,
                "No accessibility root available"
            )

            return
        }

        Log.d(
            TAG,
            "========== UI TREE =========="
        )

        dumpNode(
            root,
            0
        )

        Log.d(
            TAG,
            "============================="
        )
    }

    /**
     * Recursively prints every node.
     */
    private fun dumpNode(
        node: AccessibilityNodeInfo,
        depth: Int
    ) {

        val indent =
            "  ".repeat(depth)

        val text =
            node.text?.toString()

        val description =
            node.contentDescription
                ?.toString()

        val className =
            node.className?.toString()

        val resourceId =
            node.viewIdResourceName

        val bounds =
            Rect()

        node.getBoundsInScreen(
            bounds
        )

        Log.d(
            TAG,

            "$indent" +
                    "class=$className " +
                    "text=$text " +
                    "description=$description " +
                    "id=$resourceId " +
                    "clickable=${node.isClickable} " +
                    "editable=${node.isEditable} " +
                    "scrollable=${node.isScrollable} " +
                    "enabled=${node.isEnabled} " +
                    "bounds=$bounds"
        )

        for (
            index in 0 until node.childCount
        ) {

            val child =
                node.getChild(index)

            if (child != null) {

                dumpNode(
                    child,
                    depth + 1
                )

                child.recycle()
            }
        }
    }

    /**
     * Converts a node into our own
     * lightweight data representation.
     */
    fun inspectNode(
        node: AccessibilityNodeInfo
    ): AccessibilityNodeData {

        return AccessibilityNodeData(

            text =
                node.text?.toString(),

            contentDescription =
                node.contentDescription
                    ?.toString(),

            className =
                node.className
                    ?.toString(),

            resourceId =
                node.viewIdResourceName,

            isClickable =
                node.isClickable,

            isFocusable =
                node.isFocusable,

            isEditable =
                node.isEditable,

            isScrollable =
                node.isScrollable,

            isEnabled =
                node.isEnabled,

            childCount =
                node.childCount
        )
    }
    /**
 * Finds the first node containing the
 * requested text.
 */
fun findByText(
    text: String
): AccessibilityNodeInfo? {

    val root = getRoot()
        ?: return null

    val nodes =
        root.findAccessibilityNodeInfosByText(
            text
        )

    return nodes.firstOrNull()
}

/**
 * Finds the first node whose content
 * description matches the requested text.
 */
fun findByContentDescription(
    description: String
): AccessibilityNodeInfo? {

    val root = getRoot()
        ?: return null

    return findNodeByContentDescription(
        root,
        description
    )
}

/**
 * Recursively searches the accessibility
 * tree for a content description.
 */
private fun findNodeByContentDescription(
    node: AccessibilityNodeInfo,
    description: String
): AccessibilityNodeInfo? {

    val nodeDescription =
        node.contentDescription
            ?.toString()

    if (
        nodeDescription != null &&
        nodeDescription.contains(
            description,
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
            findNodeByContentDescription(
                child,
                description
            )

        if (result != null) {
            return result
        }

        child.recycle()
    }

    return null
}

/**
 * Clicks a node.
 */
fun click(
    node: AccessibilityNodeInfo
): UIActionResult {

    if (!node.isClickable) {

        /*
         * Some visible elements are not
         * themselves clickable. Their parent
         * may be clickable instead.
         */
        val clickableParent =
            findClickableParent(node)

        if (clickableParent != null) {

            val success =
                clickableParent.performAction(
                    AccessibilityNodeInfo
                        .ACTION_CLICK
                )

            clickableParent.recycle()

            return if (success) {
                UIActionResult.Success
            } else {
                UIActionResult.Failed
            }
        }

        return UIActionResult.NotClickable
    }

    val success =
        node.performAction(
            AccessibilityNodeInfo
                .ACTION_CLICK
        )

    return if (success) {

        UIActionResult.Success

    } else {

        UIActionResult.Failed
    }
}

/**
 * Searches upward through the node tree
 * for a clickable parent.
 */
private fun findClickableParent(
    node: AccessibilityNodeInfo
): AccessibilityNodeInfo? {

    var parent =
        node.parent

    while (parent != null) {

        if (parent.isClickable) {

            return parent
        }

        val next =
            parent.parent

        parent.recycle()

        parent = next
    }

    return null
}

/**
 * Sets text inside an editable node.
 */
fun typeText(
    node: AccessibilityNodeInfo,
    text: String
): UIActionResult {

    if (!node.isEditable) {

        return UIActionResult.NotEditable
    }

    val arguments =
        android.os.Bundle()

    arguments.putCharSequence(
        AccessibilityNodeInfo
            .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
        text
    )

    val success =
        node.performAction(
            AccessibilityNodeInfo
                .ACTION_SET_TEXT,
            arguments
        )

    return if (success) {

        UIActionResult.Success

    } else {

        UIActionResult.Failed
    }
}
}