package com.bing.example.main.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews

import com.bing.example.R
import com.bing.mvvmbase.base.BaseApplication

import android.os.Build.VERSION_CODES.O
import com.bing.example.main.home.MainActivity.Companion.ACTION_EXIT_APP
import com.bing.example.main.home.MainActivity.Companion.ACTION_RECORD
import com.bing.example.main.home.MainActivity.Companion.ACTION_VIDEO_LIST

class GlobalNotification(context: Context, val manager: NotificationManager, val notificationID: Int, val channelID: String) : ContextWrapper(context) {

        private val remoteViews: RemoteViews by lazy {
                val view = RemoteViews(BaseApplication.sContext.packageName, R.layout.layout_notification_global)
                setCommonClickPending(view)
                view
        }

        private val builder: Notification.Builder by lazy {
                val field = Notification.Builder(this)
                        .setOngoing(true)
                        .setLocalOnly(true)
                        .setOnlyAlertOnce(true)
                        .setWhen(System.currentTimeMillis())
                        .setContent(remoteViews)
                        .setSmallIcon(R.drawable.ic_stat_recording)
                if (Build.VERSION.SDK_INT >= O) {
                        field!!.setChannelId(channelID)
                                .setUsesChronometer(true)
                }
                field
        }

        fun show() {
                val notification = builder.build()
                manager.notify(notificationID, notification)
        }

        // 播放或暂停，下一曲，关闭
        private fun setCommonClickPending(view: RemoteViews) {
                val intentStartRecord = Intent(ACTION_RECORD).setPackage(packageName)
                val pendingIntent1 = PendingIntent.getBroadcast(this, 1,
                        intentStartRecord, PendingIntent.FLAG_UPDATE_CURRENT)
                view.setOnClickPendingIntent(R.id.start_record, pendingIntent1)

                val intentVideoList = Intent(ACTION_VIDEO_LIST).setPackage(packageName)
                val pendingIntent2 = PendingIntent.getBroadcast(this, 2,
                        intentVideoList, PendingIntent.FLAG_UPDATE_CURRENT)
                view.setOnClickPendingIntent(R.id.list, pendingIntent2)

                val intentExitApp = Intent(ACTION_EXIT_APP).setPackage(packageName)
                val pendingIntent3 = PendingIntent.getBroadcast(this, 3,
                        intentExitApp, PendingIntent.FLAG_UPDATE_CURRENT)
                view.setOnClickPendingIntent(R.id.exit, pendingIntent3)
        }

        fun clear() {
                manager.cancelAll()
        }
}
