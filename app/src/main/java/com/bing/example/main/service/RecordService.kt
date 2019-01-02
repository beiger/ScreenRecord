package com.bing.example.main.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.bing.example.main.home.MainActivity
import com.bing.example.main.notification.NotificationDelegate
import com.bing.example.utils.collapseStatusBar
import com.blankj.utilcode.util.AppUtils

class RecordService : Service() {
        private val mNotificationDelegate: NotificationDelegate by lazy {
                NotificationDelegate(this)
        }
        private val mGlobalControlReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                        when (intent.action) {
                                MainActivity.ACTION_RECORD -> {
                                        collapseStatusBar(this@RecordService)
                                        mRecordHelper.onRecordButtonClick()
                                }
                                MainActivity.ACTION_VIDEO_LIST -> {
                                        collapseStatusBar(this@RecordService)
                                        val _intent = Intent(this@RecordService, MainActivity::class.java)
                                        _intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                                        startActivity(_intent)
                                }
                                MainActivity.ACTION_EXIT_APP -> {
                                        mNotificationDelegate.clearAll()
                                        collapseStatusBar(this@RecordService)
                                        AppUtils.exitApp()
                                }
                        }
                }
        }

        override fun onBind(intent: Intent): IBinder? {
                return null
        }

        override fun onCreate() {
                super.onCreate()
                initBroadcastRecieve()
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
                val globalNotification = mNotificationDelegate.globalNotification.builder.build()
                startForeground(mNotificationDelegate.id, globalNotification)
                return super.onStartCommand(intent, flags, startId)
        }

        private fun initBroadcastRecieve() {
                val intentFilter = IntentFilter()
                intentFilter.addAction(MainActivity.ACTION_RECORD)
                intentFilter.addAction(MainActivity.ACTION_VIDEO_LIST)
                intentFilter.addAction(MainActivity.ACTION_EXIT_APP)
                registerReceiver(mGlobalControlReceiver, intentFilter)
                mNotificationDelegate.showGlobal()
        }

        override fun onDestroy() {
                stopForeground(true)
                super.onDestroy()
        }
}
