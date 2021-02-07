package com.example.android_aop.aspectj

import android.os.SystemClock
import android.util.Log
import com.example.android_aop.annotation.AopMethodTime
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature

@Aspect
class MethodTimeAspect {

    /**
     * 定义切点，标记切点为所有被@AopMethodTime
     */
    @Pointcut("execution(@com.example.android_aop.annotation.AopMethodTime * *(..))")
    fun methodTime() {
    }


    /**
     * 定义一个切面方法，包裹切点方法
     */
    @Around("methodTime()")
    @Throws(Throwable::class)
    fun aroundMethodTimeJoinPoint(joinPoint: ProceedingJoinPoint) {
        // 取出方法的注解
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method
        if (!method.isAnnotationPresent(AopMethodTime::class.java)) {
            return
        }
        val aopMethodTime = method.getAnnotation(AopMethodTime::class.java)

        val beginTime = SystemClock.currentThreadTimeMillis()
        joinPoint.proceed()
        val endTime = SystemClock.currentThreadTimeMillis()
        val dx = endTime - beginTime
        Log.e("ddup", methodSignature.name + "方法执行耗时：" + dx + "ms")
    }
}