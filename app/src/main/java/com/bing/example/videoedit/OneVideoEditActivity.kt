package com.bing.example.videoedit

import androidx.lifecycle.ViewModelProviders
import com.bing.example.R
import com.bing.mvvmbase.base.BaseActivity
import com.bing.example.databinding.ActivityOneVideoEditBinding
import com.bing.mvvmbase.base.BaseViewModel

class OneVideoEditActivity : BaseActivity<ActivityOneVideoEditBinding, BaseViewModel>() {

        override fun bindAndObserve() {

        }

        override fun initViewModel() {
                mViewModel = ViewModelProviders.of(this).get(BaseViewModel::class.java)
        }

        override fun layoutId(): Int {
                return R.layout.activity_one_video_edit
        }

}
