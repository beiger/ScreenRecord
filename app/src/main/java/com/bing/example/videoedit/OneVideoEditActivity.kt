package com.bing.example.videoedit

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Gravity
import androidx.lifecycle.ViewModelProviders
import com.bing.example.R
import com.bing.mvvmbase.base.BaseActivity
import com.bing.example.databinding.ActivityOneVideoEditBinding
<<<<<<< HEAD
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionButton.SIZE_NORMAL
=======
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SizeUtils
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
>>>>>>> video edit

class OneVideoEditActivity : BaseActivity<ActivityOneVideoEditBinding, OneVideoEditViewModel>() {

        override fun layoutId(): Int {
                return R.layout.activity_one_video_edit
        }

        override fun initViewModel() {
                mViewModel = ViewModelProviders.of(this).get(OneVideoEditViewModel::class.java)
<<<<<<< HEAD
                val videoPath = intent.getStringExtra("videopath")
=======
                val videoPath = intent.getStringExtra(TAG_VIDEO_PATH)
>>>>>>> video edit
                mViewModel.editInfo = EditInfo(videoPath?: "")
        }

        override fun bindAndObserve() {
                mBinding.editInfo = mViewModel.editInfotFloatingMenu()
        }

<<<<<<< HEAD
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
=======
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setSupportActionBar(mBinding.toolbar)
                supportActionBar!!.title = ""
                initFloatingMenu()
        }

        private fun initFloatingMenu() {
                val num = mBinding.bmb.piecePlaceEnum.pieceNumber()
                for (index in 0 until num) {
                        val builder = TextInsideCircleButton.Builder()
                                .normalImageRes(EditType.values()[index].imgRes())
                                .normalTextRes(EditType.values()[index].desText())
                                .normalColorRes(EditType.values()[index].colorRes())
                                .buttonRadius(SizeUtils.dp2px(32f))
                                .buttonCornerRadius(SizeUtils.dp2px(32f))
                                .textGravity(Gravity.CENTER)
                                .listener {

                                }
                        mBinding.bmb.addBuilder(builder)
                }
        }

        companion object {
                const val TAG_VIDEO_PATH = "video_path"
>>>>>>> video edit
        }
}
