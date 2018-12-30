package com.bing.example.main.videolist

import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.dinus.com.itemdecoration.GridOffsetsItemDecoration
import cn.jzvd.JzvdStd

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input

import com.bing.example.R
import com.bing.example.databinding.FragmentVideoListBinding
import com.bing.example.main.home.MainViewModel
import com.bing.example.model.RepositoryManager
import com.bing.example.model.entity.VideoInfo
import com.bing.mvvmbase.base.recycleview.BaseRecycleViewFragment
import com.bing.mvvmbase.model.datawrapper.Status
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import com.yarolegovich.lovelydialog.LovelyTextInputDialog

import java.io.File

import com.bing.example.main.videolist.VideosAdapter.Companion.MODE_SELECT

class VideoListFragment : BaseRecycleViewFragment<FragmentVideoListBinding, VideoListViewModel, MainViewModel, VideosAdapter, VideoInfo>() {
        override fun initActivityViewModel() {
                mActivityViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        }

        override fun initViewModel() {
                mViewModel = ViewModelProviders.of(this).get(VideoListViewModel::class.java)
        }


        override val refreshLayout: SmartRefreshLayout
                get() = mBinding.refreshLayout

        override val recyclerView: RecyclerView
                get() = mBinding.recyclerView

        override val layoutManager: RecyclerView.LayoutManager
                get() = GridLayoutManager(context, 2)

        override val networkState: MutableLiveData<Status>
                get() = mViewModel.loadStatus

        override val refreshState: MutableLiveData<Status>
                get() = mViewModel.loadStatus

        override val data: LiveData<List<VideoInfo>>
                get() = mViewModel.videoInfos

        override fun handleArguments() {

        }

        override fun layoutId(): Int {
                return R.layout.fragment_video_list
        }

        override fun initRefreshLayout() {
                super.initRefreshLayout()
                refreshLayout.setEnableRefresh(false)
        }

        override fun refresh(refreshLayout: RefreshLayout) {

        }

        override fun initRecycleView() {
                super.initRecycleView()
                val offsetsItemDecoration = GridOffsetsItemDecoration(
                        GridOffsetsItemDecoration.GRID_OFFSETS_VERTICAL)
                offsetsItemDecoration.setVerticalItemOffsets(SizeUtils.dp2px(8f))
                offsetsItemDecoration.setHorizontalItemOffsets(SizeUtils.dp2px(8f))
                mRecyclerView.addItemDecoration(offsetsItemDecoration)
        }

        override fun initAdapter() {
                mAdapter = VideosAdapter(object : VideosAdapter.OnClickListener {
                        override fun onClickImage(position: Int, videoInfo: VideoInfo) {
                                val fileName = videoInfo.path
                                if (FileUtils.isFileExists(fileName)) {
                                        JzvdStd.startFullscreen(context, JzvdStd::class.java, videoInfo.path, videoInfo.title)
                                } else {
                                        ToastUtils.showShort(R.string.file_not_exits)
                                }
                        }

                        override fun onClickRename(position: Int, videoInfo: VideoInfo) {
                                MaterialDialog(context!!).show {
                                        message(R.string.rename)
                                        input { _, text ->
                                                val videoInfo1 = videoInfo.copy()
                                                videoInfo1.title = text.toString()
                                                RepositoryManager.instance().updateVideo(videoInfo1)
                                        }
                                        positiveButton(android.R.string.ok)
                                }
                        }

                        override fun onClickDelete(position: Int, videoInfo: VideoInfo) {
                                MaterialDialog(context!!).show {
                                        message(R.string.sure_delete)
                                        positiveButton(android.R.string.ok) {
                                                mViewModel.deleteVideo(videoInfo)
                                        }
                                        negativeButton(android.R.string.no) { }
                                }
                        }

                        override fun onClickShare(position: Int, videoInfo: VideoInfo) {
                                val uri = FileProvider.getUriForFile(context!!, "com.bing.example.fileprovider", File(videoInfo.path))

                                val shareIntent = Intent()
                                shareIntent.action = Intent.ACTION_SEND            //分享视频只能单个分享
                                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                                shareIntent.type = "audio/*"
                                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)))
                        }

                        override fun onClickFore(position: Int, selectedNumber: Int) {

                        }
                }, object : VideosAdapter.OnModeChangeListener {
                        override fun onModeTo(mode: Int) {
                                if (mode == VideosAdapter.MODE_NORMAL) {
                                        mActivityViewModel.isNormalMode.set(true)
                                } else {
                                        mActivityViewModel.isNormalMode.set(false)
                                }
                        }
                })
        }

        fun onClickBack() {
                mAdapter.intoNormalMode()
                mActivityViewModel.isNormalMode.set(true)
        }

        fun onClickDelete() {
                mViewModel.deleteVideos(mAdapter.selectedData)
                mAdapter.intoNormalMode()
                mActivityViewModel.isNormalMode.set(true)
        }

        fun onClickSelectAll() {
                if (mAdapter.selectedPostions.size == mAdapter.itemCount) {
                        mAdapter.selectedPostions.clear()
                } else {
                        for (i in 0 until mAdapter.data!!.size) {
                                mAdapter.selectedPostions.add(i)
                        }
                }
                mAdapter.notifyDataSetChanged()
        }

        fun onBackPressed(): Boolean {
                if (mAdapter.currentMode == MODE_SELECT) {
                        mAdapter.intoNormalMode()
                        return true
                } else {
                        return false
                }
        }

        fun intoNormalMode() {
                if (mAdapter.currentMode == MODE_SELECT) {
                        mAdapter.intoNormalMode()
                }
        }

        override fun initStatusLayout() {
                mStatusLayout = mBinding.statusLayout
        }
}
