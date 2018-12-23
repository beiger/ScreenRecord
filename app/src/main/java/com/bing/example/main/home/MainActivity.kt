package com.bing.example.main.home

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View

import com.bing.example.databinding.ActivityMainBinding
import com.bing.example.main.videolist.VideoListFragment
import com.bing.example.module.screenRecord.AudioEncodeConfig
import com.bing.example.module.screenRecord.VideoEncodeConfigParcelable
import com.bing.example.otherdetails.AboutActivity
import com.bing.example.otherdetails.FeedbackActivity
import com.bing.mvvmbase.base.BaseActivity
import com.bing.mvvmbase.base.viewpager.BaseFragmentPagerAdapter
import com.bing.mvvmbase.utils.UiUtil
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem

import com.bing.example.R

import java.util.ArrayList
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import cn.jzvd.Jzvd
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bing.example.main.home.floatingview.FloatHelper
import com.bing.example.main.notification.NotificationDelegate
import com.bing.example.otherdetails.SettingActivity

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), View.OnClickListener {
        private lateinit var mVideoListFragment: VideoListFragment
        private lateinit var mRecordHelper: RecordHelper
        private lateinit var mFloatHelper: FloatHelper
        private val mNotificationDelegate: NotificationDelegate by lazy {
                NotificationDelegate(this)
        }
        private val mStartRecordReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                        if (ACTION_START == intent.action) {
                                mRecordHelper.onRecordButtonClick()
                        }
                }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                initDrawer(savedInstanceState)

                initRecordHelper()
                initFloatHelper()
                initBroadcastRecieve()
        }

        private fun initDrawer(savedInstanceState: Bundle?) {
                val toolbar: Toolbar = findViewById(R.id.toolbar)
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

        override fun initViewModel() {
                mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        }

        override fun bindAndObserve() {
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

        private fun initRecordHelper() {
                mRecordHelper = RecordHelper(this, mViewModel, mNotificationDelegate)
        }

        private fun initFloatHelper() {
                mFloatHelper = FloatHelper(this)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        val channelId = "default_floatingview_channel";
//                        val channelName = "Default Channel";
//                        val defaultChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_MIN);
//                        val manager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager;
//                        manager.createNotificationChannel(defaultChannel)
//                }
//                Looper.myQueue().addIdleHandler {
//                        mFloatHelper.showFloatingView(true, true)
//                        false
//                }
        }

        private fun initBroadcastRecieve() {
                registerReceiver(mStartRecordReceiver, IntentFilter(ACTION_START))
                mNotificationDelegate.showGlobal()
        }

        override fun onClick(v: View) {
                when (v.id) {
                        R.id.record -> onRecordButtonClick()

                        R.id.back -> mVideoListFragment.onClickBack()

                        R.id.delete -> mVideoListFragment.onClickDelete()

                        R.id.select_all -> mVideoListFragment.onClickSelectAll()
                }
        }

        private fun onRecordButtonClick() {
                mVideoListFragment.intoNormalMode()
                mRecordHelper.onRecordButtonClick()
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                mRecordHelper.onActivityResult(requestCode, resultCode, data)
                mFloatHelper.onActivityResult(requestCode, resultCode, data)
                if (requestCode == REQUEST_SETTINGS) {
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

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
                mRecordHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

        override fun onBackPressed() {
                if (Jzvd.backPress()) {
                        return
                }
                if (mVideoListFragment.onBackPressed()) {
                        return
                }
                moveTaskToBack(false)
        }

        override fun onPause() {
                super.onPause()
                Jzvd.releaseAllVideos()
        }

        override fun onDestroy() {
                super.onDestroy()
                mRecordHelper.onDestroy()
        }

        companion object {
                private const val REQUEST_SETTINGS = 2
                const val ACTION_START = "net.yrom.screenrecorder.action.START"
        }
}
