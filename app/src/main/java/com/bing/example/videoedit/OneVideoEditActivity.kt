package com.bing.example.videoedit

import androidx.lifecycle.ViewModelProviders
import com.bing.example.R
import com.bing.mvvmbase.base.BaseActivity
import com.bing.example.databinding.ActivityOneVideoEditBinding
import com.bing.mvvmbase.base.BaseViewModel

class OneVideoEditActivity : BaseActivity<ActivityOneVideoEditBinding, OneVideoEditViewModel>() {

        override fun layoutId(): Int {
                return R.layout.activity_one_video_edit
        }

        override fun initViewModel() {
                mViewModel = ViewModelProviders.of(this).get(OneVideoEditViewModel::class.java)
                val videoPath = intent.getStringExtra("videopath")
                mViewModel.editInfo = EditInfo(videoPath)
        }

        override fun bindAndObserve() {
                mBinding.editInfo = mViewModel.editInfo
        }

}
