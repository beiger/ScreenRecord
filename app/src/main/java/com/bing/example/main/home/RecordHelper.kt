package com.bing.example.main.home

import android.content.Intent
import android.media.projection.MediaProjection
import android.net.Uri
import android.os.StrictMode
import com.bing.example.app.globalAudioConfig
import com.bing.example.app.globalVideoConfig
import com.bing.example.main.notification.NotificationDelegate
import com.bing.example.model.RepositoryManager
import com.bing.example.model.entity.VideoInfo
import com.bing.example.module.screenRecord.ScreenRecorder
import com.bing.example.utils.BitmapUtil
import com.bing.example.utils.Constant
import com.bing.mvvmbase.base.AppExecutors
import com.bing.mvvmbase.base.BaseApplication
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object RecordHelper {
        var recordState = RecordState.NOT_RECORD
        private lateinit var mRecorder: ScreenRecorder
        val savingDir: File
                get() = File(Constant.VIDEO_DIR)

        fun newRecorder(mediaProjection: MediaProjection) {
                val video = globalVideoConfig
                val audio = globalAudioConfig // audio can be null
                if (video == null) {
                        ToastUtils.showShort("Create ScreenRecorder failure")
                        mediaProjection.stop()
                        return
                }

                val dir = savingDir
                if (!dir.exists() && !dir.mkdirs()) {
                        return
                }
                val format = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US)
                val output = File(dir, format.format(Date()) + ".mp4")

                mRecorder = ScreenRecorder(video, audio, 1, mediaProjection, output.absolutePath)
                mRecorder.setCallback(object : ScreenRecorder.Callback {
                        var startTime: Long = 0

                        override fun onStop(error: Throwable?) {
                                recordState = RecordState.NOT_RECORD
                                NotificationDelegate.showGlobal()
                                val vmPolicy = StrictMode.getVmPolicy()
                                val file = File(mRecorder.savedPath)
                                try {
                                        // disable detecting FileUriExposure on public file
                                        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().build())
                                        insertIntoDatabase(file)
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
                                        BaseApplication.sContext.sendBroadcast(intent)
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
                                NotificationDelegate.recording(0)
                                recordState = RecordState.ON_RECORD
                        }

                        override fun onRecording(presentationTimeUs: Long) {
                                if (startTime <= 0) {
                                        startTime = presentationTimeUs
                                }
                                val time = (presentationTimeUs - startTime) / 1000
                                LogUtils.i(time.toString() + "")
                                NotificationDelegate.recording(time)
                        }
                })
                mRecorder.start()
        }

        fun stopRecorder() {
                mRecorder.quit()
        }
}


enum class RecordState {
        NOT_RECORD,
        ON_RECORD
}