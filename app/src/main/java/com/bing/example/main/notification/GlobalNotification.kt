package com.bing.example.main.notification

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.text.format.DateUtils
import android.widget.RemoteViews

import com.bing.example.R
import com.bing.mvvmbase.base.BaseApplication
import com.blankj.utilcode.util.LogUtils

import android.os.Build.VERSION_CODES.O
import com.bing.example.main.home.RecordHelper.Companion.ACTION_STOP

class GlobalNotification(context: Context) : ContextWrapper(context) {

        private var mLastFiredTime: Long = 0
        private var mTime: String? = null
        private var mManager: NotificationManager? = null
        private var mStopAction: Notification.Action? = null
        private var mBuilder: Notification.Builder? = null
        private var mRemoteViews: RemoteViews? = null

        private val builder: Notification.Builder?
                get() {
                        if (mBuilder == null) {
                                val builder = Notification.Builder(this)
                                        .setContentTitle("Recording...")
                                        .setOngoing(true)
                                        .setLocalOnly(true)
                                        .setOnlyAlertOnce(true)
                                        .addAction(stopAction())
                                        .setWhen(System.currentTimeMillis())
                                        .setContent(mRemoteViews)
                                        .setSmallIcon(R.drawable.ic_stat_recording)
                                if (Build.VERSION.SDK_INT >= O) {
                                        builder.setChannelId(CHANNEL_ID)
                                                .setUsesChronometer(true)
                                }
                                mBuilder = builder
                        }
                        return mBuilder
                }

        internal val notificationManager: NotificationManager?
                get() {
                        if (mManager == null) {
                                mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        }
                        return mManager
                }

        init {
                if (Build.VERSION.SDK_INT >= O) {
                        createNotificationChannel()
                }
        }

        fun recording(timeMs: Long) {
                if (SystemClock.elapsedRealtime() - mLastFiredTime < 1000) {
                        return
                }
                mTime = DateUtils.formatElapsedTime(timeMs / 1000)
                if (mRemoteViews == null || timeMs == 0L) {
                        mRemoteViews = createContentView()
                }
                mRemoteViews!!.setTextViewText(R.id.time, mTime)
                LogUtils.i("recording:" + mTime!!)
                val notification = builder!!.build()
                notificationManager!!.notify(id, notification)
                mLastFiredTime = SystemClock.elapsedRealtime()
        }

        @TargetApi(O)
        private fun createNotificationChannel() {
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
                channel.setShowBadge(false)
                notificationManager!!.createNotificationChannel(channel)
        }

        private fun stopAction(): Notification.Action {
                if (mStopAction == null) {
                        val intent = Intent(ACTION_STOP).setPackage(packageName)
                        val pendingIntent = PendingIntent.getBroadcast(this, 1,
                                intent, PendingIntent.FLAG_ONE_SHOT)
                        mStopAction = Notification.Action(android.R.drawable.ic_media_pause, "Stop", pendingIntent)
                }
                return mStopAction
        }

        private fun createContentView(): RemoteViews {
                val view = RemoteViews(BaseApplication.sContext.packageName, R.layout.layout_notification)
                setCommonView(view)
                setCommonClickPending(view)
                return view
        }

        // 图片，歌名，艺术家，播放按钮，下一曲按钮，关闭按钮
        private fun setCommonView(view: RemoteViews) {
                view.setTextViewText(R.id.time, mTime)
        }

        // 播放或暂停，下一曲，关闭
        private fun setCommonClickPending(view: RemoteViews) {
                val intent = Intent(ACTION_STOP).setPackage(packageName)
                val pendingIntent = PendingIntent.getBroadcast(this, 1,
                        intent, PendingIntent.FLAG_ONE_SHOT)
                view.setOnClickPendingIntent(R.id.stop, pendingIntent)
        }

        fun clear() {
                mLastFiredTime = 0
                mBuilder = null
                mStopAction = null
                notificationManager!!.cancelAll()
        }

        companion object {
                private val id = 0x1fff
                private val CHANNEL_ID = "Recording"
                private val CHANNEL_NAME = "Screen Recorder Notifications"
        }
}
