package com.example.android_aop.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 线程工具类
 */
class AppExecutors private constructor(
    /**
     * 单线程池
     */
    private val mSingleIO: ExecutorService = Executors.newSingleThreadExecutor(),
    /**
     * 多线程池
     */
    private var mPoolIO: ExecutorService = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors()
    ),
    /**
     * 主线程
     */
    private val mMainThread: Executor =
        MainThreadExecutor()
) {

    /**
     * 更新多线程池
     * @param nThreads 线程池线程的数量
     * @return
     */
    fun updatePoolIO(nThreads: Int): AppExecutors {
        mPoolIO = Executors.newFixedThreadPool(nThreads)
        return this
    }

    fun singleIO(): ExecutorService {
        return mSingleIO
    }

    fun poolIO(): ExecutorService {
        return mPoolIO
    }

    fun mainThread(): Executor {
        return mMainThread
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler =
            Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

    companion object {
        private var sInstance: AppExecutors? = null

        /**
         * 获取线程管理实例
         *
         * @return
         */
        fun get(): AppExecutors? {
            if (sInstance == null) {
                synchronized(AppExecutors::class.java) {
                    if (sInstance == null) {
                        sInstance = AppExecutors()
                    }
                }
            }
            return sInstance
        }
    }

}