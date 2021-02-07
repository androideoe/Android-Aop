package com.example.android_aop

import android.app.Application
import kotlin.properties.Delegates

class App : Application() {

    companion object {
        private lateinit var instance: App
        fun instance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}