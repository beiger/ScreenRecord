package com.bing.example.videoedit

import android.os.Bundle

import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.bing.example.R
import com.bing.mvvmbase.base.BaseActivity
import com.bing.example.databinding.ActivityOneVideoEditBinding

import com.nightonke.boommenu.BoomButtons.HamButton
import cn.jzvd.Jzvd
import com.bing.example.utils.BitmapUtil

class OneVideoEditActivity : BaseActivity<ActivityOneVideoEditBinding, OneVideoEditViewModel>() {

        override fun layoutId(): Int {
                return R.layout.activity_one_video_edit
        }

        override fun initViewModel() {
                mViewModel = ViewModelProviders.of(this).get(OneVideoEditViewModel::class.java)
                val videoPath = intent.getStringExtra(TAG_VIDEO_PATH)
                mViewModel.editInfo = EditInfo(videoPath?: "")
                mBinding.videoPlayer.setUp(videoPath, "", Jzvd.SCREEN_WINDOW_NORMAL)
                mBinding.videoPlayer.thumbImageView.setImageBitmap(BitmapUtil.createVideoThumbnailLocal(videoPath, 1))
        }

        override fun bindAndObserve() {
                mBinding.editInfo = mViewModel.editInfo
        }

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                addOnClickListener(mBinding.back)
                setSupportActionBar(mBinding.toolbar)
                supportActionBar!!.title = ""
                initFloatingMenu()
        }

        private fun initFloatingMenu() {
                val num = mBinding.bmb.piecePlaceEnum.pieceNumber()
                for (index in 0 until num) {
                        val builder = HamButton.Builder()
                                .normalImageRes(EditType.values()[index].imgRes())
                                .normalTextRes(EditType.values()[index].desText())
                                .subNormalTextRes(EditType.values()[index].subDesText())
                                .normalColorRes(EditType.values()[index].colorRes())
                                .listener {

                                }
                        mBinding.bmb.addBuilder(builder)
                }
        }

        override fun onClick(v: View) {
                when (v.id) {
                        R.id.back -> finish()
                }
        }

        override fun onBackPressed() {
                if (Jzvd.backPress()) {
                        return
                }
                super.onBackPressed()
        }

        override fun onPause() {
                super.onPause()
                Jzvd.releaseAllVideos()
        }

        companion object {
                const val TAG_VIDEO_PATH = "video_path"
        }
}
