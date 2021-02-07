package com.example.android_aop.aspectj

import android.view.View
import com.example.android_aop.annotation.AopOnClick
import com.example.android_aop.util.AopClickUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature


@Aspect
class ClickAspect {


    /**
     * 定义切点，标记切点为所有被@AopOnclick注解的方法
     */
    @Pointcut("execution(@com.example.android_aop.annotation.AopOnClick * *(..))")
    fun methodClick() {
    }


    /**
     * 定义一个切面方法，包裹切点方法
     */
    @Around("methodClick()")
    @Throws(Throwable::class)
    fun aroundClickJoinPoint(joinPoint: ProceedingJoinPoint) {
        // 取出方法的注解
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method
        if (!method.isAnnotationPresent(AopOnClick::class.java)) {
            return
        }
        val aopOnclick = method.getAnnotation(AopOnClick::class.java)
        var view: View? = null;
        for (arg in joinPoint.args) {
            if (arg is View) {
                view = arg
                break
            }
        }
        // 判断是否快速点击
        if (view != null && !AopClickUtils.isFastDoubleClick(view, aopOnclick.value)) {
            // 不是快速点击，执行原方法
            joinPoint.proceed()
        }
    }


}