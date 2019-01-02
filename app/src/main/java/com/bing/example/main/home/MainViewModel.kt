package com.bing.example.main.home

import android.app.Application

import com.alibaba.fastjson.JSON
import com.bing.example.module.screenRecord.AudioEncodeConfig
import com.bing.example.module.screenRecord.VideoEncodeConfig
import com.bing.example.module.screenRecord.VideoEncodeConfigParcelable
import com.bing.example.utils.Constant
import com.bing.mvvmbase.base.AppExecutors
import com.bing.mvvmbase.base.BaseViewModel
import com.blankj.utilcode.util.FileIOUtils
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.bing.example.app.globalAudioConfig
import com.bing.example.app.globalVideoConfig

class MainViewModel(application: Application) : BaseViewModel(application) {
        val videoEncodeConfigLive = MutableLiveData<VideoEncodeConfig>()
        var videoEncodeConfig: VideoEncodeConfig? = null
                set(value) {
                        field = value
                        globalVideoConfig = value
                }
        val audioEncodeConfigLive = MutableLiveData<AudioEncodeConfig>()
        var audioEncodeConfig: AudioEncodeConfig? = null
                set(value) {
                        field = value
                        globalAudioConfig = value
                }

        val isNormalMode = ObservableBoolean(true)

        init {
                AppExecutors.diskIO.execute { this.getConfigFromLocal() }
        }

        private fun getConfigFromLocal() {
                val vcString = FileIOUtils.readFile2String(VIDEO_CONFIG_PATH)
                if (vcString != null) {
                        val videoEncodeConfigParcelable = JSON.parseObject(vcString, VideoEncodeConfigParcelable::class.java)
                        videoEncodeConfig = videoEncodeConfigParcelable.toConfig()
                } else {
                        VideoEncodeConfig.getDefault(videoEncodeConfigLive)
                }
                val acString = FileIOUtils.readFile2String(AUDIO_CONFIG_PATH)
                if (acString != null) {
                        audioEncodeConfig = JSON.parseObject(acString, AudioEncodeConfig::class.java)
                } else {
                        AudioEncodeConfig.getDefault(audioEncodeConfigLive)
                }
        }

        fun writeConfigToFile() {
                AppExecutors.diskIO.execute {
                        if (videoEncodeConfig != null) {
                                val vcString = JSON.toJSONString(videoEncodeConfig!!.toParcelable())
                                FileIOUtils.writeFileFromString(VIDEO_CONFIG_PATH, vcString)
                        }
                        if (audioEncodeConfig != null) {
                                val acString = JSON.toJSONString(audioEncodeConfig)
                                FileIOUtils.writeFileFromString(AUDIO_CONFIG_PATH, acString)
                        }
                }
        }

        override fun onDestroy(owner: LifecycleOwner) {
                writeConfigToFile()
                super.onDestroy(owner)
        }

        companion object {
                private val VIDEO_CONFIG_PATH = Constant.FILE_DIR + "videoConfig.json"
                private val AUDIO_CONFIG_PATH = Constant.FILE_DIR + "audioConfig.json"
        }
}
