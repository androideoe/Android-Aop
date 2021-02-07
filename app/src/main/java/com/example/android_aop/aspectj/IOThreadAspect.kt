package com.example.android_aop.aspectj

import android.os.Looper
import android.util.Log
import com.example.android_aop.annotation.AopIOThread
import com.example.android_aop.util.AppExecutors
import com.example.android_aop.util.ThreadType
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import java.util.concurrent.Callable

@Aspect
class IOThreadAspect {

    /**
     * 定义切点，标记切点为所有被@AopMethodTime
     */
    @Pointcut("execution(@com.example.android_aop.annotation.AopIOThread * *(..))")
    fun methodRunInIO() {
    }


    /**
     * 定义一个切面方法，包裹切点方法
     */
    @Around("methodRunInIO()")
    @Throws(Throwable::class)
    fun aroundMethodTimeJoinPoint(joinPoint: ProceedingJoinPoint) {
        // 已经子线程直接运行当前方法
        if (Looper.getMainLooper() != Looper.myLooper()) {
            Log.d("ddup", "aleady in IO thread...");
            joinPoint.proceed()
        } else {
            Log.d("ddup", "【当前线程】= " + Thread.currentThread().name);
            // 取出方法的注解
            val methodSignature = joinPoint.signature as MethodSignature
            val method = methodSignature.method
            if (!method.isAnnotationPresent(AopIOThread::class.java)) {
                return
            }

            val runInIO = method.getAnnotation(AopIOThread::class.java)
            when (runInIO?.value) {
                ThreadType.Single,
                ThreadType.Disk -> AppExecutors.get()!!.singleIO().submit(Callable<Any?> {
                    try {
                        joinPoint.proceed()
                    } catch (e: Exception) {
                        Log.e("ddup", "run in singleIO error" + e.message)
                    }
                }).get()
                ThreadType.Fixed,
                ThreadType.Network -> AppExecutors.get()!!.poolIO().submit(Callable<Any?> {
                    try {
                        joinPoint.proceed()
                    } catch (e: Exception) {
                        Log.e("ddup", "run in poolIO error" + e.message)
                    }
                }).get()
                else -> {
                    Log.d("ddup", "value is error...");
                }

            }
        }


    }
}



