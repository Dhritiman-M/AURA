package com.dhritiman.aura.agent

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class AgentEventSignal {

    @Volatile
    private var latch: CountDownLatch =
        CountDownLatch(0)

    fun reset() {
        latch = CountDownLatch(1)
    }

    fun signal() {
        latch.countDown()
    }

    fun await(timeoutMillis: Long): Boolean {
        return latch.await(
            timeoutMillis,
            TimeUnit.MILLISECONDS
        )
    }
}