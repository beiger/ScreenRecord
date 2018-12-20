package com.bing.example.main.home

import android.media.projection.MediaProjectionManager
import androidx.appcompat.app.AppCompatActivity
import com.bing.example.module.screenRecord.Notifications

class RecordHelper(val activity: AppCompatActivity) {
        lateinit var mMediaProjectionManager: MediaProjectionManager
        lateinit var mNotifications: Notifications
}