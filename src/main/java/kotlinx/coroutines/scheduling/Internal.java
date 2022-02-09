package kotlinx.coroutines.scheduling;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

@SuppressWarnings("all")
public final class Internal {
    public static Integer corePoolSize = TasksKt.CORE_POOL_SIZE;
    public static Integer maxPoolSize = TasksKt.MAX_POOL_SIZE;

    @NotNull
    @Contract("_ -> new")
    public static Executor createCoroutineScheduler(Integer corePoolSize, Integer maxPoolSize, Long idleWorkerKeepAliveNs, String threadName) {
        new SchedulerCoroutineDispatcher();
        //ThreadFactory
        return new CoroutineScheduler(corePoolSize, maxPoolSize, idleWorkerKeepAliveNs, threadName);
    }
}

