package com.example.android_aop

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.android_aop.annotation.*
import com.example.android_aop.util.PermissionConsts
import com.example.android_aop.util.ThreadType
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()


    }

    private fun initView() {
        findViewById<Button>(R.id.btn_fast_click).setOnClickListener(this)
        findViewById<Button>(R.id.btn_methodtime).setOnClickListener(this)
        findViewById<Button>(R.id.btn_aop).setOnClickListener(this)
        findViewById<Button>(R.id.btn_thread).setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btn_fast_click -> {
                noDoubleClick(v)
            }

            R.id.btn_methodtime -> {
                loadData()
            }

            R.id.btn_aop -> {
                requestPermissions()
            }

            R.id.btn_thread -> {
                Thread(Runnable {
                    changeThread(v)
                }).start()

            }


        }
    }

    /**
     * 快速点击
     */
    @AopOnClick(1000)
    fun noDoubleClick(v: View?) {
        Log.e("ddup", "noDoubleClick...")

    }

    /**
     * 模拟加载数据
     */
    @AopMethodTime
    fun loadData() {
        for (index in 1..1000) {
            Log.e("ddup", "load data..." + index)
        }
    }

    /**
     * 申请权限
     */
    @AopPermission(
        arrayOf<String>(
            PermissionConsts.CALENDAR,
            PermissionConsts.CAMERA,
            PermissionConsts.LOCATION
        )
    )
    private fun requestPermissions() {
        Log.e("ddup", "requestPermissions success...")
    }

    /**
     * 改变线程
     */
    @AopOnClick()
    @AopIOThread(ThreadType.Single)
    private fun changeThread(v: View?) {
        Log.e("ddup", "current thread = " + Thread.currentThread().name.toString())
    }
}