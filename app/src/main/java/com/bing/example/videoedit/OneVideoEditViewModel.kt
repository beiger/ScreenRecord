package com.bing.example.videoedit

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.bing.example.app.ScreenRecordApplication
import com.bing.mvvmbase.base.BaseViewModel

class OneVideoEditViewModel(application: Application): BaseViewModel(application) {
        lateinit var editInfo: EditInfo
        val videoLength = MutableLiveData<Long>()

        fun initVideoLength() {

        }

}