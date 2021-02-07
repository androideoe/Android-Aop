package com.example.android_aop.util

import android.view.View


/**
 * 快速点击工具类
 */
object AopClickUtils {

    /**
     * 最近一次点击时间
     */
    private var mLastClickTime: Long = 0

    /**
     * 最近一次点击的控件ID
     */
    private var mLastClickViewId: Int = 0


    /**
     * 是否快速点击
     */
    fun isFastDoubleClick(v: View, intervalMillis: Long): Boolean {

        var time = System.currentTimeMillis();
        var timeInterval = time - mLastClickTime
        val viewId = v.id
        if (timeInterval > 0 && timeInterval < intervalMillis && viewId == mLastClickViewId) {
            return true
        } else {
            mLastClickTime = time
            mLastClickViewId = viewId;
            return false
        }


    }


}