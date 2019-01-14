package com.bing.example.videoedit

import android.os.Bundle
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.bing.example.R
import com.bing.mvvmbase.base.BaseActivity
import com.bing.example.databinding.ActivityOneVideoEditBinding

import com.nightonke.boommenu.BoomButtons.HamButton
import cn.jzvd.Jzvd
import com.beardedhen.androidbootstrap.BootstrapButton
import com.bing.example.utils.BitmapUtil
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar

class OneVideoEditActivity : BaseActivity<ActivityOneVideoEditBinding, OneVideoEditViewModel>() {
        private lateinit var operationContainer: ViewGroup
        private val opViews = Array<View?>(3) {null}
        private val onRangeChangedListener = object : OnRangeChangedListener{
                override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

                }

                override fun onRangeChanged(view: RangeSeekBar?, leftValue: Float, rightValue: Float, isFromUser: Boolean) {

                }

                override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

                }
        }

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
                operationContainer = mBinding.operationContainer
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
                                        onEditTypeChanged(EditType.values()[it])
                                }
                        mBinding.bmb.addBuilder(builder)
                }
        }

        private fun onEditTypeChanged(type: EditType) {
                when (type) {
                        EditType.CLIP -> {
                                if (opViews[0] == null) {
                                        val view = LayoutInflater.from(this).inflate(R.layout.edit_type_clip, null)
                                        view.findViewById<BootstrapButton>(R.id.previewButton).setOnClickListener {

                                        }
                                        view.findViewById<BootstrapButton>(R.id.clipButton).setOnClickListener {

                                        }
                                        view.findViewById<RangeSeekBar>(R.id.rangeSeekBar).setOnRangeChangedListener(onRangeChangedListener)
                                        opViews[0] = view
                                }
                                operationContainer.removeAllViews()
                                operationContainer.addView(opViews[0])
                        }

                        EditType.DELETE -> {
                                if (opViews[1] == null) {
                                        val view = LayoutInflater.from(this).inflate(R.layout.edit_type_delete, null)
                                        view.findViewById<BootstrapButton>(R.id.previewButton).setOnClickListener {

                                        }
                                        view.findViewById<BootstrapButton>(R.id.deleteButton).setOnClickListener {

                                        }
                                        view.findViewById<RangeSeekBar>(R.id.rangeSeekBar).setOnRangeChangedListener(onRangeChangedListener)
                                        opViews[1] = view
                                }
                                operationContainer.removeAllViews()
                                operationContainer.addView(opViews[1])
                        }

                        EditType.BACKGROUND -> {
                                if (opViews[2] == null) {
                                        val view = LayoutInflater.from(this).inflate(R.layout.edit_type_background, null)
                                        view.findViewById<BootstrapButton>(R.id.selectButton).setOnClickListener {

                                        }
                                        view.findViewById<BootstrapButton>(R.id.previewButton).setOnClickListener {

                                        }
                                        view.findViewById<BootstrapButton>(R.id.insertButton).setOnClickListener {

                                        }
                                        view.findViewById<RangeSeekBar>(R.id.rangeSeekBar).setOnRangeChangedListener(onRangeChangedListener)
                                        opViews[2] = view
                                }
                                operationContainer.removeAllViews()
                                operationContainer.addView(opViews[2])
                        }
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
