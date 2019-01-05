package com.bing.example.search

import android.content.Intent
import android.view.View
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
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
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_search.*
import java.io.File
import java.util.concurrent.TimeUnit

class SearchActivity : BaseActivity<ActivitySearchBinding, SearchViewModel>() {
        private val mRecyclerView: RecyclerView
                get() =  mBinding.recyclerView
        private lateinit var mAdapter: VideosAdapter

        override fun bindAndObserve() {
                initRecycleView()
                initAdapter()
                addOnClickListener(mBinding.back, mBinding.ivSearch)
                addDisposable(
                        RxTextView.textChanges(searchText)
                                .debounce(400, TimeUnit.MILLISECONDS)
                                .subscribeOn(AndroidSchedulers.mainThread())
//                                .filter{it.isNotEmpty()}
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe{ s ->
                                        val result = mViewModel.videoInfos.filter {
                                                if (s.isEmpty()) {
                                                        false
                                                } else {
                                                        it.title?.contains(s) ?: false
                                                }
                                        }
                                        mAdapter.updateData(result)
                                }
                )
        }

        override fun initViewModel() {
                mViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        }

        override fun layoutId(): Int {
                return R.layout.activity_search
        }

        private fun initRecycleView() {
                val offsetsItemDecoration = GridOffsetsItemDecoration(
                        GridOffsetsItemDecoration.GRID_OFFSETS_VERTICAL)
                offsetsItemDecoration.setVerticalItemOffsets(SizeUtils.dp2px(8f))
                offsetsItemDecoration.setHorizontalItemOffsets(SizeUtils.dp2px(8f))
                mRecyclerView.addItemDecoration(offsetsItemDecoration)
                mRecyclerView.layoutManager = GridLayoutManager(this, 2)
        }

        private fun initAdapter() {
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
                mRecyclerView.adapter = mAdapter
        }

        override fun onClick(v: View) {
                when (v.id) {
                        R.id.back  -> finish()

                        R.id.ivSearch -> {}
                }
        }

//        override fun finish() {
//                super.finish()
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//        }

}
