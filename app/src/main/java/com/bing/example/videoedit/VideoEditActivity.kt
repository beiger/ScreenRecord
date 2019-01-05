package com.bing.example.videoedit

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bing.example.R
import com.bing.example.databinding.ActivityVideoEditBinding
import com.bing.mvvmbase.base.BaseViewModel
import com.bing.mvvmbase.base.recycleview.BaseRecycleViewActivity
import com.bing.mvvmbase.base.recycleview.BaseRecycleViewAdapter
import com.bing.mvvmbase.model.datawrapper.Status
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout

class VideoEditActivity : BaseRecycleViewActivity<ActivityVideoEditBinding, BaseViewModel, VideoEditAdapter, VideoEditType>() {
        override val data: LiveData<List<VideoEditType>>
                get() {
                        val list = listOf(VideoEditType("jianqie"))
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

        override fun initAdapter() {
                mAdapter = VideoEditAdapter(object : BaseRecycleViewAdapter.OnClickListener {
                        override fun onClick(position: Int) {

                        }
                })
        }

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

}
