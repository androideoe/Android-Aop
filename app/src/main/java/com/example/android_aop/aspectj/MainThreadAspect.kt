package com.example.android_aop.aspectj

import android.os.Looper
import android.util.Log
import com.example.android_aop.annotation.AopMainThread
import com.example.android_aop.util.AppExecutors
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature

@Aspect
class MainThreadAspect {

    /**
     * 定义切点，标记切点为所有被@AopMethodTime
     */
    @Pointcut("execution(@com.example.android_aop.annotation.AopMainThread * *(..))")
    fun methodRunInMain() {
    }


    /**
     * 定义一个切面方法，包裹切点方法
     */
    @Around("methodRunInMain()")
    @Throws(Throwable::class)
    fun aroundRunMainJoinPoint(joinPoint: ProceedingJoinPoint) {
        // 已经主线程直接运行当前方法
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Log.d("ddup", "aleady in Main thread...");
            joinPoint.proceed()
        } else {
            Log.d("ddup", "【当前线程】= " + Thread.currentThread().name);
            // 取出方法的注解
            val methodSignature = joinPoint.signature as MethodSignature
            val method = methodSignature.method
            if (!method.isAnnotationPresent(AopMainThread::class.java)) {
                return
            }

//            val runInMain = method.getAnnotation(AopMainThread::class.java)
            AppExecutors.get()!!.mainThread().execute { joinPoint.proceed() }


        }


    }
}