package com.dhritiman.aura.accessibility

import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo

class ScreenObserver {

    companion object {
        private const val TAG = "AURA_OBSERVER"
    }

    fun observe(
        root: AccessibilityNodeInfo?
    ): ScreenState? {

        if (root == null) {
            Log.d(
                TAG,
                "No active accessibility root"
            )

            return null
        }

        val elements =
            mutableListOf<ScreenElement>()

        collectElements(
            root,
            elements
        )

        val state =
            ScreenState(
                packageName =
                    root.packageName?.toString(),

                className =
                    root.className?.toString(),

                elements =
                    elements
            )

        Log.d(
            TAG,
            "Observed screen: " +
                    "${state.packageName}, " +
                    "${elements.size} elements"
        )

        return state
    }

    private fun collectElements(
        node: AccessibilityNodeInfo,
        elements: MutableList<ScreenElement>
    ) {

        /*
         * Only store useful UI nodes.
         *
         * This prevents the observation from
         * becoming unnecessarily large.
         */
        val hasUsefulInformation =
            !node.text.isNullOrBlank() ||
            !node.contentDescription.isNullOrBlank() ||
            node.isClickable ||
            node.isEditable ||
            node.isScrollable

        if (hasUsefulInformation) {

            elements.add(
                ScreenElement(

                    text =
                        node.text?.toString(),

                    contentDescription =
                        node.contentDescription
                            ?.toString(),

                    className =
                        node.className
                            ?.toString(),

                    viewId =
                        node.viewIdResourceName,

                    clickable =
                        node.isClickable,

                    editable =
                        node.isEditable,

                    scrollable =
                        node.isScrollable,

                    enabled =
                        node.isEnabled
                )
            )
        }

        for (
            index in 0 until node.childCount
        ) {

            val child =
                node.getChild(index)
                    ?: continue

            collectElements(
                child,
                elements
            )

            child.recycle()
        }
    }
}