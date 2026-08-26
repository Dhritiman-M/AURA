package com.dhritiman.aura.accessibility

import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo

class UIElementResolver {

    companion object {

        private const val TAG =
            "AURA_RESOLVER"
    }

    fun findBestTextMatch(
        root: AccessibilityNodeInfo?,
        requestedText: String
    ): UIElementMatch? {

        if (root == null) {
            return null
        }

        val query =
            requestedText
                .trim()

        if (query.isEmpty()) {
            return null
        }

        val matches =
            mutableListOf<UIElementMatch>()

        collectMatches(
            root,
            query,
            matches
        )

        if (matches.isEmpty()) {

            Log.d(
                TAG,
                "No match found for '$query'"
            )

            return null
        }

        val best =
            matches.maxByOrNull {
                it.score
            }

        if (best == null) {
            return null
        }

        Log.d(
            TAG,
            "Best match for '$query': " +
                    "type=${best.matchType}, " +
                    "score=${best.score}"
        )

        return best
    }

    private fun collectMatches(
        node: AccessibilityNodeInfo,
        query: String,
        matches: MutableList<UIElementMatch>
    ) {
            val text =
                node.text
                    ?.toString()
                    ?.trim()

            val description =
                node.contentDescription
                    ?.toString()
                    ?.trim()

            if (
                !text.isNullOrEmpty() &&
                text.equals(
                    query,
                    ignoreCase = true
                )
            ) {

                matches.add(
                    UIElementMatch(
                        node = AccessibilityNodeInfo.obtain(node),
                        matchType =
                            MatchType.EXACT_TEXT,
                        score = 100
                    )
                )
            }

            if (
                !description.isNullOrEmpty() &&
                description.equals(
                    query,
                    ignoreCase = true
                )
            ) {

                matches.add(
                    UIElementMatch(
                        node = AccessibilityNodeInfo.obtain(node),
                        matchType =
                            MatchType.EXACT_DESCRIPTION,
                        score = 95
                    )
                )
            }

            if (
                !text.isNullOrEmpty() &&
                normalize(text) ==
                normalize(query)
            ) {

                matches.add(
                    UIElementMatch(
                        node = AccessibilityNodeInfo.obtain(node),
                        matchType =
                            MatchType.NORMALIZED_TEXT,
                        score = 90
                    )
                )
            }

            if (
                !text.isNullOrEmpty() &&
                text.contains(
                    query,
                    ignoreCase = true
                )
            ) {

                matches.add(
                    UIElementMatch(
                        node = AccessibilityNodeInfo.obtain(node),
                        matchType =
                            MatchType.CONTAINS_TEXT,
                        score = 40
                    )
                )
            }

            for (
                index in 0 until node.childCount
            ) {

                val child =
                    node.getChild(index)
                        ?: continue

                collectMatches(
                    child,
                    query,
                    matches
                )

                child.recycle()
            }
    }   

    private fun normalize(
        value: String
    ): String {

        return value
            .trim()
            .replace(
                Regex("\\s+"),
                " "
            )
            .lowercase()
    }
}