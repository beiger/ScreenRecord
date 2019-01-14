package com.bing.example.videoedit

import android.app.Application
import com.bing.example.app.ScreenRecordApplication
import com.bing.mvvmbase.base.BaseViewModel

class OneVideoEditViewModel(application: Application): BaseViewModel(application) {
        lateinit var editInfo: EditInfo
}