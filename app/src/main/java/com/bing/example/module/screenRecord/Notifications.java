package com.bing.example.module.screenRecord;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.widget.RemoteViews;

import com.bing.example.R;
import com.bing.mvvmbase.base.BaseApplication;
import com.blankj.utilcode.util.LogUtils;

import static android.os.Build.VERSION_CODES.O;
import static com.bing.example.main.home.RecordHelper.ACTION_STOP;

public class Notifications extends ContextWrapper {
        private long mLastFiredTime = 0;
        private String mTime;
        final private NotificationManager mManager;
        final private int mNotificationID;
        final private String mChannelID;
        private Notification.Action mStopAction;
        private Notification.Builder mBuilder;
        private RemoteViews mRemoteViews;

        public Notifications(Context context, NotificationManager manager, int notificationID, String channelID) {
                super(context);
                mManager = manager;
                mNotificationID = notificationID;
                mChannelID = channelID;
        }

        public void recording(long timeMs) {
                if (SystemClock.elapsedRealtime() - mLastFiredTime < 1000) {
                        return;
                }
                mTime = DateUtils.formatElapsedTime(timeMs / 1000);
                if (mRemoteViews == null || timeMs == 0) {
                        mRemoteViews = createContentView();
                }
                mRemoteViews.setTextViewText(R.id.time, mTime);
                LogUtils.i("recording:" + mTime);
                Notification notification = getBuilder().build();
                mManager.notify(mNotificationID, notification);
                mLastFiredTime = SystemClock.elapsedRealtime();
        }

        private Notification.Builder getBuilder() {
                if (mBuilder == null) {
                        Notification.Builder builder = new Notification.Builder(this)
                                .setContentTitle("Recording...")
                                .setOngoing(true)
                                .setLocalOnly(true)
                                .setOnlyAlertOnce(true)
                                .addAction(stopAction())
                                .setWhen(System.currentTimeMillis())
                                .setContent(mRemoteViews)
                                .setSmallIcon(R.drawable.ic_stat_recording);
                        if (Build.VERSION.SDK_INT >= O) {
                                builder.setChannelId(mChannelID)
                                        .setUsesChronometer(true);
                        }
                        mBuilder = builder;
                }
                return mBuilder;
        }

        private Notification.Action stopAction() {
                if (mStopAction == null) {
                        Intent intent = new Intent(ACTION_STOP).setPackage(getPackageName());
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1,
                                intent, PendingIntent.FLAG_ONE_SHOT);
                        mStopAction = new Notification.Action(android.R.drawable.ic_media_pause, "Stop", pendingIntent);
                }
                return mStopAction;
        }

        private RemoteViews createContentView() {
                final RemoteViews view = new RemoteViews(BaseApplication.sContext.getPackageName(), R.layout.layout_notification);
                setCommonView(view);
                setCommonClickPending(view);
                return view;
        }

        // 图片，歌名，艺术家，播放按钮，下一曲按钮，关闭按钮
        private void setCommonView(RemoteViews view) {
                view.setTextViewText(R.id.time, mTime);
        }

        // 播放或暂停，下一曲，关闭
        private void setCommonClickPending(RemoteViews view) {
                Intent intent = new Intent(ACTION_STOP).setPackage(getPackageName());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1,
                        intent, PendingIntent.FLAG_ONE_SHOT);
                view.setOnClickPendingIntent(R.id.stop, pendingIntent);
        }

        public void clear() {
                mLastFiredTime = 0;
                mBuilder = null;
                mStopAction = null;
                mManager.cancelAll();
        }
}
