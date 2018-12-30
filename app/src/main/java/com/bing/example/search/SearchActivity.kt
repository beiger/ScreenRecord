package com.bing.example.search

import android.content.Intent
import android.view.View
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.dinus.com.itemdecoration.GridOffsetsItemDecoration
import cn.jzvd.JzvdStd
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.bing.example.R
import com.bing.mvvmbase.base.BaseActivity
import com.bing.example.databinding.ActivitySearchBinding
import com.bing.example.model.RepositoryManager
import com.bing.example.model.entity.VideoInfo
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import java.io.File

class SearchActivity : BaseActivity<ActivitySearchBinding, SearchViewModel>() {
        private val mRecyclerView: RecyclerView
                get() =  mBinding.recyclerView
        private lateinit var mAdapter: VideosAdapter

        override fun bindAndObserve() {
                initRecycleView()
                initAdapter()
                addOnClickListener(mBinding.back, mBinding.ivSearch)
        }

        override fun initViewModel() {
                mViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        }

        override fun layoutId(): Int {
                return R.layout.activity_search
        }

        fun initRecycleView() {
                val offsetsItemDecoration = GridOffsetsItemDecoration(
                        GridOffsetsItemDecoration.GRID_OFFSETS_VERTICAL)
                offsetsItemDecoration.setVerticalItemOffsets(SizeUtils.dp2px(8f))
                offsetsItemDecoration.setHorizontalItemOffsets(SizeUtils.dp2px(8f))
                mRecyclerView.addItemDecoration(offsetsItemDecoration)
                mRecyclerView.layoutManager = GridLayoutManager(this, 2)
        }

        fun initAdapter() {
                mAdapter = VideosAdapter(object : VideosAdapter.OnClickListener {
                        override fun onClickImage(position: Int, videoInfo: VideoInfo) {
                                val fileName = videoInfo.path
                                if (FileUtils.isFileExists(fileName)) {
                                        JzvdStd.startFullscreen(this@SearchActivity, JzvdStd::class.java, videoInfo.path, videoInfo.title)
                                } else {
                                        ToastUtils.showShort(R.string.file_not_exits)
                                }
                        }

                        override fun onClickRename(position: Int, videoInfo: VideoInfo) {
                                MaterialDialog(this@SearchActivity).show {
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
                                MaterialDialog(this@SearchActivity).show {
                                        message(R.string.sure_delete)
                                        positiveButton(android.R.string.ok) {
                                                mViewModel.deleteVideo(videoInfo)
                                        }
                                        negativeButton(android.R.string.no) { }
                                }
                        }

                        override fun onClickShare(position: Int, videoInfo: VideoInfo) {
                                val uri = FileProvider.getUriForFile(this@SearchActivity, "com.bing.example.fileprovider", File(videoInfo.path))

                                val shareIntent = Intent()
                                shareIntent.action = Intent.ACTION_SEND            //分享视频只能单个分享
                                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                                shareIntent.type = "audio/*"
                                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)))
                        }
                })
                mViewModel.videoInfos.observe(this, Observer {
                        mAdapter.updateData(it)
                })
                mRecyclerView.adapter = mAdapter
        }

        override fun onClick(v: View) {
                when (v.id) {
                        R.id.back  -> finish()

                        R.id.ivSearch -> {}
                }
        }

}
