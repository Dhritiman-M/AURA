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

            is UIAction.WaitForText -> {
                waitForText(
                    text = action.text,
                    timeoutMs = action.timeoutMs
                )
            }

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

                    val match =
                        resolver.findBestTextMatch(
                            root,
                            action.text
                        )

                    if (match == null) {

                        Log.d(
                            TAG,
                            "Could not resolve: " +
                                    action.text
                        )

                        return UIActionResult.NotFound
                    }

                    Log.d(
                        TAG,
                        "Resolved '${action.text}' " +
                                "using ${match.matchType} " +
                                "score=${match.score}"
                    )

                    val result =
                        controller.click(
                            match.node
                        )

                    match.node.recycle()

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

    private fun waitForText(
            text: String,
            timeoutMs: Long
        ): UIActionResult {

            val startTime =
                System.currentTimeMillis()

            Log.d(
                TAG,
                "Waiting for text: $text"
            )

            while (
                System.currentTimeMillis() - startTime
                < timeoutMs
            ) {

                val node =
                    controller.findByText(text)

                if (node != null) {

                    node.recycle()

                    Log.d(
                        TAG,
                        "Found text: $text"
                    )

                    return UIActionResult.Success
                }

                Thread.sleep(200)
            }

            Log.d(
                TAG,
                "Timed out waiting for: $text"
            )

            return UIActionResult.NotFound
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