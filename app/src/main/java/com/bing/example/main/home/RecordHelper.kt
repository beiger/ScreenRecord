package com.bing.example.main.home

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bing.example.R
import com.bing.example.main.notification.NotificationDelegate
import com.bing.example.model.RepositoryManager
import com.bing.example.model.entity.VideoInfo
import com.bing.example.module.screenRecord.*
import com.bing.example.utils.BitmapUtil
import com.bing.example.utils.Constant
import com.bing.mvvmbase.base.AppExecutors
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class RecordHelper(val activity: AppCompatActivity, private val viewModel: MainViewModel, private val notificationDelegate: NotificationDelegate) {
        private var mMediaProjectionManager: MediaProjectionManager = activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        var mRecorder: ScreenRecorder? = null
        private val mStopActionReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                        if (ACTION_STOP == intent.action) {
                                stopRecorder()
                        }
                }
        }
        private var disposable: Disposable? = null

        fun onRecordButtonClick() {
                when {
                        mRecorder != null -> stopRecorder()
                        hasPermissions() -> startCaptureIntent()
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> requestPermissions()
                        else -> ToastUtils.showShort("No permission to write sd card")
                }
        }

        @TargetApi(Build.VERSION_CODES.M)
        private fun requestPermissions() {
                val permissions = if (viewModel.audioEncodeConfig != null)
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                else
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                var showRationale = false
                for (perm in permissions) {
                        showRationale = showRationale or activity.shouldShowRequestPermissionRationale(perm)
                }
                if (!showRationale) {
                        activity.requestPermissions(permissions, REQUEST_PERMISSIONS)
                        return
                }
                AlertDialog.Builder(activity)
                        .setMessage(R.string.request_permission)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok) { _, _ -> activity.requestPermissions(permissions, REQUEST_PERMISSIONS) }
                        .setNegativeButton(android.R.string.cancel, null)
                        .create()
                        .show()
        }

        private fun hasPermissions(): Boolean {
                val pm = activity.packageManager
                val packageName = activity.packageName
                val granted = (if (viewModel.audioEncodeConfig != null) {
                        pm.checkPermission(Manifest.permission.RECORD_AUDIO, packageName)
                } else {
                        PackageManager.PERMISSION_GRANTED
                }) or
                pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, packageName)
                return granted == PackageManager.PERMISSION_GRANTED
        }

        fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
                if (requestCode == REQUEST_PERMISSIONS) {
                        var granted = PackageManager.PERMISSION_GRANTED
                        for (r in grantResults) {
                                granted = granted or r
                        }
                        if (granted == PackageManager.PERMISSION_GRANTED) {
                                startCaptureIntent()
                        } else {
                                ToastUtils.showShort(activity.getString(R.string.no_permission))
                        }
                }
        }

        private fun startCaptureIntent() {
                val captureIntent = mMediaProjectionManager.createScreenCaptureIntent()
                activity.startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION)
        }

        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == Activity.RESULT_OK) {
                        val mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data!!)
                                ?: return

                        val video = viewModel.videoEncodeConfig
                        val audio = viewModel.audioEncodeConfig // audio can be null
                        if (video == null) {
                                ToastUtils.showShort("Create ScreenRecorder failure")
                                mediaProjection.stop()
                                return
                        }

                        val dir = savingDir
                        if (!dir.exists() && !dir.mkdirs()) {
                                cancelRecorder()
                                return
                        }
                        val format = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US)
                        val file = File(dir, format.format(Date()) + ".mp4")
                        mRecorder = newRecorder(mediaProjection, video, audio, file)
                        if (hasPermissions()) {
                                activity.moveTaskToBack(false)
                                disposable = Single.timer(300, TimeUnit.MILLISECONDS).subscribe { aLong -> startRecorder() }
                        } else {
                                cancelRecorder()
                        }
                }
        }

        private fun newRecorder(mediaProjection: MediaProjection, video: VideoEncodeConfig,
                                audio: AudioEncodeConfig?, output: File): ScreenRecorder {
                val r = ScreenRecorder(video, audio,
                        1, mediaProjection, output.absolutePath)
                r.setCallback(object : ScreenRecorder.Callback {
                        var startTime: Long = 0

                        override fun onStop(error: Throwable?) {
                                notificationDelegate.clear()
                                val vmPolicy = StrictMode.getVmPolicy()
                                val file = File(mRecorder!!.savedPath)
                                mRecorder = null
                                try {
                                        // disable detecting FileUriExposure on public file
                                        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().build())
                                        insertIntoDatabase(file)
                                        val am = activity.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
                                        am.moveTaskToFront(activity.taskId, 0)
                                } finally {
                                        StrictMode.setVmPolicy(vmPolicy)
                                }
                                if (error != null) {
                                        ToastUtils.showShort("Recorder error ! See logcat for more details")
                                        error.printStackTrace()
                                        output.delete()
                                } else {
                                        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                                                .addCategory(Intent.CATEGORY_DEFAULT)
                                                .setData(Uri.fromFile(output))
                                        activity.sendBroadcast(intent)
                                }
                        }

                        private fun insertIntoDatabase(file: File) {
                                AppExecutors.diskIO.execute {
                                        val imagePath = Constant.THUMB_DIR + file.name
                                        val bitmap = BitmapUtil.createVideoThumbnailLocal(file.absolutePath, 0)
                                        BitmapUtil.saveBitmap(bitmap, imagePath)
                                        val videoInfo = VideoInfo(0, file.name, file.absolutePath, imagePath)
                                        RepositoryManager.instance().insertVideo(videoInfo)
                                }
                        }

                        override fun onStart() {
                                notificationDelegate.recording(0)
                        }

                        override fun onRecording(presentationTimeUs: Long) {
                                if (startTime <= 0) {
                                        startTime = presentationTimeUs
                                }
                                val time = (presentationTimeUs - startTime) / 1000
                                LogUtils.i(time.toString() + "")
                                notificationDelegate.recording(time)
                        }
                })
                return r
        }

        private fun startRecorder() {
                if (mRecorder == null) return
                mRecorder!!.start()
                activity.registerReceiver(mStopActionReceiver, IntentFilter(ACTION_STOP))
        }

        private fun stopRecorder() {
                if (mRecorder != null) {
                        mRecorder!!.quit()
                }
                try {
                        activity.unregisterReceiver(mStopActionReceiver)
                } catch (e: Exception) {

                }

        }

        private fun cancelRecorder() {
                if (mRecorder == null) return
                Toast.makeText(activity, "Permission denied! Screen recorder is cancel", Toast.LENGTH_SHORT).show()
                stopRecorder()
        }

        fun onDestroy() {
                if (mRecorder != null) {
                        stopRecorder()
                }
                disposable?.dispose()
        }

        companion object {
                const val ACTION_STOP = "net.yrom.screenrecorder.action.STOP"
                const val REQUEST_MEDIA_PROJECTION = 1
                const val REQUEST_PERMISSIONS = 2

                val savingDir: File
                        get() = File(Constant.VIDEO_DIR)
        }
}