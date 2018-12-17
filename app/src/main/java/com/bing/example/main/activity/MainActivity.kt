package com.bing.example.main.activity

import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.Toast

import com.bing.example.databinding.ActivityMainBinding
import com.bing.example.main.fragment.VideoListFragment
import com.bing.example.main.viewmodel.MainViewModel
import com.bing.example.model.RepositoryManager
import com.bing.example.model.entity.VideoInfo
import com.bing.example.module.screenRecord.AudioEncodeConfig
import com.bing.example.module.screenRecord.Notifications
import com.bing.example.module.screenRecord.ScreenRecorder
import com.bing.example.module.screenRecord.VideoEncodeConfig
import com.bing.example.module.screenRecord.VideoEncodeConfigParcelable
import com.bing.example.otherdetails.AboutActivity
import com.bing.example.otherdetails.FeedbackActivity
import com.bing.example.utils.BitmapUtil
import com.bing.example.utils.Constant
import com.bing.mvvmbase.base.AppExecutors
import com.bing.mvvmbase.base.BaseActivity
import com.bing.mvvmbase.base.viewpager.BaseFragmentPagerAdapter
import com.bing.mvvmbase.utils.UiUtil
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem

import com.bing.example.R

import java.io.File
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import cn.jzvd.Jzvd
import io.reactivex.Single
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Build.VERSION_CODES.M
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), View.OnClickListener {
        override fun initViewModel() {
                mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        }

        lateinit var mVideoListFragment: VideoListFragment
        lateinit var mMediaProjectionManager: MediaProjectionManager
        lateinit var mNotifications: Notifications
        var mRecorder: ScreenRecorder? = null
        private val mStopActionReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                        if (ACTION_STOP == intent.action) {
                                stopRecorder()
                        }
                }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

                val toolbar = findViewById<Toolbar>(R.id.toolbar)
                setSupportActionBar(toolbar)
                val actionBar = supportActionBar!!
                actionBar.title = ""

                val headerResult = AccountHeaderBuilder()
                        .withActivity(this)
                        .withCompactStyle(false)
                        .withHeaderBackground(R.drawable.fall)
                        .withSavedInstance(savedInstanceState)
                        .build()

                DrawerBuilder()
                        .withActivity(this)
                        .withAccountHeader(headerResult)
                        .withToolbar(toolbar)
                        .withFullscreen(true)
                        .addDrawerItems(
                                PrimaryDrawerItem().withName(R.string.config).withIcon(R.drawable.ic_setting).withIdentifier(1),
                                PrimaryDrawerItem().withName(R.string.feedback).withIcon(R.drawable.ic_feedback).withIdentifier(3),
                                PrimaryDrawerItem().withName(R.string.about).withIcon(R.drawable.ic_about).withIdentifier(4)
                        )
                        .withSelectedItem(-1)
                        .withOnDrawerItemClickListener { _, _, drawerItem ->
                                when {
                                        drawerItem!!.identifier == 1L -> {
                                                val intent = Intent(this@MainActivity, SettingActivity::class.java)
                                                startActivityForResult(intent, REQUEST_SETTINGS)
                                        }
                                        drawerItem.identifier == 3L -> {
                                                val intent = Intent(this@MainActivity, FeedbackActivity::class.java)
                                                startActivityForResult(intent, REQUEST_SETTINGS)
                                        }
                                        drawerItem.identifier == 4L -> {
                                                val intent = Intent(this@MainActivity, AboutActivity::class.java)
                                                startActivityForResult(intent, REQUEST_SETTINGS)
                                        }
                                }
                                false
                        }
                        .withSavedInstance(savedInstanceState)
                        .build()
        }

        override fun onCreateFirst() {
                UiUtil.setBarColorAndFontBlack(this, Color.TRANSPARENT)
        }

        override fun layoutId(): Int {
                return R.layout.activity_main
        }

        override fun bindAndObserve() {
                mMediaProjectionManager = applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                mNotifications = Notifications(applicationContext)

                mViewModel.videoEncodeConfigLive.observe(this, Observer{ config ->
                        if (config != null) {
                                mViewModel.videoEncodeConfig = config
                        }
                })
                mViewModel.audioEncodeConfigLive.observe(this, Observer{ audioEncodeConfig ->
                        if (audioEncodeConfig != null) {
                                mViewModel.audioEncodeConfig = audioEncodeConfig
                        }
                })
                mBinding.record.setOnClickListener(this)
                mBinding.back.setOnClickListener(this)
                mBinding.delete.setOnClickListener(this)
                mBinding.selectAll.setOnClickListener(this)
                initViewPager()
                mBinding.isNormalMode = mViewModel.isNormalMode
        }

        private fun initViewPager() {
                val viewPager = mBinding.viewPager
                viewPager.setScanScroll(false)
                mVideoListFragment = VideoListFragment()
                val fragments = ArrayList<Fragment>()
                fragments.add(mVideoListFragment)
                val fragmentPagerAdapter = BaseFragmentPagerAdapter(supportFragmentManager, fragments)
                viewPager.adapter = fragmentPagerAdapter
        }

        override fun onClick(v: View) {
                when (v.id) {
                        R.id.record -> onButtonClick()

                        R.id.back -> mVideoListFragment.onClickBack()

                        R.id.delete -> mVideoListFragment.onClickDelete()

                        R.id.select_all -> mVideoListFragment.onClickSelectAll()
                }
        }

        private fun startCaptureIntent() {
                val captureIntent = mMediaProjectionManager.createScreenCaptureIntent()
                startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == Activity.RESULT_OK) {
                        val mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data!!)
                                ?: return

                        val video = mViewModel.videoEncodeConfig
                        val audio = mViewModel.audioEncodeConfig // audio can be null
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
                                moveTaskToBack(false)
                                addDisposable(Single.timer(300, TimeUnit.MILLISECONDS).subscribe { aLong -> startRecorder() })
                        } else {
                                cancelRecorder()
                        }
                } else if (requestCode == REQUEST_SETTINGS) {
                        if (resultCode == Activity.RESULT_OK) {
                                val videoEncodeConfigParcelable = data!!.getParcelableExtra<VideoEncodeConfigParcelable>("video")
                                if (videoEncodeConfigParcelable != null) {
                                        mViewModel.videoEncodeConfig = videoEncodeConfigParcelable.toConfig()
                                }
                                val audioEncodeConfig = data.getParcelableExtra<AudioEncodeConfig>("audio")
                                if (audioEncodeConfig != null) {
                                        mViewModel.audioEncodeConfig = audioEncodeConfig
                                }
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
                                mNotifications.clear()
                                val vmPolicy = StrictMode.getVmPolicy()
                                val file = File(mRecorder!!.savedPath)
                                mRecorder = null
                                try {
                                        // disable detecting FileUriExposure on public file
                                        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().build())
                                        insertIntoDatabase(file)
                                        val am = getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
                                        am.moveTaskToFront(taskId, 0)
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
                                        sendBroadcast(intent)
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
                                mNotifications.recording(0)
                        }

                        override fun onRecording(presentationTimeUs: Long) {
                                if (startTime <= 0) {
                                        startTime = presentationTimeUs
                                }
                                val time = (presentationTimeUs - startTime) / 1000
                                LogUtils.i(time.toString() + "")
                                mNotifications.recording(time)
                        }
                })
                return r
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
                        }
                }
        }

        override fun onBackPressed() {
                if (Jzvd.backPress()) {
                        return
                }
                if (mVideoListFragment.onBackPressed()) {
                        return
                }
                super.onBackPressed()
        }

        override fun onPause() {
                super.onPause()
                Jzvd.releaseAllVideos()
        }

        override fun onDestroy() {
                super.onDestroy()
                stopRecorder()
        }

        private fun onButtonClick() {
                mVideoListFragment.intoNormalMode()
                if (mRecorder != null) {
                        stopRecorder()
                } else if (hasPermissions()) {
                        startCaptureIntent()
                } else if (Build.VERSION.SDK_INT >= M) {
                        requestPermissions()
                } else {
                        ToastUtils.showShort("No permission to write sd card")
                }
        }

        private fun startRecorder() {
                if (mRecorder == null) return
                mRecorder!!.start()
                registerReceiver(mStopActionReceiver, IntentFilter(ACTION_STOP))
        }

        private fun stopRecorder() {
                if (mRecorder != null) {
                        mRecorder!!.quit()
                }
                try {
                        unregisterReceiver(mStopActionReceiver)
                } catch (e: Exception) {
                        //ignored
                }

        }

        private fun cancelRecorder() {
                if (mRecorder == null) return
                Toast.makeText(this, "Permission denied! Screen recorder is cancel", Toast.LENGTH_SHORT).show()
                stopRecorder()
        }

        @TargetApi(M)
        private fun requestPermissions() {
                val permissions = if (mViewModel.audioEncodeConfig != null)
                        arrayOf(WRITE_EXTERNAL_STORAGE, RECORD_AUDIO)
                else
                        arrayOf(WRITE_EXTERNAL_STORAGE)
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
                        .setNegativeButton(android.R.string.cancel, null)
                        .create()
                        .show()
        }

        private fun hasPermissions(): Boolean {
                val pm = packageManager
                val packageName = packageName
                val granted = (if (mViewModel.audioEncodeConfig != null) pm.checkPermission(RECORD_AUDIO, packageName) else PackageManager.PERMISSION_GRANTED) or pm.checkPermission(WRITE_EXTERNAL_STORAGE, packageName)
                return granted == PackageManager.PERMISSION_GRANTED
        }

        companion object {
                const val ACTION_STOP = "net.yrom.screenrecorder.action.STOP"
                private const val REQUEST_MEDIA_PROJECTION = 1
                private const val REQUEST_PERMISSIONS = 2
                private const val REQUEST_SETTINGS = 2

                private val savingDir: File
                        get() = File(Constant.VIDEO_DIR)
        }
}
