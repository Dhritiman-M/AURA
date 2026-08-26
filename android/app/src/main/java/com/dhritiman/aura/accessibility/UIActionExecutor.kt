package com.dhritiman.aura.accessibility

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo

class UIActionExecutor(
    private val service: AccessibilityService
) {

    companion object {
        private const val TAG = "AURA_ACTION"
    }

    private val controller =
        AccessibilityController()

    private val resolver =
    UIElementResolver()

    fun execute(
        action: UIAction
    ): UIActionResult {

        Log.d(
            TAG,
            "Executing UI action: $action"
        )

        return when (action) {

            is UIAction.FindText -> {

                val node =
                    controller.findByText(
                        action.text
                    )

                if (node != null) {

                    node.recycle()

                    UIActionResult.Success

                } else {

                    UIActionResult.NotFound
                }
            }

            is UIAction.ClickText -> {

                val root =
                    controller.getRoot()
                        ?: return UIActionResult.NotFound

                val node =
                    resolver.find(
                        root,
                        action.text
                    )

                if (node == null) {

                    Log.d(
                        TAG,
                        "UI element not found: " +
                                action.text
                    )

                    root.recycle()

                    return UIActionResult.NotFound
                }

                Log.d(
                    TAG,
                    "UI element resolved: " +
                            action.text
                )

                val result =
                    controller.click(node)

                node.recycle()
                root.recycle()

                result
            }

            is UIAction.ClickDescription -> {

                val node =
                    controller.findByContentDescription(
                        action.description
                    )

                if (node == null) {

                    Log.d(
                        TAG,
                        "Description not found: " +
                                action.description
                    )

                    return UIActionResult.NotFound
                }

                val result =
                    controller.click(node)

                node.recycle()

                result
            }

            is UIAction.TypeText -> {

                val root =
                    controller.getRoot()
                        ?: return UIActionResult.NotFound

                val editable =
                    findEditableNode(root)

                if (editable == null) {

                    return UIActionResult.NotFound
                }

                val result =
                    controller.typeText(
                        editable,
                        action.text
                    )

                editable.recycle()

                result
            }

            UIAction.PressBack -> {

                val success =
                    service.performGlobalAction(
                        AccessibilityService
                            .GLOBAL_ACTION_BACK
                    )

                if (success) {

                    UIActionResult.Success

                } else {

                    UIActionResult.Failed
                }
            }

            UIAction.ScrollDown -> {

                performScroll(
                    AccessibilityNodeInfo
                        .ACTION_SCROLL_FORWARD
                )
            }

            UIAction.ScrollForward -> {

                performScroll(
                    AccessibilityNodeInfo
                        .ACTION_SCROLL_FORWARD
                )
            }

            UIAction.ScrollBackward -> {

                performScroll(
                    AccessibilityNodeInfo
                        .ACTION_SCROLL_BACKWARD
                )
            }
        }
    }

    private fun performScroll(
        action: Int
    ): UIActionResult {

        val root =
            controller.getRoot()
                ?: return UIActionResult.NotFound

        val scrollable =
            findScrollableNode(root)

            if (scrollable == null) {

                Log.d(
                    TAG,
                    "No scrollable node found"
                )

                return UIActionResult.NotFound
            }

            Log.d(
                TAG,
                "Scrollable node found: " +
                        "class=${scrollable.className}, " +
                        "text=${scrollable.text}, " +
                        "description=${scrollable.contentDescription}"
            )

            val success =
                scrollable.performAction(action)

            scrollable.recycle()

            return if (success) {

                UIActionResult.Success

            } else {

                UIActionResult.Failed
            }
    }

    private fun findEditableNode(
        node: AccessibilityNodeInfo
    ): AccessibilityNodeInfo? {

        if (
            node.isEditable &&
            node.isEnabled
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
                findEditableNode(child)

            if (result != null) {

                return result
            }

            child.recycle()
        }

        return null
    }

    private fun findScrollableNode(
        node: AccessibilityNodeInfo
    ): AccessibilityNodeInfo? {

        if (
            node.isScrollable &&
            node.isEnabled
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
                findScrollableNode(child)

            if (result != null) {

                return result
            }

            child.recycle()
        }

        return null
    }
}