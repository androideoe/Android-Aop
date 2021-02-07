package com.example.android_aop.aspectj

import android.util.Log
import com.example.android_aop.annotation.AopPermission
import com.example.android_aop.util.PermissionUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature

@Aspect
class PermissionAspect {


    /**
     * 定义切点，标记切点为所有被@AopMethodTime
     */
    @Pointcut("execution(@com.example.android_aop.annotation.AopPermission * *(..))")
    fun methodPermission() {
    }


    /**
     * 定义一个切面方法，包裹切点方法
     */
    @Around("methodPermission()")
    @Throws(Throwable::class)
    fun aroundRunIOJoinPoint(joinPoint: ProceedingJoinPoint) {
        // 取出方法的注解
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method
        if (!method.isAnnotationPresent(AopPermission::class.java)) {
            return
        }
        val aopPermission = method.getAnnotation(AopPermission::class.java)

        PermissionUtils.getInstance(*aopPermission.value)
            ?.callback(object : PermissionUtils.FullCallback {
                override fun onGranted(permissionsGranted: Array<String>?) {
                    try {
                        Log.e(
                            "ddup",
                            "request onGranted success = " + permissionsGranted?.joinToString(
                                separator = ","
                            )
                        );
                        joinPoint.proceed() // 获得权限，执行原方法
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        Log.e("ddup", "request permission error = " + e.message);
                    }
                }

                override fun onDenied(
                    permissionsDeniedForever: Array<String>?,
                    permissionsDenied: Array<String>?
                ) {
                    Log.e(
                        "ddup",
                        "onDenied permissionsDeniedForever = " + permissionsDeniedForever?.joinToString(
                            separator = ","
                        )
                    );
                    Log.e(
                        "ddup",
                        "onDenied permissionsDenied = " + permissionsDenied?.joinToString(
                            separator = ","
                        )
                    );

                }

            })
            ?.request()


    }


}