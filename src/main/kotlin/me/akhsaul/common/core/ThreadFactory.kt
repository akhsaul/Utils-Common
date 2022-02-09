package me.akhsaul.common.core

import kotlinx.coroutines.scheduling.Internal
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ThreadFactory(
    corePoolSize: Int,
    maxPoolSize: Int,
    aliveTime: Long,
    unit: TimeUnit,
    private val threadName: String = "Global-Thread",
    private val threadPriority: Int = 10,
    queue: BlockingQueue<Runnable>? = null,
) : Executor {
    private var exe: Lazy<Executor> = lazy(LazyThreadSafetyMode.PUBLICATION) {
        if (queue != null){
            ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                aliveTime,
                unit,
                queue
            )
        } else {
            Internal.createCoroutineScheduler(
                corePoolSize,
                maxPoolSize,
                TimeUnit.NANOSECONDS.convert(aliveTime, unit),
                threadName
            )
        }
    }

    override fun execute(command: Runnable) {
        exe.value.execute {
            Thread.currentThread().apply {
                name = threadName
                priority = threadPriority
            }
            command.run()
        }
    }
}