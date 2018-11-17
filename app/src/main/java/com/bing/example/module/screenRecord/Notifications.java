/*
 * Copyright (c) 2017 Yrom Wang <http://www.yrom.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bing.example.module.screenRecord;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
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
import com.bing.example.app.ScreenRecordApplication;
import com.blankj.utilcode.util.LogUtils;

import static android.os.Build.VERSION_CODES.O;
import static com.bing.example.main.activity.MainActivity.ACTION_STOP;

/**
 * @author yrom
 * @version 2017/12/1
 */
public class Notifications extends ContextWrapper {
        private static final int id = 0x1fff;
        private static final String CHANNEL_ID = "Recording";
        private static final String CHANNEL_NAME = "Screen Recorder Notifications";

        private long mLastFiredTime = 0;
        private String mTime;
        private NotificationManager mManager;
        private Notification.Action mStopAction;
        private Notification.Builder mBuilder;
        private RemoteViews mRemoteViews;

        public Notifications(Context context) {
                super(context);
                if (Build.VERSION.SDK_INT >= O) {
                        createNotificationChannel();
                }
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
                getNotificationManager().notify(id, notification);
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
                                builder.setChannelId(CHANNEL_ID)
                                        .setUsesChronometer(true);
                        }
                        mBuilder = builder;
                }
                return mBuilder;
        }

        @TargetApi(O)
        private void createNotificationChannel() {
                NotificationChannel channel =
                        new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
                channel.setShowBadge(false);
                getNotificationManager().createNotificationChannel(channel);
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
                final RemoteViews view = new RemoteViews(ScreenRecordApplication.getContext().getPackageName(), R.layout.layout_notification);
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
                getNotificationManager().cancelAll();
        }

        NotificationManager getNotificationManager() {
                if (mManager == null) {
                        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                }
                return mManager;
        }
}
