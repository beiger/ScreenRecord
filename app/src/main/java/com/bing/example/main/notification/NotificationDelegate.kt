package com.bing.example.main.notification

import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import com.bing.example.module.screenRecord.Notifications
import android.app.NotificationChannel
import android.os.Build.VERSION_CODES.O
import android.os.Build
import com.bing.mvvmbase.base.BaseApplication

object NotificationDelegate {
        val id = 0x1fff
       private val CHANNEL_ID = "Screen Recorder"
        private val CHANNEL_NAME = "Screen Recorder Notifications"

        private val mManager: NotificationManager by lazy {
                val manager = BaseApplication.sContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= O) {
                        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
                        channel.setShowBadge(false)
                        manager.createNotificationChannel(channel)
                }
                manager
        }

        private val notifications: Notifications = Notifications(BaseApplication.sContext, mManager, id, CHANNEL_ID);
        val globalNotification = GlobalNotification(BaseApplication.sContext, mManager, id, CHANNEL_ID);

        fun recording(timeMs: Long) {
                notifications.recording(timeMs)
        }

        fun showGlobal() {
                notifications.clear()
                globalNotification.show()
        }

        fun clearAll() {
                notifications.clear()
                globalNotification.clear()
        }
}