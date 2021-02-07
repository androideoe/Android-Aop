package com.example.android_aop.util


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.android_aop.App
import com.example.android_aop.util.PermissionConsts


/**
 * 权限工具类
 */
class PermissionUtils constructor(vararg permissions: String) {

    private val PERMISSIONS: Array<String> = getPermissions();

    private var mPermissions: MutableSet<String>? = null
    private var mPermissionsRequest: Array<String>? = null
    private var mPermissionsGranted: Array<String>? = null
    private var mPermissionsDenied: Array<String>? = null
    private var mPermissionsDeniedForever: Array<String>? = null

    private var mFullCallback: FullCallback? = null

    companion object {
        @Volatile
        var instance: PermissionUtils? = null

        fun getInstance(vararg permissions: String): PermissionUtils? {
            if (instance == null) {
                synchronized(PermissionUtils::class) {
                    if (instance == null) {
                        instance = PermissionUtils(*permissions)
                    }
                }
            }
            return instance
        }
    }

    init {

        mPermissions = mutableSetOf();
        for (permission in permissions) {
            for (aPermission in PermissionConsts.getPermissions(permission)) {
                if (PERMISSIONS.contains(aPermission)) {
                    mPermissions!!.add(aPermission)
                }
            }
        }
    }


    private fun getPermissions(): Array<String> {
        return getPermissions(App.instance().packageName)
    }

    /**
     * 开始请求
     */
    fun request() {
        mPermissionsGranted = arrayOf();
        mPermissionsRequest = arrayOf();
        var i = 0;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mPermissionsGranted = mPermissionsGranted?.plus(mPermissions!!)
            requestCallback()
        } else {
            for (permission in mPermissions!!) {
                if (isGranted(permission)) {
                    mPermissionsGranted = mPermissionsGranted!!.plus(permission)
                } else {
                    mPermissionsRequest = mPermissionsRequest!!.plus(permission)
                }
            }
            if (mPermissionsRequest!!.isEmpty()) {
                requestCallback()
            } else {
                startPermissionActivity()
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun startPermissionActivity() {
        mPermissionsDenied = arrayOf()
        mPermissionsDeniedForever = arrayOf()
        val intent = Intent(App.instance(), PermissionActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        App.instance().startActivity(intent)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    internal class PermissionActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            instance?.mPermissionsRequest?.let { requestPermissions(it, 1) };

        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            instance?.onRequestPermissionsResult(this)
            finish()
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun onRequestPermissionsResult(activity: Activity) {
        getPermissionsStatus(activity)
        requestCallback()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getPermissionsStatus(activity: Activity) {
        for (permission in mPermissionsRequest!!) {
            if (isGranted(permission)) {
                mPermissionsGranted = mPermissionsGranted?.plus(permission)
            } else {
                mPermissionsDenied = mPermissionsDenied?.plus(permission)
                if (!activity.shouldShowRequestPermissionRationale(permission)) {
                    mPermissionsDeniedForever = mPermissionsDeniedForever?.plus(permission)
                }
            }
        }
    }


    private fun requestCallback() {
        if (mFullCallback != null) {
            if (mPermissionsRequest!!.isEmpty()
                || mPermissions!!.size == mPermissionsGranted!!.size
            ) {
                mFullCallback!!.onGranted(mPermissionsGranted)
            } else {
                mFullCallback!!.onDenied(mPermissionsDeniedForever, mPermissionsDenied)
            }
            mFullCallback = null
        }
    }

    /**
     * 获取应用权限列表
     */
    fun getPermissions(packageName: String): Array<String> {
        return App.instance().packageManager
            .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            .requestedPermissions
    }

    fun isGranted(vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (!isGranted(permission)) {
                return false
            }
        }
        return true
    }

    private fun isGranted(permission: String): Boolean {
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || PackageManager.PERMISSION_GRANTED
                == ContextCompat.checkSelfPermission(App.instance(), permission))
    }

    interface FullCallback {
        fun onGranted(permissionsGranted: Array<String>?)
        fun onDenied(
            permissionsDeniedForever: Array<String>?,
            permissionsDenied: Array<String>?
        )
    }

    /**
     * 设置回调
     *
     * @param callback 完整回调接口
     * @return [PermissionUtils]
     */
    fun callback(callback: FullCallback): PermissionUtils? {
        mFullCallback = callback
        return this
    }


}