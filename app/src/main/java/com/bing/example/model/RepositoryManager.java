package com.bing.example.model;


import com.bing.example.app.ScreenRecordApplication;
import com.bing.example.model.db.AppDatabase;
import com.bing.example.model.entity.VideoInfo;

import java.util.List;

import androidx.lifecycle.LiveData;

public class RepositoryManager {
        private static RepositoryManager sInstance;
        private final AppDatabase mDatabase;

        private RepositoryManager() {
                mDatabase = AppDatabase.getInstance(ScreenRecordApplication.getContext());
        }

        public static RepositoryManager instance() {
                if (sInstance == null) {
                        synchronized (RepositoryManager.class) {
                                if (sInstance == null) {
                                        sInstance = new RepositoryManager();
                                }
                        }
                }
                return sInstance;
        }

        public LiveData<List<VideoInfo>> loadVideosLiveData() {
                return mDatabase.videoDao().loadAllVideosLiveData();
        }

        public void insertVideo(VideoInfo videoInfo) {
                mDatabase.videoDao().insert(videoInfo);
        }

        public void deleteVideo(List<VideoInfo> videoInfos) {
                mDatabase.videoDao().deleteVideo(videoInfos);
        }
}
