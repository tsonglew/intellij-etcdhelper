package com.github.tsonglew.etcdhelper.common

import java.util.concurrent.*
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy

object ThreadPoolManager {
    private val EXECUTOR = ThreadPoolExecutor(
        20,
        100,
        10,
        TimeUnit.SECONDS,
        LinkedBlockingDeque(),
        Executors.defaultThreadFactory(),
        AbortPolicy()
    )

    val executor: ThreadPoolExecutor
        get() = EXECUTOR

    fun submit(task: Runnable?): Future<*>? = task?.let { EXECUTOR.submit(it) }

    fun execute(command: Runnable?) {
        command?.let {
            EXECUTOR.execute(command)
        }
    }
}
