package me.akhsaul.common.core

import kotlinx.coroutines.*
import kotlinx.coroutines.scheduling.Internal
import java.util.concurrent.Executor
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

object Core {
    private val corePool = Internal.corePoolSize
    private val maxPool = Internal.maxPoolSize
    private var immediateScope: CoroutineScope? = null
    private var networkScope: CoroutineScope? = null
    private var normalScope: CoroutineScope? = null

    @JvmStatic
    fun makeExceptionHandler(nameCoroutine: String, ignoreError: Boolean): CoroutineContext {
        return CoroutineName(nameCoroutine).plus(
            CoroutineExceptionHandler { coroutineContext, throwable ->
                println("CoroutineExceptionHandler")
                //CoroutineException(coroutineContext, throwable.message).printStackTrace(ignoreError)
            }
        )
    }

    init {
        // Set Debug = ON or OFF
        System.setProperty(DEBUG_PROPERTY_NAME, DEBUG_PROPERTY_VALUE_ON)
    }

    /**
     * create ThreadPool like Executors.newCachedThreadPool(),
     *
     * this Thread has 9 priority level (HIGH priority)
     *
     * shutdown immediately idle thread
     * */
    fun forImmediateTask(ignoreError: Boolean): CoroutineScope {
        if (immediateScope == null) {
            synchronized(Core.javaClass) {
                immediateScope = CoroutineScope(
                    ThreadFactory(
                        corePool, maxPool,
                        60L, TimeUnit.SECONDS,
                        "Immediate-Thread", 9
                    ).asCoroutineDispatcher().plus(
                        makeExceptionHandler("immediate_coroutine", ignoreError)
                    )
                )
            }
        }
        return requireNotNull(immediateScope)
    }

    /**
     * create ThreadPool like Executors.newFixedThreadPool(),
     *
     * this Thread has 1 ~ 4 priority level (low priority)
     *
     * idle Thread will alive for 5 second
     * */
    fun forNetworkTask(ignoreError: Boolean): CoroutineScope {
        if (networkScope == null) {
            synchronized(Core.javaClass) {
                networkScope = CoroutineScope(
                    ThreadFactory(
                        8, 8,
                        60L, TimeUnit.SECONDS,
                        "Network-Thread", Random.nextInt(Thread.MIN_PRIORITY, 5)
                    ).asCoroutineDispatcher().plus(
                        makeExceptionHandler("network_coroutine", ignoreError)
                    )
                )
            }
        }
        return requireNotNull(networkScope)
    }

    /**
     * Dispatchers.IO use 5 for priority = Normal Priority
     * */
    fun forIOTask(ignoreError: Boolean): CoroutineScope {
        return CoroutineScope(Dispatchers.IO.plus(
            makeExceptionHandler("io_coroutine", ignoreError)
        ))
    }

    /**
     * Main Thread should be responsive
     *
     * Only use this if you use for console App
     *
     * Main Thread will be forced to use 10 for priority (MAX priority)
     * */
    fun forMainTask(ignoreError: Boolean): CoroutineScope {
        return CoroutineScope(
            Executor {
                // change some properties
                Thread.currentThread().apply {
                    priority = Thread.MAX_PRIORITY
                    name = "Main-Thread-@$id-#$priority"
                }
                // then run it!
                it.run()
            }.asCoroutineDispatcher().plus(
                makeExceptionHandler("main_coroutine", ignoreError)
            )
        )
    }

    /**
     * Only use this if you use javaFx or Android or Swing
     *
     * UI-Thread is same like Main-Thread
     * */
    fun forUITask(ignoreError: Boolean): CoroutineScope {
        return CoroutineScope(
            Dispatchers.Main.plus(
                makeExceptionHandler("ui_coroutine", ignoreError)
            )
        )
    }

    fun forNormalTask(ignoreError: Boolean): CoroutineScope {
        if (normalScope == null) {
            synchronized(Core.javaClass) {
                normalScope = CoroutineScope(
                    ThreadFactory(
                        0, Integer.MAX_VALUE,
                        60L, TimeUnit.SECONDS,
                        "Normal-Thread", 5,
                        SynchronousQueue()
                    ).asCoroutineDispatcher().plus(
                        makeExceptionHandler("normal_coroutine", ignoreError)
                    )
                )
            }
        }
        return requireNotNull(normalScope)
    }
}