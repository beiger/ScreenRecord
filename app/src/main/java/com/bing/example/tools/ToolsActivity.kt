package com.bing.example.tools

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bing.example.R
import com.bing.example.databinding.ActivityVideoEditBinding
import com.bing.example.videoedit.OneVideoEditActivity
import com.bing.mvvmbase.base.BaseViewModel
import com.bing.mvvmbase.base.recycleview.BaseRecycleViewActivity
import com.bing.mvvmbase.base.recycleview.BaseRecycleViewAdapter
import com.bing.mvvmbase.model.datawrapper.Status
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import org.jetbrains.anko.startActivity
import android.content.Intent
import com.bing.example.widget.PermissionDialog
import com.tbruyelle.rxpermissions2.RxPermissions


class ToolsActivity : BaseRecycleViewActivity<ActivityVideoEditBinding, BaseViewModel, VideoEditAdapter, VideoEditType>() {
        override val data: LiveData<List<VideoEditType>>
                get() {
                        val list = listOf(VideoEditType("shipinbianji"))
                        val temp = MutableLiveData<List<VideoEditType>>()
                        temp.value = list
                        return temp
                }

        override val layoutManager: RecyclerView.LayoutManager
                get() = GridLayoutManager(this, 2)
        override val networkState: LiveData<Status>
                get() = MutableLiveData<Status>()
        override val recyclerView: RecyclerView
                get() = mBinding.recyclerView
        override val refreshLayout: SmartRefreshLayout
                get() = mBinding.refreshLayout
        override val refreshState: LiveData<Status>
                get() = MutableLiveData<Status>()

        override fun initStatusLayout() {
                mStatusLayout = mBinding.statusLayout
        }

        override fun initViewModel() {
                mViewModel = ViewModelProviders.of(this).get(BaseViewModel::class.java)
        }

        override fun layoutId(): Int {
                return R.layout.activity_video_edit
        }

        override fun refresh(refreshLayout: RefreshLayout) {

        }

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                addOnClickListener(mBinding.back)
        }

        override fun onClick(v: View) {
                super.onClick(v)
                when (v.id) {
                        R.id.back -> finish()
                }
        }

        override fun initAdapter() {
                mAdapter = VideoEditAdapter(object : BaseRecycleViewAdapter.OnClickListener {
                        override fun onClick(position: Int) {
                                when (position) {
                                        0 -> {
                                                chooseOneVideo()
                                        }

                                        else -> {}
                                }
                        }
                })
        }

        private fun chooseOneVideo() {
                addDisposable(RxPermissions(this)
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe {
                                if (it) {
                                        PictureSelector.create(this)
                                                .openGallery(PictureMimeType.ofVideo())
                                                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                                                .previewVideo(false)// 是否可预览视频 true or false
                                                .isCamera(false)// 是否显示拍照按钮 true or false
//                                                .videoMaxSecond(15)// 显示多少秒以内的视频or音频也可适用 int
//                                                .videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int
                                                .forResult(CHOOSE_ONE_VIDEO)//结果回调onActivityResult code
                                } else {
                                        PermissionDialog.show(supportFragmentManager, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                                }
                        })

        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                super.onActivityResult(requestCode, resultCode, data)
                if (resultCode == RESULT_OK) {
                        when (requestCode) {
                                CHOOSE_ONE_VIDEO -> {
                                        val selectList = PictureSelector.obtainMultipleResult(data)[0]
                                        startActivity<OneVideoEditActivity>(OneVideoEditActivity.TAG_VIDEO_PATH to selectList.path)
                                }
                        }
                }
        }

        companion object {
                const val CHOOSE_ONE_VIDEO = 0
        }
}
