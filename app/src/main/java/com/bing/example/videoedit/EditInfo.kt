package com.bing.example.videoedit

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.bing.example.BR
import com.bing.example.R
import com.bing.example.utils.Constant
import java.text.SimpleDateFormat
import java.util.*

class EditInfo(originVideoPath: String, editType: EditType? = null): BaseObservable() {
        private val originVideoPath: String = originVideoPath

        @Bindable
        private var editType: EditType? = editType
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
        JIANQIE {
                override fun imgRes(): Int {
                        return R.drawable.ic_about
                }

                override fun desText(): Int {
                        return R.string.fgh_text_loading
                }

                override fun colorRes(): Int {
                        return R.color.color11
                }
        },
        SHANCHU {
                override fun imgRes(): Int {
                        return R.drawable.ic_about
                }

                override fun desText(): Int {
                        return R.string.fgh_text_loading
                }

                override fun colorRes(): Int {
                        return R.color.color11
                }
        },
        BEIJING {
                override fun imgRes(): Int {
                        return R.drawable.ic_about
                }

                override fun desText(): Int {
                        return R.string.fgh_text_loading
                }

                override fun colorRes(): Int {
                        return R.color.color11
                }
        };

        abstract fun imgRes(): Int
        abstract fun desText(): Int
        abstract fun colorRes(): Int
}