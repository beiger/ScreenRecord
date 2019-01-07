package com.bing.example.main.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cn.jzvd.Jzvd
import com.bing.example.R
import com.bing.example.databinding.ActivityMainBinding
import com.bing.example.main.service.RecordService
import com.bing.example.main.service.RecordService.Companion.ACTION_RECORD
import com.bing.example.main.videolist.VideoListFragment
import com.bing.example.module.screenRecord.AudioEncodeConfig
import com.bing.example.module.screenRecord.VideoEncodeConfigParcelable
import com.bing.example.otherdetails.AboutActivity
import com.bing.example.otherdetails.FeedbackActivity
import com.bing.example.otherdetails.SettingActivity
import com.bing.example.search.SearchActivity
import com.bing.example.videoedit.VideoEditActivity
import com.bing.mvvmbase.base.BaseActivity
import com.bing.mvvmbase.base.viewpager.BaseFragmentPagerAdapter
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import org.jetbrains.anko.startActivity
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
        private lateinit var mDrawer: Drawer
        private lateinit var mVideoListFragment: VideoListFragment

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                initDrawer(savedInstanceState)
                startService(Intent(this, RecordService::class.java))
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

                mDrawer = DrawerBuilder()
                        .withActivity(this)
                        .withAccountHeader(headerResult)
//                        .withToolbar(toolbar)
                        .withFullscreen(true)
                        .addDrawerItems(
                                PrimaryDrawerItem().withName(R.string.video_edit).withIcon(R.drawable.ic_setting).withIdentifier(1),
                                PrimaryDrawerItem().withName(R.string.config).withIcon(R.drawable.ic_setting).withIdentifier(2),
                                PrimaryDrawerItem().withName(R.string.feedback).withIcon(R.drawable.ic_feedback).withIdentifier(3),
                                PrimaryDrawerItem().withName(R.string.about).withIcon(R.drawable.ic_about).withIdentifier(4)
                        )
                        .withSelectedItem(-1)
                        .withOnDrawerItemClickListener { _, _, drawerItem ->
                                when {
                                        drawerItem!!.identifier == 1L -> {
                                                startActivity<VideoEditActivity>()
                                        }
                                        drawerItem.identifier == 2L -> {
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
                addOnClickListener(mBinding.menu, mBinding.record, mBinding.back, mBinding.delete, mBinding.selectAll, mBinding.searchText, mBinding.ivSearch)
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
                        R.id.menu -> {
                                if (mDrawer.isDrawerOpen) {
                                        mDrawer.closeDrawer()
                                } else {
                                        mDrawer.openDrawer()
                                }
                        }

                        R.id.searchText, R.id.ivSearch -> {
                                startActivity<SearchActivity>()
//                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        }

                        R.id.record -> onRecordButtonClick()

                        R.id.back -> mVideoListFragment.onClickBack()

                        R.id.delete -> mVideoListFragment.onClickDelete()

                        R.id.select_all -> mVideoListFragment.onClickSelectAll()
                }
        }

        private fun onRecordButtonClick() {
                mVideoListFragment.intoNormalMode()
                sendBroadcast(Intent(ACTION_RECORD))
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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

        companion object {
                private const val REQUEST_SETTINGS = 2
        }
}
