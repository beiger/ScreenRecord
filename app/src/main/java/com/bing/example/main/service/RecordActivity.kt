package com.bing.example.main.service

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import com.bing.example.R
import com.bing.example.app.globalAudioConfig
import com.bing.example.main.home.RecordHelper
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils

class RecordActivity : Activity() {
        private lateinit var mMediaProjectionManager: MediaProjectionManager

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                LogUtils.i("------onCreate")
                mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                when {
                        hasPermissions() -> startCaptureIntent()
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> requestPermissions()
                        else -> ToastUtils.showShort("No permission to write sd card")
                }
        }

        @TargetApi(Build.VERSION_CODES.M)
        private fun requestPermissions() {
                val permissions = if (globalAudioConfig != null)
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                else
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                var showRationale = false
                for (perm in permissions) {
                        showRationale = showRationale or shouldShowRequestPermissionRationale(perm)
                }
                if (!showRationale) {
                        requestPermissions(permissions, REQUEST_PERMISSIONS)
                        return
                }
                AlertDialog.Builder(this)
                        .setMessage(R.string.request_permission)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok) { _, _ -> requestPermissions(permissions, REQUEST_PERMISSIONS) }
                        .setNegativeButton(android.R.string.cancel) {_, _ -> finish()}
                        .create()
                        .show()
        }

        private fun hasPermissions(): Boolean {
                val pm = packageManager
                val packageName = packageName
                val granted = (if (globalAudioConfig != null) {
                        pm.checkPermission(Manifest.permission.RECORD_AUDIO, packageName)
                } else {
                        PackageManager.PERMISSION_GRANTED
                }) or pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, packageName)
                return granted == PackageManager.PERMISSION_GRANTED
        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
                if (requestCode == REQUEST_PERMISSIONS) {
                        var granted = PackageManager.PERMISSION_GRANTED
                        for (r in grantResults) {
                                granted = granted or r
                        }
                        if (granted == PackageManager.PERMISSION_GRANTED) {
                                startCaptureIntent()
                        } else {
                                ToastUtils.showShort(getString(R.string.no_permission))
                                finish()
                        }
                }
        }

        private fun startCaptureIntent() {
                val captureIntent = mMediaProjectionManager.createScreenCaptureIntent()
                startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == Activity.RESULT_OK) {
                        val mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data!!) ?: return

                        RecordHelper.newRecorder(mediaProjection)
                }
                finish()
        }

        companion object {
                const val REQUEST_MEDIA_PROJECTION = 1
                const val REQUEST_PERMISSIONS = 2
        }
}
