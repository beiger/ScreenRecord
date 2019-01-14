package com.bing.example.videoedit

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.bing.example.BR
import com.bing.example.R
import com.bing.example.utils.Constant
import java.text.SimpleDateFormat
import java.util.*

class EditInfo(originVideoPath: String, editType: EditType? = null): BaseObservable() {
        val originVideoPath: String = originVideoPath

        @Bindable
        var editType: EditType? = editType
        set(value) {
                field = value
                notifyPropertyChanged(BR.editType)
        }

        private val newVideoPath: String
        init {
               newVideoPath = initNewPath()
        }

        private fun initNewPath(): String {
                val format = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US)
                val filename = format.format(Date()) + "_edited.mp4"
                return Constant.VIDEO_DIR + filename
        }
}

enum class EditType {
        CLIP {
                override fun imgRes(): Int {
                        return R.drawable.abc_ic_clear_material
                }

                override fun desText(): Int {
                        return R.string.clip
                }

                override fun subDesText(): Int {
                        return R.string.choose_video_to_save
                }

                override fun subDesText(): Int {
                        return R.string.fgh_text_loading
                }

                override fun colorRes(): Int {
                        return R.color.color17
                }
        },
        DELETE {
                override fun imgRes(): Int {
                        return R.drawable.ic_about
                }

                override fun desText(): Int {
                        return R.string.delete
                }

                override fun subDesText(): Int {
                        return R.string.choose_video_to_delete
                }

                override fun subDesText(): Int {
                        return R.string.fgh_text_loading
                }

                override fun colorRes(): Int {
                        return R.color.color20
                }
        },
        BACKGROUND {
                override fun imgRes(): Int {
                        return R.drawable.ic_about
                }

                override fun desText(): Int {
                        return R.string.background
                }

                override fun subDesText(): Int {
                        return R.string.choose_img_as_bg
                }

                override fun subDesText(): Int {
                        return R.string.fgh_text_loading
                }

                override fun colorRes(): Int {
                        return R.color.color18
                }
        };

        abstract fun imgRes(): Int
        abstract fun desText(): Int
        abstract fun subDesText(): Int
        abstract fun colorRes(): Int
}