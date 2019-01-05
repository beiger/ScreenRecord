package com.bing.example.main.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.bing.example.main.home.MainActivity
import com.bing.example.main.home.RecordHelper
import com.bing.example.main.home.RecordState
import com.bing.example.main.notification.NotificationDelegate
import com.bing.example.utils.collapseStatusBar
import com.blankj.utilcode.util.AppUtils
import org.jetbrains.anko.startActivity

class RecordService : Service() {

        private val mGlobalControlReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                        when (intent.action) {
                                ACTION_RECORD -> {
                                        collapseStatusBar(this@RecordService)
                                         if (RecordHelper.recordState == RecordState.NOT_RECORD) {
                                                startActivity<RecordActivity>()
                                        } else {
                                                RecordHelper.stopRecorder()
                                        }
                                }
                                ACTION_VIDEO_LIST -> {
                                        collapseStatusBar(this@RecordService)
                                        val _intent = Intent(this@RecordService, MainActivity::class.java)
                                        _intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                                        startActivity(_intent)
                                }
                                ACTION_EXIT_APP -> {
                                        NotificationDelegate.clearAll()
                                        collapseStatusBar(this@RecordService)
                                        stopSelf()
                                        AppUtils.exitApp()
                                }
                                ACTION_STOP -> {
                                        RecordHelper.stopRecorder()
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
                val globalNotification = NotificationDelegate.globalNotification.builder.build()
                startForeground(NotificationDelegate.id, globalNotification)
                return super.onStartCommand(intent, flags, startId)
        }

        private fun initBroadcastRecieve() {
                val intentFilter = IntentFilter()
                intentFilter.addAction(ACTION_RECORD)
                intentFilter.addAction(ACTION_STOP)
                intentFilter.addAction(ACTION_VIDEO_LIST)
                intentFilter.addAction(ACTION_EXIT_APP)
                registerReceiver(mGlobalControlReceiver, intentFilter)
                NotificationDelegate.showGlobal()
        }

        override fun onDestroy() {
                stopForeground(true)
                super.onDestroy()
        }
        companion object {
                const val ACTION_STOP = "net.yrom.screenrecorder.action.STOP"
                const val ACTION_RECORD = "net.yrom.screenrecorder.action.RECORD"
                const val ACTION_VIDEO_LIST = "net.yrom.screenrecorder.action.VIDEO_LIST"
                const val ACTION_EXIT_APP = "net.yrom.screenrecorder.action.EXIT_APP"
        }
}
