package com.example.sunkai.heritage.Activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.example.sunkai.heritage.Activity.BaseActivity.BaseStopGlideActivity

import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.PermissionsChecker

/**
 * 此类用于处理不同安卓系统间的权限申请，安卓6.0需要动态申请权限
 */
class PermissionsActivity : BaseStopGlideActivity() {

    private var mChecker: PermissionsChecker? = null // 权限检测器
    private var isRequireCheck: Boolean = false // 是否需要系统权限检测

    // 返回传递的权限参数
    private val permissions: Array<String>
        get() = intent.getStringArrayExtra(EXTRA_PERMISSIONS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent == null || !intent.hasExtra(EXTRA_PERMISSIONS)) {
            throw RuntimeException("PermissionsActivity需要使用静态startActivityForResult方法启动!")
        }
        setContentView(R.layout.activity_permissions)

        mChecker = PermissionsChecker(this)
        isRequireCheck = true
    }

    override fun onResume() {
        super.onResume()
        if (isRequireCheck) {
            val permissions = permissions
            if (mChecker!!.lacksPermissions(*permissions)) {
                requestPermissions(*permissions) // 请求权限
            } else {
                allPermissionsGranted() // 全部权限都已获取
            }
        } else {
            isRequireCheck = true
        }
    }

    // 请求权限兼容低版本
    private fun requestPermissions(vararg permissions: String) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
    }

    // 全部权限均已获取
    private fun allPermissionsGranted() {
        setResult(PERMISSIONS_GRANTED)
        finish()
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            isRequireCheck = true
            allPermissionsGranted()
        } else {
            isRequireCheck = false
            showMissingPermissionDialog()
        }
    }

    // 含有全部的权限
    private fun hasAllPermissionsGranted(grantResults: IntArray): Boolean {
        return !grantResults.contains(PackageManager.PERMISSION_DENIED)
    }

    // 显示缺失权限提示
    private fun showMissingPermissionDialog() {
        val builder = AlertDialog.Builder(this@PermissionsActivity)
        builder.setTitle("帮助")
        builder.setMessage("当前应用缺少必要权限")

        // 拒绝, 退出应用
        builder.setNegativeButton("退出") { _, _ ->
            setResult(PERMISSIONS_DENIED)
            finish()
        }

        builder.setPositiveButton("设置") { _, _ -> startAppSettings() }

        builder.show()
    }

    // 启动应用的设置
    private fun startAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse(PACKAGE_URL_SCHEME + packageName)
        startActivity(intent)
    }

    companion object {

        const val PERMISSIONS_GRANTED = 0 // 权限授权
        const val PERMISSIONS_DENIED = 1 // 权限拒绝

        private const val PERMISSION_REQUEST_CODE = 0 // 系统权限管理页面的参数
        private const val EXTRA_PERMISSIONS = "me.chunyu.clwang.permission.extra_permission" // 权限参数
        private const val PACKAGE_URL_SCHEME = "package:" // 方案

        // 启动当前权限页面的公开接口
        fun startActivityForResult(activity: Activity, requestCode: Int, vararg permissions: String) {
            val intent = Intent(activity, PermissionsActivity::class.java)
            intent.putExtra(EXTRA_PERMISSIONS, permissions)
            ActivityCompat.startActivityForResult(activity, intent, requestCode, null)
        }
    }
}
