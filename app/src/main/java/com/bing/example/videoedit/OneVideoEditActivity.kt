package com.bing.example.videoedit

import androidx.lifecycle.ViewModelProviders
import com.bing.example.R
import com.bing.mvvmbase.base.BaseActivity
import com.bing.example.databinding.ActivityOneVideoEditBinding
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionButton.SIZE_NORMAL

class OneVideoEditActivity : BaseActivity<ActivityOneVideoEditBinding, OneVideoEditViewModel>() {

        override fun layoutId(): Int {
                return R.layout.activity_one_video_edit
        }

        override fun initViewModel() {
                mViewModel = ViewModelProviders.of(this).get(OneVideoEditViewModel::class.java)
                val videoPath = intent.getStringExtra("videopath")
                mViewModel.editInfo = EditInfo(videoPath?: "")
        }

        override fun bindAndObserve() {
                mBinding.editInfo = mViewModel.editInfotFloatingMenu()
        }

        fun initFloatingMenu() {
                val button0 = FloatingActionButton(this)
                button0.buttonSize = SIZE_NORMAL
                button0.labelText = "hahaha"
                mBinding.menu.addMenuButton(button0, 0)

                val button1 = FloatingActionButton(this)
                button1.buttonSize = SIZE_NORMAL
                button0.labelText = "hahaha"
                mBinding.menu.addMenuButton(button1, 1)


                val button2 = FloatingActionButton(this)
                button2.buttonSize = SIZE_NORMAL
                button0.labelText = "hahaha"
                mBinding.menu.addMenuButton(button2, 2)

                mBinding.menu.setClosedOnTouchOutside(true)
        }
}
