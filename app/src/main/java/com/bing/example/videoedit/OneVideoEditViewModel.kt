package com.bing.example.videoedit

import com.bing.example.app.ScreenRecordApplication
import com.bing.mvvmbase.base.BaseViewModel

class OneVideoEditViewModel(application: ScreenRecordApplication): BaseViewModel(application) {
        lateinit var editInfo: EditInfo
}