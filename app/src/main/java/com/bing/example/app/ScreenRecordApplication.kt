package com.bing.example.app

import com.beardedhen.androidbootstrap.TypefaceProvider
import com.bing.mvvmbase.base.BaseApplication

class ScreenRecordApplication : BaseApplication() {
        override fun onCreate() {
                super.onCreate()
                TypefaceProvider.registerDefaultIconSets()
        }
}
