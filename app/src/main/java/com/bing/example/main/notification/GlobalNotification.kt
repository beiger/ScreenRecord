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
import com.bing.example.main.home.MainActivity.Companion.ACTION_START

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
                val intentStartRecord = Intent(ACTION_START).setPackage(packageName)
                val pendingIntent = PendingIntent.getBroadcast(this, 1,
                        intentStartRecord, PendingIntent.FLAG_ONE_SHOT)
                view.setOnClickPendingIntent(R.id.start_record, pendingIntent)
        }

        fun clear() {
                manager.cancelAll()
        }
}
