package com.bing.example.main.service

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import com.afollestad.materialdialogs.MaterialDialog
import com.bing.example.R
import com.bing.example.app.globalAudioConfig
import com.bing.example.main.home.RecordHelper
import com.tbruyelle.rxpermissions2.RxPermissions

class RecordActivity : Activity() {
        private lateinit var mMediaProjectionManager: MediaProjectionManager

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

                //1. 获取悬浮窗权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                        getOverlayPermission()
                } else {
                        startCapture()
                }
        }

        private fun getOverlayPermission() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!Settings.canDrawOverlays(this)) {
                                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                                intent.data = Uri.parse("package:$packageName")
                                startActivityForResult(intent, REQUEST_OVERLAY_PERMISSIONS)
                        }
                }
        }

        private fun startCapture() {
                val permissions = if (globalAudioConfig != null)
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                else
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                val disposable =
                        RxPermissions(this)
                                .request(*permissions).subscribe {
                                        if (it) {
                                                startCaptureIntent()
                                        } else {
                                                showNeedRecordPermissionDialog(permissions)
                                        }
                                }
        }

        private fun startCaptureIntent() {
                val captureIntent = mMediaProjectionManager.createScreenCaptureIntent()
                startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                if (requestCode == REQUEST_OVERLAY_PERMISSIONS
                        && resultCode == Activity.RESULT_OK
                        && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!Settings.canDrawOverlays(this)) {
                                showNeedOverlayDialog()
                        } else {
                                startCapture()
                        }
                }
                if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == Activity.RESULT_OK) {
                        val mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data!!) ?: return
                        CountDownManager(this)
                                .startCountDown(3, {
                                        finish()
                                },  {
                                        RecordHelper.newRecorder(mediaProjection)
                                 })
                }
        }

        private fun showNeedOverlayDialog() {
                MaterialDialog(this)
                        .message(R.string.need_overlay_permission)
                        .positiveButton(android.R.string.ok) {
                                getOverlayPermission()
                        }.negativeButton(android.R.string.cancel) {
                                finish()
                        }
        }

        private fun showNeedRecordPermissionDialog(permissions: Array<String>) {
                MaterialDialog(this)
                        .message(text = getPermissionsString(permissions))
                        .positiveButton(android.R.string.ok) {
                                toSelfSetting(this)
                        }.negativeButton(android.R.string.cancel) {
                                finish()
                        }
        }

        private fun toSelfSetting(context: Context) {
                val mIntent = Intent()
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                mIntent.data = Uri.fromParts("package", context.packageName, null)
                context.startActivity(mIntent)
        }


        private fun getPermissionsString(permissions: Array<String>?): String {
                if (permissions == null || permissions.isEmpty()) {
                        return ""
                }
                val result = StringBuilder()
                result.append(getString(R.string.need_permisson) + ":\n")
                for (i in permissions.indices) {
                        if (i < permissions.size - 1) {
                                result.append("· ").append(getPermissionGroupLabel(permissions[i])).append("\n")
                        } else {
                                result.append("· ").append(getPermissionGroupLabel(permissions[i]))
                        }
                }
                return result.toString()
        }

        private fun getPermissionGroupLabel(permissionName: String): String? {
                var label: String? = null
                try {
                        val permissionInfo: PermissionInfo = packageManager.getPermissionInfo(permissionName, 0)
                        val groupInfo = packageManager.getPermissionGroupInfo(permissionInfo.group, 0)
                        label = groupInfo.loadLabel(packageManager).toString()
                } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                }

                return label
        }

        companion object {
                const val REQUEST_MEDIA_PROJECTION = 1
                const val REQUEST_OVERLAY_PERMISSIONS = 3
        }
}
