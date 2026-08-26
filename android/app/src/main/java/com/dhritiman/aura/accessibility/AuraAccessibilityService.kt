package com.dhritiman.aura.accessibility

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AuraAccessibilityService :
    AccessibilityService() {

    companion object {

        private const val TAG =
            "AURA_ACCESSIBILITY"

        @Volatile
        var instance:
            AuraAccessibilityService? = null
            private set
    }

    /*
     * Controller responsible for inspecting
     * and interacting with the current UI.
     */
    private val accessibilityController =
        AccessibilityController()

    /*
     * Executor responsible for executing
     * high-level UI actions.
     */
    private lateinit var actionExecutor:
            UIActionExecutor

    private val screenObserver =ScreenObserver()

    private val agentEventSignal =
    com.dhritiman.aura.agent.AgentEventSignal()

    // --------------------------------------------------
    // SERVICE LIFECYCLE
    // --------------------------------------------------

    override fun onServiceConnected() {

        super.onServiceConnected()

        instance = this

        /*
         * Initialize the UI action executor
         * after the Accessibility Service has
         * been connected.
         */
        actionExecutor =
            UIActionExecutor(this)

        Log.d(
            TAG,
            "AURA Accessibility Service connected"
        )
    }

    override fun onAccessibilityEvent(
        event: AccessibilityEvent?
    ) {

        if (event == null) {
            return
        }

        if (
            event.eventType ==
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType ==
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        ) {

            agentEventSignal.signal()
        }

        Log.d(
            TAG,
            "Event received: " +
                    "type=${event.eventType}, " +
                    "package=${event.packageName}"
        )

        /*
         * When the active application/window
         * changes, inspect its accessibility tree.
         */
        if (
            event.eventType ==
            AccessibilityEvent
                .TYPE_WINDOW_STATE_CHANGED
        ) {

            accessibilityController
                .dumpCurrentScreen()
        }
        val state =
        observeCurrentScreen()

        Log.d(
            "AURA_OBSERVER",
            "Screen package: " +
                    state?.packageName
        )

        state?.elements?.forEach { element ->

            Log.d(
                "AURA_OBSERVER",
                "Element: " +
                        "text=${element.text}, " +
                        "description=${element.contentDescription}, " +
                        "clickable=${element.clickable}, " +
                        "editable=${element.editable}, " +
                        "scrollable=${element.scrollable}"
            )
        }
    }

    override fun onInterrupt() {

        Log.d(
            TAG,
            "Accessibility service interrupted"
        )
    }

    override fun onDestroy() {

        super.onDestroy()

        if (instance === this) {

            instance = null
        }

        Log.d(
            TAG,
            "AURA Accessibility Service destroyed"
        )
    }

    // --------------------------------------------------
    // ACCESSIBILITY HELPERS
    // --------------------------------------------------

    /**
     * Returns the root node of the currently
     * visible application.
     */
    fun waitForWindowChange(
        timeoutMillis: Long = 5000
    ): Boolean {

        agentEventSignal.reset()

        return agentEventSignal.await(
            timeoutMillis
        )
    }

    fun getRootNode():
            AccessibilityNodeInfo? {

        return rootInActiveWindow
    }

    // --------------------------------------------------
    // UI ACTION EXECUTION
    // --------------------------------------------------

    /**
     * Executes a UIAction through the
     * UIActionExecutor.
     *
     * This is the bridge that will eventually
     * allow the AURA agent to control Android.
     */
    fun executeAction(
        action: UIAction
    ): UIActionResult {

        /*
         * The service should normally already
         * be connected, but this check prevents
         * accidental access before initialization.
         */
        if (!::actionExecutor.isInitialized) {

            Log.e(
                TAG,
                "Action executor is not initialized"
            )

            return UIActionResult.Failed
        }

        Log.d(
            TAG,
            "Executing action: $action"
        )

        val result =
            actionExecutor.execute(action)

        Log.d(
            TAG,
            "Action result: $result"
        )

        return result
    }

    // --------------------------------------------------
    // TEMPORARY TEST METHODS
    // --------------------------------------------------

    /**
     * Temporary test method.
     *
     * Finds text on the current screen and
     * attempts to click it.
     *
     * We will remove this later when the
     * real TaskExecutor/AI agent is connected.
     */
    fun testFindAndClick(
        text: String
    ): UIActionResult {

        Log.d(
            TAG,
            "TEST: Find and click '$text'"
        )

        return executeAction(
            UIAction.ClickText(text)
        )
    }

    /**
     * Temporary test method.
     *
     * Finds the first editable field and
     * enters the supplied text.
     */
    fun testTypeText(
        text: String
    ): UIActionResult {

        Log.d(
            TAG,
            "TEST: Type text '$text'"
        )

        return executeAction(
            UIAction.TypeText(text)
        )
    }

    /**
     * Temporary test method for pressing
     * the Android Back action.
     */
    fun testPressBack():
            UIActionResult {

        Log.d(
            TAG,
            "TEST: Press Back"
        )

        return executeAction(
            UIAction.PressBack
        )
    }

    /**
     * Temporary test method for scrolling
     * forward.
     */
    fun testScrollForward():
            UIActionResult {

        Log.d(
            TAG,
            "TEST: Scroll Forward"
        )

        return executeAction(
            UIAction.ScrollForward
        )
    }

    /**
     * Temporary test method for scrolling
     * backward.
     */
    fun testScrollBackward():
            UIActionResult {

        Log.d(
            TAG,
            "TEST: Scroll Backward"
        )

        return executeAction(
            UIAction.ScrollBackward
        )
    }

    fun observeCurrentScreen(): ScreenState? {

    return screenObserver.observe(
        rootInActiveWindow
    )
    }
}